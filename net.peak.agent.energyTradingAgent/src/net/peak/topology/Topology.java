package net.peak.topology;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import lombok.Getter;
import net.peak.agent.energyTradingAgent.EnergyTradingAgent;
import net.peak.agent.energyTradingAgent.NodeToSerialize;

@Getter
public class Topology {

    private Map<String, Node> nodeMap = new HashMap<>(); // HashMap to store nodes with their IDs
    private Double MaxSource;
    private Double MaxStorage;
    private Double MaxDistance;
    private Double MaxConsumption;
    private boolean networkReachable = false;
    private String MyNode;
    private EnergyTradingAgent energyTradingAgent;

    /**
     * Constructor to initialize the Topology with an EnergyTradingAgent.
     *
     * @param energyTradingAgent The agent managing the energy trading.
     */
    public Topology(EnergyTradingAgent energyTradingAgent) {
        this.energyTradingAgent = energyTradingAgent;
    }

    /**
     * Adds a node to the topology with the specified name.
     *
     * @param name The name of the node to add.
     */
    public void putNode(String name) {
        this.MyNode = name;
        nodeMap.put(name, new Node(name));
    }

    /**
     * Imports data for the node with the specified name from a CSV file.
     *
     * @param name The name of the node to import data for.
     */
    public void importData(String name) {
        ReadAndWrite read = new ReadAndWrite(this.energyTradingAgent);
        this.nodeMap = read.readCSV(this.nodeMap, name);
    }

    /**
     * Retrieves the node with the specified name from the topology.
     *
     * @param name The name of the node to retrieve.
     * @return The node with the specified name, or null if not found.
     */
    public Node getNode(String name) {
        return nodeMap.getOrDefault(name, null);
    }

    /**
     * Returns the number of nodes in the topology.
     *
     * @return The size of the node map.
     */
    public int getAmountNodes() {
        return nodeMap.size();
    }

    /**
     * Adds a node to the grid based on the received ACLMessage.
     *
     * @param msg  The ACLMessage containing the node information.
     * @param name The name of the node to add.
     * @return True if the node was new to the system, false otherwise.
     */
    public boolean add_node(ACLMessage msg, String name) {
        boolean answer = addToGrid(msg);
        calculateShortestPath(nodeMap.get(MyNode));
        this.networkReachable = checkReachable();
        return answer;
    }

    /**
     * Removes a node from the grid based on the received ACLMessage.
     *
     * @param msg  The ACLMessage containing the node information.
     * @param name The name of the node to remove.
     */
    public void remove_node(ACLMessage msg, String name) {
        removeFromGrid(msg);
        calculateShortestPath(nodeMap.get(MyNode));
        this.networkReachable = checkReachable();
        if (!networkReachable) {
            System.out.println(MyNode + ": The network is no longer fully reachable.");
        }
    }

    /**
     * Sorts the nodes based on their provide scores and returns the sorted list.
     *
     * @return A sorted list of nodes by provide score.
     */
    public List<Node> sortList() {
        checkMaximums();
        calcScores();
        List<Node> nodeByScore = new ArrayList<>(nodeMap.values());
        nodeByScore.sort(Comparator.comparing(Node::getProvideScore).reversed());
        nodeByScore.removeIf(node -> node.getName().equalsIgnoreCase(MyNode));
        return nodeByScore;
    }

    /**
     * Returns a randomly shuffled list of nodes.
     *
     * @return A shuffled list of nodes.
     */
    public List<Node> sortListRandom() {
        List<Node> nodeByScore = new ArrayList<>(nodeMap.values());
        Collections.shuffle(nodeByScore);
        nodeByScore.removeIf(node -> node.getName().equalsIgnoreCase(MyNode));
        return nodeByScore;
    }

    /**
     * Sorts the nodes based on their consumption scores and returns the sorted list.
     *
     * @return A sorted list of nodes by consumption score.
     */
    public List<Node> sortList2() {
        checkMaximums();
        calcScores();
        List<Node> nodeByScore = new ArrayList<>(nodeMap.values());
        nodeByScore.sort(Comparator.comparing(Node::getConsumptionScore).reversed());
        nodeByScore.removeIf(node -> node.getName().equalsIgnoreCase(MyNode));
        return nodeByScore;
    }

