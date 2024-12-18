package net.peak.topology;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Node implements Comparable<Node> {

    private String name;
    private double energySourceCapacity = 0;
    private double energyStorageCapacity = 0;
    private double energyConsumption;
    private double distance = Double.MAX_VALUE;
    private double specificResistanceAl = 37.7 * 10 * Math.exp(6);
    private double cableProfilArea = 0;
    private double EVBoolean = 0;
    private double HeatPumpBoolean = 0;

    private double ConsumptionScore;
    private double ProvideScore;
    private double factor_src = 0.5;
    private double factor_str = 0.5;
    private double factor_con = 0.5;

    private List<Node> shortestPath = new LinkedList<>();
    private Map<Node, Double> adjacentNodes = new HashMap<>();

    /**
     * Adds an adjacent node to this node with the specified weight.
     * The weight is calculated using the specific resistance of aluminum and the cable profile area.
     *
     * @param node   The adjacent node to add.
     * @param weight The weight (distance) to the adjacent node.
     */
    public void addAdjacentNode(Node node, double weight) {
        weight = specificResistanceAl * weight / this.cableProfilArea;
        adjacentNodes.put(node, weight);
    }

    /**
     * Removes an adjacent node from this node's list of adjacent nodes.
     *
     * @param node The adjacent node to remove.
     */
    public void removeAdjacentNode(Node node) {
        adjacentNodes.remove(node);
    }

    /**
     * Constructor to create a Node with a specified name.
     *
     * @param s The name of the node.
     */
    public Node(String s) {
        this.name = s;
    }

    /**
     * Compares this node to another node based on their distances.
     *
     * @param node The node to compare to.
     * @return A negative integer, zero, or a positive integer as this node's distance
     *         is less than, equal to, or greater than the specified node's distance.
     */
    @Override
    public int compareTo(Node node) {
        return Double.compare(this.distance, node.getDistance());
    }

    /**
     * Updates the factor parameters. This method is a placeholder for future implementations
     * that may update factors based on time, weather, or other parameters.
     */
    private void updateFactorParameter() {
        // TODO: Update factor parameters based on time and weather data
    }

    /**
     * Calculates the provide and consumption scores for this node.
     * The scores are based on the node's energy capacities, consumption, distance, and weighting factors.
     *
     * @param maxDis         The maximum distance in the network.
     * @param maxSource      The maximum energy source capacity in the network.
     * @param maxStorage     The maximum energy storage capacity in the network.
     * @param maxConsumption The maximum energy consumption in the network.
     */
    public void calcProvideScore(Double maxDis, Double maxSource, Double maxStorage, Double maxConsumption) {
        updateFactorParameter();
        if (maxDis == 0 || maxSource == 0 || maxStorage == 0) {
            // Log or handle error for zero values
        } else {
            double a = (energySourceCapacity / maxSource);
            double b = (energyStorageCapacity / maxStorage);
            double c = (energyConsumption / maxConsumption);
            double d = (1 - (distance / maxDis));

            ProvideScore = 100 * (a * factor_src + b * factor_str) * d;
            ConsumptionScore = 100 * (c * factor_con + b * factor_str) * d;

            if (distance == maxDis) {
                ProvideScore = 0.01;
                ConsumptionScore = 0.01;
            }
        }
    }

    /**
     * Sets the EV (Electric Vehicle) boolean status for this node.
     *
     * @param parseDouble The value to set for EV status.
     */
    public void setEVBoolean(double parseDouble) {
        this.EVBoolean = parseDouble;
    }

    /**
     * Sets the Heat Pump boolean status for this node.
     *
     * @param parseDouble The value to set for Heat Pump status.
     */
    public void setHeatPumpBoolean(double parseDouble) {
        this.HeatPumpBoolean = parseDouble;
    }
}
