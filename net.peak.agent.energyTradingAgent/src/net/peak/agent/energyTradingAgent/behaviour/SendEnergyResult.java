package net.peak.agent.energyTradingAgent.behaviour;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.util.Date;
import java.util.HashMap;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import net.peak.agent.energyTradingAgent.EnergyTradingAgent;
import net.peak.agent.energyTradingAgent.InternalDataModel.OfferAnswerType;
import net.peak.datamodel.communication.*;
import net.peak.datamodel.configuration.PeakConfiguration;
import net.peak.topology.ReadAndWrite;

public class SendEnergyResult extends OneShotBehaviour {

	private static final long serialVersionUID = -7215830591060381700L;
	private EnergyResult energyResult;
	private OfferAnswerType answerType;
	private EnergyTradingAgent energyTradingAgent;
	private AID senderAID;

	/**
	 * Constructor to initialize SendEnergyResult behaviour.
	 *
	 * @param answerType      The type of response to send (ACCEPT, REJECT).
	 * @param energyResult    The energy result to be sent.
	 * @param energyTradingAgent The agent responsible for sending the result.
	 * @param senderAID       The agent identifier of the sender.
	 */
	public SendEnergyResult(OfferAnswerType answerType, EnergyResult energyResult, EnergyTradingAgent energyTradingAgent, AID senderAID) {
		this.answerType = answerType;
		this.energyResult = energyResult;
		this.energyTradingAgent = energyTradingAgent;
		this.senderAID = senderAID;
	}

	/**
	 * The main action method that executes the behavior.
	 */
	@Override
	public void action() {
		sendAnswerMessage(answerType, energyResult);
	}

	/**
	 * Sends the answer message with the specified offer answer type and energy result.
	 *
	 * @param answerType The type of response (ACCEPT, REJECT).
	 * @param energyResult The energy result to be sent.
	 */
	private void sendAnswerMessage(OfferAnswerType answerType, EnergyResult energyResult) {
		// Set the timestamp for the energy result
		Date date = new Date();
		long timeStampInMillis = date.getTime();
		energyResult.setTimeStampMS(timeStampInMillis);
		
		LongValue currentTimeinMS = new LongValue();
		currentTimeinMS.setLongValue(System.currentTimeMillis());
		energyResult.setTimeSlotStart(currentTimeinMS);
		
		//Set CPU und RAM Load:
		 OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
	        
         // Get CPU Load
         double cpuLoad = osBean.getProcessCpuLoad();
         energyResult.setCpuLoad(cpuLoad);
 		
 		//Get RAM Load
 		long totalMemory = osBean.getTotalPhysicalMemorySize();
 		long freeMemory = osBean.getFreePhysicalMemorySize();
         long usedMemory = totalMemory - freeMemory;
         
         // Prozentualen Anteil des genutzten Speichers berechnen
         double usedMemoryPercentage = (double) usedMemory / totalMemory;
         energyResult.setRamLoad(usedMemoryPercentage);

		// Save the trading results to the result list
		ReadAndWrite raw = new ReadAndWrite(this.energyTradingAgent);
		HashMap<String, EnergyResult> resultList = new HashMap<>();
		synchronized (this.energyTradingAgent.getInternalDataModel().getResultList()) {
			HashMap<String, EnergyResult> resultListOld = new HashMap<>(this.energyTradingAgent.getInternalDataModel().getResultListExtended());
			if (resultListOld != null) {
				resultList.putAll(resultListOld);
			}
		}
		raw.writeTradingResults(resultList, this.energyTradingAgent.getInternalDataModel().getStorage(), this.energyTradingAgent.getLocalName());

		// Create and configure the ACL message
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		if (answerType.equals(OfferAnswerType.ACCEPT)) {
			msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
		} else if (answerType.equals(OfferAnswerType.REJECT)) {
			msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
		}
		msg.setPostTimeStamp(System.nanoTime());

		// Prepare the energy result to be sent
		PutEnergyResult putEnergyResult = new PutEnergyResult();
		putEnergyResult.setEnergyResult(energyResult);

		// Create the action containing the energy result
		Action action = new Action();
		action.setActor(this.energyTradingAgent.getAID());
		action.setAction(putEnergyResult);

		// Set the sender's address
		PeakConfiguration peakConfiguration = new PeakConfiguration();
		String agentIP = peakConfiguration.ownIPAddress;
		AID senderAIDAdjusted = new AID(this.energyTradingAgent.getAID().getName(), AID.ISGUID);
		senderAIDAdjusted.addAddresses("http://" + agentIP + ":7778/acc");
		msg.setSender(senderAIDAdjusted);

		// Add the receiver to the message and set the content
		msg.addReceiver(senderAID);
		msg.setLanguage(this.energyTradingAgent.getAgentCodec().getName());
		msg.setOntology(this.energyTradingAgent.getPeakOntology().getName());

		try {
			this.energyTradingAgent.getContentManager().fillContent(msg, action);
			this.energyTradingAgent.getInternalDataModel().increaseAmountMessagesSended();
			this.energyTradingAgent.send(msg);
			System.out.println("TradingAmount: " + energyResult.getEnergyAmountFloat() + " from Agent: " + msg.getSender().getName() + " to Agent: " + senderAID.getName());
		} catch (CodecException | OntologyException ex) {
			ex.printStackTrace();
		}
	}
}
