package net.peak.agent.energyTradingAgent.behaviour;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import com.sun.management.OperatingSystemMXBean;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import net.peak.agent.energyTradingAgent.EnergyTradingAgent;
import net.peak.agent.energyTradingAgent.InternalDataModel;
import net.peak.datamodel.communication.EnergyResult;
import net.peak.datamodel.communication.EnergyTransaction;
import net.peak.datamodel.communication.PutEnergyResult;
import net.peak.datamodel.communication.PutEnergyTransaction;
import net.peak.topology.ReadAndWrite;

public class MessageReceiveBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = 254907738451044781L;
    
    private EnergyTradingAgent energyTradingAgent; 
    public MessageReceiveBehaviour(EnergyTradingAgent marketAgent2) {
        this.energyTradingAgent = marketAgent2;
    }

    @Override
    public void action() {
        ACLMessage msg = energyTradingAgent.receive();
        this.energyTradingAgent.getInternalDataModel(); 
        this.energyTradingAgent.getInternalDataModel();
        
        if (msg != null) {
            // Handle messages from the EnergyAMD agent
            if (msg.getSender().getLocalName().equals(this.energyTradingAgent.getInternalDataModel().getEnergyAMDAID().getLocalName())) {
                try {
                    // Convert received message content into a list of AIDs (agents)
                    @SuppressWarnings("unchecked")
					ArrayList<AID> openGridNodes = (ArrayList<AID>) msg.getContentObject();
                    this.energyTradingAgent.getInternalDataModel().setOpenGridNodes(openGridNodes);
                    System.out.println("Agents found in: " + this.energyTradingAgent.getLocalName() + " " + openGridNodes.size());
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
            }

            if (msg.getPerformative() == ACLMessage.FAILURE) {
                System.err.println("[" + this.myAgent.getLocalName() + "] Received failure message from " + msg.getSender().getName() + ":");
                System.err.println(msg.getContent());
                return;
            }
            
            if (msg.getOntology() != null) {
            	
                Action contentAction = new Action();
                try {
                    contentAction = (Action) this.myAgent.getContentManager().extractContent(msg);
                } catch (CodecException | OntologyException e1) {
                    e1.printStackTrace();
                }
                
                if (contentAction.getAction() instanceof PutEnergyTransaction) {
                    // Handle received energy transaction
                    handleEnergyTransaction(msg, contentAction);
                    
                } else if (contentAction.getAction() instanceof PutEnergyResult) {
                    // Handle received energy result
                    handleEnergyResult(msg, contentAction);
                }
            }
        }
    }

    /**
     * Handles incoming EnergyTransaction messages and processes them based on the performative type.
     */
    private void handleEnergyTransaction(ACLMessage msg, Action contentAction) {
        float openEnergyAmount = this.energyTradingAgent.updateOpenEnergyAmount();
        PutEnergyTransaction putEnergyTransaction = (PutEnergyTransaction) contentAction.getAction();
        EnergyTransaction receivedEnergyOffer = putEnergyTransaction.getEnergyTransaction();
        
        // Calculate communication time
        double communicationTime = calculateCommunicationTime(receivedEnergyOffer.getTimeSlotStart().getLongValue());
        receivedEnergyOffer.setCommunicationTime(communicationTime);
        
        // Set asking agent and initial transaction details if missing
        if (receivedEnergyOffer.getAskingAgent() == null || receivedEnergyOffer.getInitialEnergyAmountAsked() == 0) {
            receivedEnergyOffer.setAskingAgent(this.energyTradingAgent.getAID());
            receivedEnergyOffer.setInitialEnergyAmountAsked(openEnergyAmount);
            receivedEnergyOffer.setInitialTransactionPriceAsked(this.energyTradingAgent.getInternalDataModel().getOwnEnergyOffer().getTransactionPrice());
        }
        
        // Handle the message based on its performative type
        switch (msg.getPerformative()) {
            case ACLMessage.REJECT_PROPOSAL: 
                handleRejectMessage(receivedEnergyOffer);
                break;
            case ACLMessage.ACCEPT_PROPOSAL:
                CreateEnergyResult cer = new CreateEnergyResult(receivedEnergyOffer, this.energyTradingAgent, msg.getSender(), true);
                this.energyTradingAgent.addBehaviour(cer);
                break;
            case ACLMessage.PROPOSE:
            case ACLMessage.AGREE:
                this.energyTradingAgent.getInternalDataModel().addEnergyTrade2ListEnergyOffers(receivedEnergyOffer);
                break;
            default:
                System.out.println("No OfferAnswerType sent");
                break;
        }
    }

    /**
     * Handles incoming EnergyResult messages and processes them based on the performative type.
     */
    private void handleEnergyResult(ACLMessage msg, Action contentAction) {
        PutEnergyResult putEnergyResult = (PutEnergyResult) contentAction.getAction();
        EnergyResult receivedEnergyResult = putEnergyResult.getEnergyResult();
        
        int aidMsg = Integer.parseInt(msg.getSender().getLocalName());
        if (aidMsg / 1000 >= 1) {
            // For messages from other agents
            double communicationTime = calculateCommunicationTime(receivedEnergyResult.getTimeSlotStart().getLongValue());
            receivedEnergyResult.setCommuncationTimeInMs(communicationTime);
            
    		//Set CPU und RAM Load:
            OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
   	        
            // Get CPU Load
            double cpuLoad = osBean.getProcessCpuLoad();
            receivedEnergyResult.setCpuLoad(cpuLoad);
    		
    		//Get RAM Load
    		long totalMemory = osBean.getTotalPhysicalMemorySize();
    		long freeMemory = osBean.getFreePhysicalMemorySize();
            long usedMemory = totalMemory - freeMemory;
            
            // Prozentualen Anteil des genutzten Speichers berechnen
            double usedMemoryPercentage = (double) usedMemory / totalMemory;
            receivedEnergyResult.setRamLoad(usedMemoryPercentage);
            
            switch (msg.getPerformative()) {
                case ACLMessage.ACCEPT_PROPOSAL:
                    handleAcceptedEnergyResult(receivedEnergyResult);
                    break;
                case ACLMessage.REJECT_PROPOSAL:
                    handleRejectMessage(receivedEnergyResult);
                    break;
            }
        } else {
            // For messages from internal processes
            this.energyTradingAgent.getInternalDataModel().increaseAdaptedEnergy(receivedEnergyResult.getDeliveredEnergy());
        }
    }

    /**
     * Calculate the communication time based on the message timestamp.
     */
    private double calculateCommunicationTime(long timeStampMsg) {
        long currentTime = System.currentTimeMillis();
        return timeStampMsg != 0 ? currentTime - timeStampMsg : 0;
    }

    /**
     * Handle rejection of an EnergyTransaction.
     */
    private void handleRejectMessage(EnergyTransaction receivedEnergyOffer) {
        InternalDataModel internalDataModel = this.energyTradingAgent.getInternalDataModel();
        String localTransactionID = receivedEnergyOffer.getLocalTransactionID();
        
        internalDataModel.getListOpenEnergyOffers().remove(localTransactionID);
        internalDataModel.getListEnergyOffers().remove(localTransactionID);
        
        EnergyTransaction reOffer = createReOffer();
        if (reOffer.getEnergyAmountFloat() > 0 && internalDataModel.getOwnEnergyOffer().getEnergyAmountFloat() > 0) {
            reOffer.setTransactionPrice(receivedEnergyOffer.getTransactionPrice());
            reOffer.setInitialEnergyAmountOffered(reOffer.getEnergyAmountFloat());
            reOffer.setInitialTransactionPriceOffered(receivedEnergyOffer.getTransactionPrice());
            if (this.energyTradingAgent.checkIfOfferAgent()) {
                reOffer.setOfferingAgent(this.energyTradingAgent.getAID());
                reOffer.setTradeTypeString(this.energyTradingAgent.getInternalDataModel().Offer);
            } else {
                reOffer.setAskingAgent(this.energyTradingAgent.getAID());
                reOffer.setTradeTypeString(this.energyTradingAgent.getInternalDataModel().Ask);
            }
            reOffer.setLocalTransactionID(generateTransactionID());
            this.energyTradingAgent.addBehaviour(new CreateEnergyOffer(energyTradingAgent, reOffer));
        }
    }

    /**
     * Handle rejection of an EnergyResult.
     */
    private void handleRejectMessage(EnergyResult receivedEnergyResult) {
        String localTransactionID = receivedEnergyResult.getLocalTransactionID();
        if (!localTransactionID.equals("0")) {
            this.energyTradingAgent.getInternalDataModel().getListOpenEnergyOffers().remove(localTransactionID);
        }
        
        EnergyTransaction reOffer = createReOffer();
        if (reOffer.getEnergyAmountFloat() > 0 && this.energyTradingAgent.getInternalDataModel().getOwnEnergyOffer().getEnergyAmountFloat() > 0) {
            reOffer.setTradeTypeString(this.energyTradingAgent.getInternalDataModel().Offer);
            reOffer.setTransactionPrice(receivedEnergyResult.getTransactionPrice());
            reOffer.setInitialEnergyAmountOffered(reOffer.getEnergyAmountFloat());
            reOffer.setInitialTransactionPriceOffered(receivedEnergyResult.getTransactionPrice());
            if (this.energyTradingAgent.checkIfOfferAgent()) {
                reOffer.setOfferingAgent(this.energyTradingAgent.getAID());
                reOffer.setTradeTypeString(this.energyTradingAgent.getInternalDataModel().Offer);
            } else {
                reOffer.setAskingAgent(this.energyTradingAgent.getAID());
                reOffer.setTradeTypeString(this.energyTradingAgent.getInternalDataModel().Ask);
            }
            reOffer.setLocalTransactionID(generateTransactionID());
            this.energyTradingAgent.addBehaviour(new CreateEnergyOffer(energyTradingAgent, reOffer));
        }
    }

    /**
     * Handle accepted EnergyResult and update internal data structures.
     */
    private void handleAcceptedEnergyResult(EnergyResult receivedEnergyResult) {
        Date date = new Date();
        long timeStampInMillis = date.getTime();
        receivedEnergyResult.setTimeStampMS(timeStampInMillis);
        
        float openEnergyAmount = this.energyTradingAgent.updateOpenEnergyAmount();
        HashMap<String, EnergyTransaction> listOpenEnergy = this.energyTradingAgent.getInternalDataModel().getListOpenEnergyOffers();
        
        EnergyTransaction energyTransactionFromResult = listOpenEnergy.get(receivedEnergyResult.getLocalTransactionID());
        if (energyTransactionFromResult != null) {
            energyTransactionFromResult.setEnergyAmountFloat(receivedEnergyResult.getDeliveredEnergyFloat());
            listOpenEnergy.replace(receivedEnergyResult.getLocalTransactionID(), energyTransactionFromResult);
        }
        
        if (this.energyTradingAgent.checkIfOfferAgent()) {
            receivedEnergyResult.setOpenEnergyAmountOffer(this.energyTradingAgent.updateOwnEnergyOffer());
        } else {
            receivedEnergyResult.setOpenEnergyAmountAsked(this.energyTradingAgent.updateOwnEnergyOffer());
        }
        
        this.energyTradingAgent.getInternalDataModel().addResult2List(receivedEnergyResult, true);
        new ReadAndWrite(this.energyTradingAgent).writeTradingResults(
                this.energyTradingAgent.getInternalDataModel().getResultListExtended(),
                this.energyTradingAgent.getInternalDataModel().getStorage(),
                this.energyTradingAgent.getLocalName()
        );
    }

    private String generateTransactionID() {
        return UUID.randomUUID().toString();
    }

    private EnergyTransaction createReOffer() {
        EnergyTransaction reOffer = new EnergyTransaction();
        reOffer.setEnergyAmountFloat(this.energyTradingAgent.updateOpenEnergyAmount());
        return reOffer;
    }
}
