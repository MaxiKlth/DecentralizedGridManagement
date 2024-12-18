package net.peak.agent.energyTradingAgent;

import java.io.IOException;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.enflexit.jade.behaviour.AbstractTimingBehaviour.ExecutionTiming;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import net.peak.agent.energyTradingAgent.InternalDataModel.Storage;
import net.peak.agent.energyTradingAgent.behaviour.ForecastOwnPowerBalance;
import net.peak.agent.energyTradingAgent.behaviour.MessageReceiveBehaviour;
import net.peak.agent.energyTradingAgent.behaviour.OfferEnergyOptimization;
import net.peak.datamodel.communication.EnergyResult;
import net.peak.datamodel.communication.EnergyTransaction;
import net.peak.datamodel.communication.LongValue;
import net.peak.datamodel.communication.OrderBook;
import net.peak.datamodel.communication.PeakCommunicationOntology;
import net.peak.datamodel.configuration.PeakConfiguration;
import net.peak.datamodel.simulation.SimulationEnvironmentConnector;
import net.peak.topology.Node;
import net.peak.topology.Topology;

/**
 * The EnergyTradingAgent class is responsible for managing energy trading operations.
 * It handles interactions with the market, manages the agent's internal data model,
 * and coordinates energy offers and forecasts.
 */
public class EnergyTradingAgent extends Agent {

    private static final long serialVersionUID = 5295772799818807209L;
    
    public static final String COMPONENT_TYPE_MARKET_AGENT = "EnergyTradingAgent";
    
    public Topology topology;  // Object for running Dijkstra algorithm for shortest paths
    public NodeToSerialize nodeToSend = new NodeToSerialize(); 
    public List<AID> gridNodes = new ArrayList<>(); // List of all nodes in the grid
    public List<Node> prioProvide = new ArrayList<>(); // Priority list for providing nodes
    public List<Node> prioConsumption = new ArrayList<>(); // Priority list for consuming nodes
    
    private MessageReceiveBehaviour messageReceiveBehaviour; // Behavior for receiving messages
    private OfferEnergyOptimization putOfferEnergyOptimization; // Behavior for optimizing energy offers
    
    private InternalDataModel internalDataModel; // Internal data model of the agent
    private SimulationEnvironmentConnector simulationEnvironmentConnector; // Connector for simulation environment
    private OrderBook orderBook; // Order book for managing orders
    private int tradingCyclingInSeconds; // Trading cycle duration in seconds
    private int tradingIntervallInSeconds; // Trading interval duration in seconds
    private ForecastOwnPowerBalance putForecastOwnPowerBalance; // Behavior for forecasting power balance

