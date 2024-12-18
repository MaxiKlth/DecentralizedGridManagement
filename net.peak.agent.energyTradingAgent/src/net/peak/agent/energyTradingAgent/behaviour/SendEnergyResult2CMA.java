package net.peak.agent.energyTradingAgent.behaviour;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import net.peak.agent.energyTradingAgent.EnergyTradingAgent;
import net.peak.datamodel.communication.EnergyResult;
import net.peak.datamodel.communication.PutEnergyResult;

public class SendEnergyResult2CMA extends OneShotBehaviour {
	private EnergyTradingAgent energyTradingAgent;

	/**
	 * Constructor to initialize SendEnergyResult2CMA behaviour.
	 *
	 * @param energyTradingAgent The agent responsible for sending the energy result to the CMA.
	 */
	public SendEnergyResult2CMA(EnergyTradingAgent energyTradingAgent) {
		this.energyTradingAgent = energyTradingAgent;
	}

	/**
	 * The main action method that executes the behavior.
	 */
	@Override
	public void action() {
		// Create a new ACL message
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setLanguage(this.energyTradingAgent.getAgentCodec().getName());
		msg.setOntology(this.energyTradingAgent.getPeakOntology().getName());

		// Create and configure the EnergyResult
		EnergyResult energyResult = new EnergyResult();
		float temporaryEnergyFloatAmount = this.energyTradingAgent.getInternalDataModel().getInitialEnergyAmountOfIntervall();
		energyResult.setEnergyAmountFloat(temporaryEnergyFloatAmount);
		energyResult.setDeliveredEnergy(temporaryEnergyFloatAmount);
		energyResult.setActualPeriod(this.energyTradingAgent.getInternalDataModel().getPeriodNumber());
		energyResult.setTradeTypeString(this.energyTradingAgent.getInternalDataModel().getOwnEnergyOffer().getTradeTypeString());
		
		// Prepare the PutEnergyResult to be sent
		PutEnergyResult putEnergyResult = new PutEnergyResult();
		putEnergyResult.setEnergyResult(energyResult);

		// Create the action containing the PutEnergyResult
		Action action = new Action();
		action.setActor(this.energyTradingAgent.getAID());
		action.setAction(putEnergyResult);

		// Set the sender and receiver for the message
		msg.setSender(this.energyTradingAgent.getAID());
		int nameCMA = getNameOfCMA();
		AID receiverAID = new AID(Integer.toString(nameCMA), AID.ISLOCALNAME);
		msg.addReceiver(receiverAID);

		// Fill the message content and send it
		try {
			this.energyTradingAgent.getContentManager().fillContent(msg, action);
			this.energyTradingAgent.send(msg);
		} catch (CodecException | OntologyException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Helper method to determine the name of the CMA based on the agent's local name.
	 *
	 * @return The calculated CMA name as an integer.
	 */
	private int getNameOfCMA() {
		return Integer.parseInt(this.energyTradingAgent.getLocalName()) / 1000;
	}
}
