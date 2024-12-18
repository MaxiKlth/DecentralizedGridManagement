package net.peak.agent.energyTradingAgent.behaviour;

import java.util.Date;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import net.peak.agent.energyTradingAgent.EnergyTradingAgent;
import net.peak.agent.energyTradingAgent.InternalDataModel.OfferAnswerType;
import net.peak.datamodel.communication.EnergyTransaction;
import net.peak.datamodel.communication.LongValue;
import net.peak.datamodel.communication.PutEnergyTransaction;
import net.peak.datamodel.configuration.PeakConfiguration;

public class SendEnergyOffer extends OneShotBehaviour {

	private static final long serialVersionUID = 4291897265461165779L;
	private EnergyTradingAgent energyTradingAgent;
	private EnergyTransaction energyTransaction2Send;
	private AID offerReceiver;
	private OfferAnswerType offerAnswerType;
	private boolean isFirstOffer = false;
	
	/**
	 * Constructor to initialize the SendEnergyOffer behaviour.
	 * 
	 * @param energyTradingAgent The agent managing energy trading.
	 * @param offeredEnergyTransaction The energy transaction to be sent.
	 * @param offerReceiver The receiver agent for the offer.
	 * @param offerAnswerType The type of response associated with the offer.
	 */
	public SendEnergyOffer(EnergyTradingAgent energyTradingAgent, EnergyTransaction offeredEnergyTransaction, AID offerReceiver, OfferAnswerType offerAnswerType) {
		this.energyTransaction2Send = offeredEnergyTransaction;
		this.energyTradingAgent = energyTradingAgent;
		this.offerReceiver = offerReceiver;
		this.offerAnswerType = offerAnswerType;
		this.isFirstOffer = isFirstOffer;
	}

	/**
	 * The main action method that executes the behavior.
	 */
	@Override
	public void action() {
		// Set the offering agent if not already set
		if (energyTransaction2Send.getOfferingAgent() == null) {
			energyTransaction2Send.setOfferingAgent(this.energyTradingAgent.getAID());
		}

		// Prepare the energy transaction to be sent
		PutEnergyTransaction putEnergyTransaction = new PutEnergyTransaction();
		LongValue currentTimeinMS = new LongValue();
		currentTimeinMS.setLongValue(System.currentTimeMillis());
		energyTransaction2Send.setTimeSlotStart(currentTimeinMS);
		putEnergyTransaction.setEnergyTransaction(energyTransaction2Send);
		
		// If not a first offer or reject, create an energy result
		if (!this.offerAnswerType.equals(OfferAnswerType.FIRSTOFFER) && !this.offerAnswerType.equals(OfferAnswerType.REJECT)) {
			CreateEnergyResult cer = new CreateEnergyResult(energyTransaction2Send, this.energyTradingAgent, offerReceiver, false);
			this.energyTradingAgent.addBehaviour(cer);
		}
		
		// Create and configure the message to be sent
		Action action = new Action();
		action.setActor(this.energyTradingAgent.getAID());
		action.setAction(putEnergyTransaction);
		 
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setLanguage(this.energyTradingAgent.getAgentCodec().getName());
		msg.setOntology(this.energyTradingAgent.getPeakOntology().getName());
		msg.setPostTimeStamp(System.nanoTime());
		
		// Set the sender's address
		PeakConfiguration peakConfiguration = new PeakConfiguration();
		String agentIP = peakConfiguration.ownIPAddress;
		AID senderAID = new AID(this.energyTradingAgent.getAID().getName(), AID.ISGUID);
		senderAID.addAddresses("http://" + agentIP + ":7778/acc");
		msg.setSender(senderAID);

		// Set the performative of the message based on the offer answer type
		if (this.offerAnswerType != null) {
			switch (this.offerAnswerType) {
				case REJECT: 
					msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
					break;
				case REOFFER: 
					msg.setPerformative(ACLMessage.AGREE);
					break;
				case FIRSTOFFER: 
					msg.setPerformative(ACLMessage.PROPOSE);
					break;
				case ACCEPT: 
					msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					break;
			}
		}

		// Add the receiver to the message
		msg.addReceiver(offerReceiver);
		
		// Send the message with the transaction content
		try {
			this.energyTradingAgent.getContentManager().fillContent(msg, action);
			this.energyTradingAgent.getInternalDataModel().increaseAmountMessagesSended();
			this.energyTradingAgent.send(msg);
		} catch (CodecException | OntologyException ex) {
			ex.printStackTrace();
		}
	}
}
