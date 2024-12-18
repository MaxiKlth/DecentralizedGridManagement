package net.peak.agent.flexibilityTradingAgent;

import java.time.Duration;
import java.util.HashMap;

import de.enflexit.jade.behaviour.AbstractTimingBehaviour.ExecutionTiming;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import net.peak.agent.flexibilityTradingAgent.behaviour.MessageReceiveBehaviour;
import net.peak.agent.flexibilityTradingAgent.behaviour.OptimizeGridCongestion;
import net.peak.datamodel.communication.EnergyResult;
import net.peak.datamodel.communication.PeakCommunicationOntology;
import net.peak.datamodel.communication.PutEnergyResult;
import net.peak.datamodel.configuration.PeakConfiguration;
import net.peak.datamodel.simulation.SimulationEnvironmentConnector;

/**
 * The Class CongestionManagingAgent.
 *
 * Manages grid congestion by optimizing energy distribution and communication
 * between agents in the energy market.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class CongestionManagingAgent extends Agent {

    private static final long serialVersionUID = 5295772799818807209L;

    public static final String COMPONENT_TYPE_MARKET_AGENT = "CongestionManagingAgent";

    // Behaviours and models related to the agent's functionality
    private MessageReceiveBehaviour messageReceiveBehaviour;
    private InternalDataModel internalDataModel;
    private SimulationEnvironmentConnector simulationEnvironmentConnector;
    private OptimizeGridCongestion putOfferEnergyOptimization;
    private int tradingCyclingInSeconds;

    /* (non-Javadoc)
     * @see jade.core.Agent#setup()
     */
    @Override
    protected void setup() {
        // --- Register Language and PEAK Ontology --------------------------------
        this.getContentManager().registerLanguage(this.getAgentCodec());
        this.getContentManager().registerOntology(this.getPeakOntology());

        // Initialize message receiving behaviour
        this.addBehaviour(this.getMessageReceiveBehaviour());

        // Debug settings for specific agents
        if(this.getLocalName().equals("1")) this.getInternalDataModel().setLeader(true);

        // Set default values for X, Z, and Lambda
        this.getInternalDataModel().setPreviousX(0.5);
        this.getInternalDataModel().setPreviousZ(0.5);
        this.getInternalDataModel().setPreviousLambda(0.005);

        // Introduce a delay to ensure all configurations are set up
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Initialize trading cycle and other configurations
        PeakConfiguration peakConfiguration = new PeakConfiguration();
        this.tradingCyclingInSeconds = peakConfiguration.tradingCyclingInSeconds;
        this.internalDataModel.setEnergyAMDAID(peakConfiguration.getTargetAID());

        // Notify the AMS of this agent's AID to receive a list of other agents
        ACLMessage informMessage = new ACLMessage(ACLMessage.INFORM);
        informMessage.addReceiver(peakConfiguration.getTargetAID());
        informMessage.setContent("This is an INFORM message.");
        this.send(informMessage);

        // Additional delay to ensure AMS processes the information
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Set maximum nodes in the grid
        this.internalDataModel.setMaxNodes(peakConfiguration.setNumberNodes());

        // Set the descent rate for optimization, adjusted with some randomness
        double decentRate = peakConfiguration.getDecenteRateADMM() / this.internalDataModel.getMaxNodes() + 0.0001 * Math.random();
        this.internalDataModel.setDecentRate(decentRate);

        // Set folder path to store results
        this.internalDataModel.setMetaFolderPath(peakConfiguration.getMetafolderpathresults());

        // Set voltage and current limits
        this.internalDataModel.setVoltage(peakConfiguration.getVoltage());
        this.internalDataModel.setMaxCurrent(peakConfiguration.getMaxCurrent());

        // Set time resolution for simulations
        this.internalDataModel.setTimeResolution(peakConfiguration.getSimulationResolutionInMin());

        // Start the cyclic grid congestion optimization behaviour
        this.getPutTradingCyclingTimingBehaviour().start();
        this.getPutTradingCyclingTimingBehaviour().setDebug(false);
    }

    /**
     * Gets the codec for this agent. Override to specify your own codec.
     * @return the agent codec
     */
    public Codec getAgentCodec() {
        return new SLCodec();
    }

    /**
     * Returns the peak ontology instance.
     * @return the peak ontology
     */
    public Ontology getPeakOntology() {
        return PeakCommunicationOntology.getInstance();
    }

    /**
     * Gets the message receive behaviour.
     * @return the message receive behaviour
     */
    private MessageReceiveBehaviour getMessageReceiveBehaviour() {
        if (messageReceiveBehaviour == null) {
            messageReceiveBehaviour = new MessageReceiveBehaviour(this);
        }
        return messageReceiveBehaviour;
    }

    /**
     * Gets the internal data model.
     * @return the internal data model
     */
    public InternalDataModel getInternalDataModel() {
        if (internalDataModel == null) {
            internalDataModel = new InternalDataModel(this);
        }
        return internalDataModel;
    }

    /**
     * Sends the adapted energy values to the EDA agents.
     * This method ensures that the agents are updated with the latest energy values
     * after grid congestion management.
     *
     * @param congestionExists flag indicating if congestion was detected
     */
    public void sendAdaptedEnergyValues2EDA(boolean congestionExists) {
        if (this.internalDataModel.getEDATradingPeriod() != this.internalDataModel.getTradingPeriod() && 
            this.internalDataModel.getTradingPeriod() != 0 && !congestionExists) {
            
            this.internalDataModel.setCMACalculationFinished(false);
            int tempoTradingPeriod = this.getInternalDataModel().getTradingPeriod();
            
        } else if (!congestionExists) {
            this.internalDataModel.setCMACalculationFinished(true);
        }

        HashMap<Integer, Double> energyValues = this.getInternalDataModel().getAdaptedTemporaryEnergyBalanceList().get(this.internalDataModel.getTradingPeriod());
        int ownAgentName = Integer.parseInt(this.getLocalName());
        EnergyResult adaptedEnergyTransaction = new EnergyResult();

        if (energyValues.containsKey(ownAgentName)) {
            adaptedEnergyTransaction.setDeliveredEnergy(energyValues.get(ownAgentName).floatValue());
        }
        adaptedEnergyTransaction.setActualPeriod(this.getInternalDataModel().getTradingPeriod());
        PutEnergyResult putEnergyResult = new PutEnergyResult();
        putEnergyResult.setEnergyResult(adaptedEnergyTransaction);
        Action action = new Action();
        action.setAction(putEnergyResult);
        action.setActor(this.getAID());
        String EDAName = String.valueOf(ownAgentName * 1000);
        AID receiverAID = new AID(EDAName, AID.ISLOCALNAME);
        this.sendACLMessage(action, receiverAID);
    }

    /**
     * Send ACL message to a specified agent.
     * 
     * @param action the action to send
     * @param receiver the receiving agent
     */
    public void sendACLMessage(Action action, Agent receiver) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setSender(this.getAID());
        msg.addReceiver(receiver.getAID());
        msg.setLanguage(this.getAgentCodec().getName());
        msg.setOntology(this.getPeakOntology().getName());

        try {
            this.getContentManager().fillContent(msg, action);
            this.send(msg);
        } catch (CodecException | OntologyException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send ACL message to a specified AID.
     *
     * @param action the action to send
     * @param receiverAID the receiver's AID
     */
    public void sendACLMessage(Action action, AID receiverAID) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setSender(this.getAID());
        msg.addReceiver(receiverAID);
        msg.setLanguage(this.getAgentCodec().getName());
        msg.setOntology(this.getPeakOntology().getName());

        try {
            this.getContentManager().fillContent(msg, action);
            this.send(msg);
        } catch (CodecException | OntologyException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get the current time in milliseconds.
     *
     * @return the current time in milliseconds
     */
    public long getTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Gets the grid congestion optimization behaviour.
     * This method ensures that the task is started at the correct time and
     * manages the timing for grid congestion optimization.
     *
     * @return the grid congestion optimization behaviour
     */
    public OptimizeGridCongestion getPutTradingCyclingTimingBehaviour() {
        if (putOfferEnergyOptimization == null) {

            Duration interval = Duration.ofMillis(this.tradingCyclingInSeconds);
            Duration offset = Duration.ofMillis(this.tradingCyclingInSeconds);
            ExecutionTiming executionTiming = ExecutionTiming.StartFrom;

            putOfferEnergyOptimization = new OptimizeGridCongestion(this, interval, offset, executionTiming);

            // Set task duration between one and 30 seconds
            putOfferEnergyOptimization.setWaitTimeBase(5000);
            putOfferEnergyOptimization.setWaitTimeMinFactor(1);
            putOfferEnergyOptimization.setWaitTimeMaxFactor(30);
        }
        return putOfferEnergyOptimization;
    }
}