    /**
     * Adds a node to the grid based on the received ACLMessage.
     *
     * @param msg The ACLMessage containing the node information.
     * @return True if the node was new to the system, false otherwise.
     */
    private boolean addToGrid(ACLMessage msg) {
        NodeToSerialize tempNode;
        try {
            tempNode = (NodeToSerialize) msg.getContentObject(); // Deserialize node data from the message
        } catch (UnreadableException e) {
            e.printStackTrace();
            return false;
        }
        String name = tempNode.getName();
        boolean answer = tempNode.isNewToSystem();
        nodeMap.putIfAbsent(name, new Node(name)); // Add the node if it's not already in the map
        Node node = nodeMap.get(name);
        node.setEnergySourceCapacity(tempNode.getEnergySourceCapacity());
        node.setEnergyStorageCapacity(tempNode.getEnergyStorageCapacity());
        node.setEnergyConsumption(tempNode.getEnergyConsumption());
        for (Map.Entry<String, Double> entry : tempNode.getAdjacentNodes().entrySet()) {
            nodeMap.putIfAbsent(entry.getKey(), new Node(entry.getKey()));
            node.addAdjacentNode(nodeMap.get(entry.getKey()), entry.getValue());
            nodeMap.get(entry.getKey()).addAdjacentNode(node, entry.getValue());
        }
        nodeMap.values().forEach(n -> n.setDistance(Double.MAX_VALUE));
        return answer;
    }

    /**
     * Calculates the shortest path from the source node to all other nodes.
     *
     * @param source The source node to calculate paths from.
     */
    private void calculateShortestPath(Node source) {
        source.setDistance(0.0);
        Set<Node> settledNodes = new HashSet<>();
        Queue<Node> unsettledNodes = new PriorityQueue<>(Collections.singleton(source));
        while (!unsettledNodes.isEmpty()) {
            Node currentNode = unsettledNodes.poll();
            currentNode.getAdjacentNodes().entrySet().stream()
                    .filter(entry -> !settledNodes.contains(entry.getKey()))
                    .forEach(entry -> {
                        evaluateDistanceAndPath(entry.getKey(), entry.getValue(), currentNode);
                        unsettledNodes.add(entry.getKey());
                    });
            settledNodes.add(currentNode);
        }
    }

    /**
     * Evaluates the distance and path for a given adjacent node.
     *
     * @param adjacentNode The adjacent node to evaluate.
     * @param edgeWeight   The weight of the edge connecting the nodes.
     * @param sourceNode   The source node from which the path is being evaluated.
     */
    private void evaluateDistanceAndPath(Node adjacentNode, Double edgeWeight, Node sourceNode) {
        Double newDistance = sourceNode.getDistance() + edgeWeight;
        if (newDistance < adjacentNode.getDistance()) {
            adjacentNode.setDistance(newDistance);
            adjacentNode.setShortestPath(Stream.concat(sourceNode.getShortestPath().stream(), Stream.of(sourceNode)).collect(Collectors.toList()));
        }
    }

    /**
     * Removes a node from the grid based on the received ACLMessage.
     *
     * @param msg The ACLMessage containing the node information.
     */
    private void removeFromGrid(ACLMessage msg) {
        NodeToSerialize tempNode;
        try {
            tempNode = (NodeToSerialize) msg.getContentObject(); // Deserialize node data from the message
        } catch (UnreadableException e) {
            e.printStackTrace();
            return;
        }
        String name = tempNode.getName();
        for (Node node : this.nodeMap.values()) {
            node.removeAdjacentNode(nodeMap.get(name));
            node.setDistance(Double.MAX_VALUE);
        }
        nodeMap.remove(name);
    }

    /**
     * Checks if all nodes in the network are reachable.
     *
     * @return True if all nodes are reachable, false otherwise.
     */
    private boolean checkReachable() {
        return nodeMap.values().stream().noneMatch(node -> node.getDistance() == Double.MAX_VALUE);
    }

    /**
     * Checks and sets the maximum values for source capacity, storage, distance, and consumption.
     */
    private void checkMaximums() {
        this.MaxSource = nodeMap.values().stream().map(Node::getEnergySourceCapacity).max(Double::compare).orElse(0.0);
        this.MaxStorage = nodeMap.values().stream().map(Node::getEnergyStorageCapacity).max(Double::compare).orElse(0.0);
        this.MaxDistance = nodeMap.values().stream().filter(node -> node.getDistance() != Double.MAX_VALUE)
                .map(Node::getDistance).max(Double::compare).orElse(0.0);
        this.MaxConsumption = nodeMap.values().stream().map(Node::getEnergyConsumption).max(Double::compare).orElse(0.0);
    }

    /**
     * Calculates the provide and consumption scores for all nodes.
     */
    private void calcScores() {
        nodeMap.values().forEach(node -> node.calcProvideScore(MaxDistance, MaxSource, MaxStorage, MaxConsumption));
        ReadAndWrite status = new ReadAndWrite(this.energyTradingAgent);
        status.writeStatus(this.MyNode, this.nodeMap);
    }

}