    /**
     * The setup method initializes the agent, including setting up behaviors,
     * connecting to data files, and establishing initial energy resources.
     */
    protected void setup() {
        // --- Get the market cycle time in seconds -------------------------------
        PeakConfiguration peakConfiguration = new PeakConfiguration();
        this.tradingCyclingInSeconds = peakConfiguration.tradingCyclingInSeconds;
        this.tradingIntervallInSeconds = peakConfiguration.tradingIntervallInSeconds;

        // Initialize the internal data model with grid node settings
        internalDataModel = new InternalDataModel(this);
        internalDataModel.setGridNodeMax(peakConfiguration.setNumberNodes());

        try {
            Thread.sleep(3000); // Delay for initialization
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // --- Register Language and PEAK Ontology --------------------------------
        this.getContentManager().registerLanguage(this.getAgentCodec());
        this.getContentManager().registerOntology(this.getPeakOntology());
        
        // --- Start cyclic message receive behaviour -----------------------------
        this.addBehaviour(this.getMessageReceiveBehaviour());
        
        // Set target agent for communication
        this.internalDataModel.setEnergyAMDAID(peakConfiguration.getTargetAID());

        // Send own AID to energyAMS to get list of other AIDs back
        ACLMessage informMessage = new ACLMessage(ACLMessage.INFORM);
        informMessage.addReceiver(peakConfiguration.getTargetAID());
        informMessage.setContent("This is an INFORM message.");
        this.send(informMessage);
        
        try {
            Thread.sleep(3000); // Additional delay for message exchange
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Establish connection to data files using paths from configuration
        this.internalDataModel.setMetaFolderPathInputData(peakConfiguration.getMetafolderpathinputdata());
        this.internalDataModel.setMetaFolderPathResults(peakConfiguration.getMetafolderpathresults());
        
        // Set simulation parameters from configuration
        this.internalDataModel.setTimeResolution(peakConfiguration.getSimulationResolutionInMin());
        this.internalDataModel.setMaxPeriodNumber(peakConfiguration.getAmountPeriods());
        this.internalDataModel.setSimulationTypeEnum(peakConfiguration.getSimulationTypeEnum());
        this.internalDataModel.setCongestionManagementActive(peakConfiguration.congestionManagementActive);

        // Start trading and forecasting behaviors
        this.getPutTradingCyclingTimingBehaviour().start();
        this.getPutTradingCyclingTimingBehaviour().setDebug(false);

        this.getForecastOwnPowerBalance().start();
        this.getForecastOwnPowerBalance().setDebug(false);
        
        // Initialize topology and network nodes
        topology = new Topology(this);
        topology.putNode(this.getLocalName()); // Add the agent's own node
        topology.importData(this.getLocalName()); // Import data for the node
        
        Node n = topology.getNode(getLocalName()); // Get the node for this agent
        nodeToSend.fillData(n); // Prepare node data for transmission
        
        this.internalDataModel.setConsumptionMultiplicator(1); // Set consumption multiplier
        
        // Find other nodes in the network and send broadcast
        gridNodes = othersAreOutThere(); 
        sendBroadcast(gridNodes, nodeToSend);
        
        // Set storage capacity and efficiency
        Storage storage = this.internalDataModel.getStorage();
        storage.setStorageCapacity(nodeToSend.getEnergyStorageCapacity());
        storage.setStorageEfficiency(1);
        
        // Set initial energy resources
        setInitialEnergyRessources();
    }
    
    /**
     * Method called when the agent is terminated.
     * Stops active behaviors before shutdown.
     */
    @Override
    protected void takeDown() {
        this.getPutTradingCyclingTimingBehaviour().stop();
        this.getForecastOwnPowerBalance().stop();
    }
    
    /**
     * Transforms a date string into an OffsetDateTime object.
     * @param date the date string
     * @return the offset date time
     */
    public OffsetDateTime transformStringtoTimeDate (String date) {
        return OffsetDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss"));
    }

    /**
     * Transforms a LongValue timestamp into an OffsetDateTime object.
     * @param timeSlotStart the time slot start
     * @return the offset date time
     */
    public OffsetDateTime transformLongValuetoTimeDate(LongValue timeSlotStart) {
        long timestamp = timeSlotStart.getLongValue();
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    /**
     * Gets the codec used by this agent for encoding/decoding messages.
     * @return the agent codec
     */
    public Codec getAgentCodec() {
        return new SLCodec();
    }

    /**
     * Gets the PEAK communication ontology used by this agent.
     * @return the peak ontology
     */
    public Ontology getPeakOntology() {
        return PeakCommunicationOntology.getInstance();
    }

    /**
     * Gets the behavior responsible for receiving messages.
     * @return the message receive behaviour
     */
    private MessageReceiveBehaviour getMessageReceiveBehaviour() {
        if (messageReceiveBehaviour == null) {
            messageReceiveBehaviour = new MessageReceiveBehaviour(this);
        }
        return messageReceiveBehaviour;
    }

    /**
     * Gets the internal data model of the agent.
     * @return the internal data model
     */
    public InternalDataModel getInternalDataModel() {
        if (internalDataModel == null) {
            internalDataModel = new InternalDataModel(this);
        }
        return internalDataModel;
    }

    /**
     * Gets the simulation environment connector for the agent.
     * @return the simulation environment connector
     */
    public SimulationEnvironmentConnector getSimulationEnvironmentConnector() {
        if (simulationEnvironmentConnector == null) {
            simulationEnvironmentConnector = new SimulationEnvironmentConnector(this);
        }
        return simulationEnvironmentConnector;
    }

    /**
     * Gets the current time in milliseconds.
     * @return the current time in milliseconds
     */
    public long getTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Gets the order book used by the agent for managing trades.
     * @return the order book
     */
    public OrderBook getOrderBook() {
        if (orderBook == null) {
            orderBook = new OrderBook();
        }
        return orderBook;
    }
    
    /**
     * Sets the order book for the agent.
     * @param orderBook the new order book
     */
    public void setOrderBook(OrderBook orderBook) {
        this.orderBook = orderBook;
    }

    /**
     * Converts a member ID to an AID (Agent Identifier).
     * @param peakMemberID the member ID
     * @return the corresponding AID
     */
    public AID transformMemberID2AID(int peakMemberID) {
        String s = Integer.toString(peakMemberID);
        AID aid = new AID(s, AID.ISLOCALNAME);
        if (gridNodes.contains(aid)) {
            return aid;
        } else return null;
    }
    
    /**
     * Checks if the agent is designated as an offer agent (e.g., has PV).
     * @return true if the agent is an offer agent, false otherwise
     */
    public boolean checkIfOfferAgent() {
        return this.internalDataModel.isPVBoolean();
    }

    /**
     * Gets the behavior responsible for managing the trading cycle.
     * @return the trading cycle behavior
     */
    public OfferEnergyOptimization getPutTradingCyclingTimingBehaviour() {
        if (putOfferEnergyOptimization == null) {
            Duration interval = Duration.ofMillis(this.tradingCyclingInSeconds);
            Duration offset = Duration.ofMillis(this.tradingCyclingInSeconds / 3);
            ExecutionTiming executionTiming = ExecutionTiming.StartFrom;
            
            putOfferEnergyOptimization = new OfferEnergyOptimization(this, interval, offset, executionTiming);
            putOfferEnergyOptimization.setWaitTimeBase(5000);
            putOfferEnergyOptimization.setWaitTimeMinFactor(1);
            putOfferEnergyOptimization.setWaitTimeMaxFactor(30);
        }
        return putOfferEnergyOptimization;
    }

    /**
     * Converts an AID (Agent Identifier) to a member ID.
     * @param name the AID name
     * @return the corresponding member ID
     */
    public int transformAID2MemberID(String name) {
        return Integer.parseInt(name);
    }
    
    /**
     * Searches for other energy trading agents in the system.
     * @return a list of AIDs representing other energy trading agents
     */
    private List<AID> othersAreOutThere() {
        AMSAgentDescription[] agents = null;
        try {
            SearchConstraints c = new SearchConstraints();
            c.setMaxResults((long) 300); // Maximum results to ensure all agents are found
            agents = AMSService.search(this, new AMSAgentDescription(), c);
        } catch (Exception e) {
            System.out.println("Problem searching AMS: " + e);
            e.printStackTrace();
        }

        List<AID> nodes = new ArrayList<>();
        for (AMSAgentDescription agentDesc : agents) {
            AID agentID = agentDesc.getName();
            if (isEnergyTradingAgent(agentID)) {
                nodes.add(agentID);
            }
        }
        return nodes;
    }

    /**
     * Checks if the given AID belongs to an energy trading agent.
     * @param agentID the AID to check
     * @return true if the AID belongs to an energy trading agent, false otherwise
     */
    private boolean isEnergyTradingAgent(AID agentID) {
        String localName = agentID.getLocalName();
        if (isNumeric(localName)) {
            int aidMsg = Integer.parseInt(localName);
            return aidMsg >= 1000;
        } else {
            return false;
        }
    }

    /**
     * Checks if a string is numeric.
     * @param str the string to check
     * @return true if the string is numeric, false otherwise
     */
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sends a broadcast message to all nodes in the grid.
     * @param gridNodes the list of nodes to send the message to
     * @param nodeToSend the node data to be broadcasted
     */
    private void sendBroadcast(List<AID> gridNodes, NodeToSerialize nodeToSend) {
        ACLMessage SendNode = new ACLMessage(ACLMessage.INFORM);
        SendNode.setConversationId("Add_Node");
        SendNode.setSender(this.getAID());
        try {
            SendNode.setContentObject((Serializable) nodeToSend);
        } catch (IOException e) {
            System.out.println("Abbruch");
            e.printStackTrace();
        }
        for (AID gridNode : gridNodes) {
            if (!gridNode.equals(this.getAID()) && gridNode != null) {
                SendNode.addReceiver(gridNode);
            }
        }
        this.send(SendNode);
    }
    
    /**
     * Gets the behavior responsible for forecasting the agent's own power balance.
     * @return the forecasting behavior
     */
    public ForecastOwnPowerBalance getForecastOwnPowerBalance() {
        if (putForecastOwnPowerBalance == null) {
            Duration interval = Duration.ofMillis(this.tradingIntervallInSeconds);
            Duration offset = Duration.ofMillis(this.tradingIntervallInSeconds);
            ExecutionTiming executionTiming = ExecutionTiming.StartFrom;
            
            putForecastOwnPowerBalance = new ForecastOwnPowerBalance(this, interval, offset, executionTiming);
            putForecastOwnPowerBalance.setWaitTimeBase(5000);
            putForecastOwnPowerBalance.setWaitTimeMinFactor(1);
            putForecastOwnPowerBalance.setWaitTimeMaxFactor(30);
        }
        return putForecastOwnPowerBalance;
    }

    /**
     * Updates the open energy amount available for trading.
     * @return the updated open energy amount
     */
    public float updateOpenEnergyAmount() {
        float openEnergyAmount = this.getInternalDataModel().getInitialEnergyAmountOfIntervall();
        float temporaryOpenEnergyAmount = openEnergyAmount;
        Map<String, EnergyTransaction> openEnergyOffers = this.getInternalDataModel().getListOpenEnergyOffers();
        for (EnergyTransaction energyTransaction : openEnergyOffers.values()) {
            if (energyTransaction != null) temporaryOpenEnergyAmount -= energyTransaction.getEnergyAmountFloat();
        }
        return temporaryOpenEnergyAmount;
    }
    
    /**
     * Updates the agent's own energy offer based on the current trades.
     * @return the updated own energy offer amount
     */
    public float updateOwnEnergyOffer() {
        float openOwnEnergyAmount = this.getInternalDataModel().getInitialEnergyAmountOfIntervall();
        float temporaryOpenEnergyAmount = openOwnEnergyAmount;
        int actualPeriod = this.getInternalDataModel().getPeriodNumber();
        Map<String, EnergyResult> resultList = this.getInternalDataModel().getResultList();
        for (Map.Entry<String, EnergyResult> entry : resultList.entrySet()) {
            EnergyResult energyResult = entry.getValue();
            if (energyResult.getActualPeriod() == actualPeriod) {
                temporaryOpenEnergyAmount -= energyResult.getDeliveredEnergyFloat();
            }
        }
        return temporaryOpenEnergyAmount;
    }
    
    /**
     * Sets the initial energy resources for the agent based on predefined percentages.
     * The agent is randomly assigned as a PV, HeatPump, or EV agent.
     */
    public void setInitialEnergyRessources() {
        int maxAmountNodes = this.internalDataModel.getGridNodeMax();
        int agentName = Integer.parseInt(this.getLocalName());

        PeakConfiguration peakConfiguration = new PeakConfiguration();
        double percentagePV = peakConfiguration.percentagePV;
        double percentageHeatPump = peakConfiguration.percentageHeatPump;
        double percentageEV = peakConfiguration.percentageEV;

        // Create a list of nodes
        List<Integer> nodeList = new ArrayList<>();
        for (int i = 1; i <= maxAmountNodes; i++) {
            nodeList.add(i * 1000);
        }

        long seedPV = 12345L;
        long seedHeatPump = 67890L;
        long seedEV = 54321L;
        
        // Shuffle for PV assignment
        Collections.shuffle(nodeList, new Random(seedPV));
        int pvNodes = (int) Math.ceil(maxAmountNodes * percentagePV);
        boolean isPVAgent = checkAgentInList(agentName, nodeList, pvNodes);
        
        // Shuffle for HeatPump assignment
        Collections.shuffle(nodeList, new Random(seedHeatPump));
        int heatPumpNodes = (int) Math.ceil(maxAmountNodes * percentageHeatPump);
        boolean isHeatPumpAgent = checkAgentInList(agentName, nodeList, heatPumpNodes);
        
        // Shuffle for EV assignment
        Collections.shuffle(nodeList, new Random(seedEV));
        int EVNodes = (int) Math.ceil(maxAmountNodes * percentageEV);
        boolean isEVAgent = checkAgentInList(agentName, nodeList, EVNodes);
        
        this.internalDataModel.setHeatPumpBoolean(isHeatPumpAgent);
        this.internalDataModel.setPVBoolean(isPVAgent);
        this.internalDataModel.setEVBoolean(isEVAgent);
    }
    
    /**
     * Checks if an agent is in the list of nodes for a specific resource type (e.g., PV).
     * @param agentName the agent's name
     * @param nodeList the list of nodes
     * @param limit the limit of nodes for that resource type
     * @return true if the agent is in the list, false otherwise
     */
    public static boolean checkAgentInList(int agentName, List<Integer> nodeList, int limit) {
        return nodeList.subList(0, limit).contains(agentName);
    }
    
    /**
     * Updates the addresses of the agents in the grid.
     * @return the list of updated AIDs
     */
    public List<AID> updateAgentAddresses() {
        PeakConfiguration peakConfiguration = new PeakConfiguration();
        
        int numberAgent = Integer.parseInt(this.getLocalName());

        try {
            Thread.sleep(numberAgent / 1000); // Delay based on agent number
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (this.internalDataModel.getOpenGridNodes().size() < this.internalDataModel.getGridNodeMax()) {
            ACLMessage informMessage = new ACLMessage(ACLMessage.INFORM);
            informMessage.addReceiver(peakConfiguration.getTargetAID());
            informMessage.setContent("This is an INFORM message.");
            this.send(informMessage);
        }
        
        return this.internalDataModel.getOpenGridNodes();
    }
}
