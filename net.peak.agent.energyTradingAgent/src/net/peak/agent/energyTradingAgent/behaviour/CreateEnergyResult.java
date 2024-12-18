package net.peak.agent.energyTradingAgent.behaviour;

import java.util.Date;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import net.peak.agent.energyTradingAgent.EnergyTradingAgent;
import net.peak.agent.energyTradingAgent.InternalDataModel.OfferAnswerType;
import net.peak.datamodel.communication.EnergyResult;
import net.peak.datamodel.communication.EnergyTransaction;

public class CreateEnergyResult extends OneShotBehaviour {

    private EnergyTransaction energyTransaction;
    private EnergyTradingAgent energyTradingAgent;
    private AID receiverAID;
    private boolean sendResultEnabled;

    /**
     * Constructor to initialize the CreateEnergyResult behavior.
     *
     * @param e                 The energy transaction to be processed.
     * @param energyTradingAgent The agent managing energy trading.
     * @param receiverAID       The AID of the agent that will receive the result.
     * @param sendResultEnabled Flag indicating whether the result should be sent.
     */
    public CreateEnergyResult(EnergyTransaction e, EnergyTradingAgent energyTradingAgent, AID receiverAID, boolean sendResultEnabled) {
        this.energyTransaction = e;
        this.energyTradingAgent = energyTradingAgent;
        this.receiverAID = receiverAID;
        this.sendResultEnabled = sendResultEnabled;
    }

    /**
     * Action method that creates and sends an EnergyResult based on the transaction.
     * It updates internal data models and optionally sends the result to another agent.
     */
    @Override
    public void action() {
        OfferAnswerType offerAnswerType = OfferAnswerType.ACCEPT;

        // Create a new EnergyResult object and populate it with transaction details.
        EnergyResult energyResult = new EnergyResult();
        energyResult.setActualPeriod(this.energyTradingAgent.getInternalDataModel().getPeriodNumber());
        energyResult.setActualTradingCycle(this.energyTradingAgent.getInternalDataModel().getTradingCyclesCnt());
        energyResult.setAgentOfferedEnergy(energyTransaction.getOfferingAgent().getLocalName());

        // Ensure the asking agent is set in the transaction.
        if (energyTransaction.getAskingAgent() == null) {
            energyTransaction.setAskingAgent(receiverAID);
        }
        energyResult.setAgentAskedEnergy(energyTransaction.getAskingAgent().getLocalName());
        energyResult.setInitialEnergyAmountAsked(energyTransaction.getInitialEnergyAmountAsked());
        energyResult.setInitialEnergyAmountOffered(energyTransaction.getInitialEnergyAmountOffered());
        energyResult.setInitialTransactionPriceAsked(energyTransaction.getInitialTransactionPriceAsked());
        energyResult.setInitialTransactionPriceOffered(energyTransaction.getInitialTransactionPriceOffered());
        energyResult.setDeliveredEnergyFloat(energyTransaction.getEnergyAmountFloat());
        energyResult.setEnergyPriceMatched(energyTransaction.getTransactionPrice());
        energyResult.setLocalTransactionID(energyTransaction.getLocalTransactionID());
        energyResult.setOwnEnergyOffer(this.energyTradingAgent.getInternalDataModel().getOwnEnergyOffer());
        energyResult.setEnergyTransaction(energyTransaction.getEnergyTransaction());
        energyResult.setCommuncationTimeInMs(energyTransaction.getCommunicationTime());

        // Set the timestamp for the EnergyResult.
        Date date = new Date();
        long timeStampInMillis = date.getTime();
        energyResult.setTimeStampMS(timeStampInMillis);

        // Retrieve and update the transaction from the internal list of open offers.
        EnergyTransaction energyTransactionFromResult = this.energyTradingAgent.getInternalDataModel().getListOpenEnergyOffers().get(energyResult.getLocalTransactionID());
        if (energyTransactionFromResult != null) {
            energyTransactionFromResult.setEnergyAmountFloat(energyResult.getDeliveredEnergyFloat());
            this.energyTradingAgent.getInternalDataModel().getListOpenEnergyOffers().replace(energyResult.getLocalTransactionID(), energyTransactionFromResult);
        }

        // Add the result to the agent's internal data model.
        this.energyTradingAgent.getInternalDataModel().addResult2List(energyResult, sendResultEnabled);

        // If sending results is enabled, update the energy offer and send the result.
        if (this.sendResultEnabled) {
            if (this.energyTradingAgent.checkIfOfferAgent()) {
                energyResult.setOpenEnergyAmountOffer(this.energyTradingAgent.updateOwnEnergyOffer());
            } else {
                energyResult.setOpenEnergyAmountAsked(this.energyTradingAgent.updateOwnEnergyOffer());
            }
            this.energyTradingAgent.getInternalDataModel().getOwnEnergyOffer().setEnergyAmountFloat(this.energyTradingAgent.updateOwnEnergyOffer());

            // Use a threaded behavior to send the energy result.
            ThreadedBehaviourFactory tbf = new ThreadedBehaviourFactory();
            SendEnergyResult ser = new SendEnergyResult(offerAnswerType, energyResult, energyTradingAgent, receiverAID);
            this.energyTradingAgent.addBehaviour(tbf.wrap(ser));

            // Reset the waiting iteration counter in the internal data model.
            this.energyTradingAgent.getInternalDataModel().setStartWaitingIteration(0);
        }
    }
}
