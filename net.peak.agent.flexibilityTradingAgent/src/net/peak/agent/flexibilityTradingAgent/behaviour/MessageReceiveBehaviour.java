package net.peak.agent.flexibilityTradingAgent.behaviour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import net.peak.agent.flexibilityTradingAgent.CongestionManagingAgent;
import net.peak.datamodel.communication.EnergyResult;
import net.peak.datamodel.communication.FlexibilityResult;
import net.peak.datamodel.communication.FlexibilityTransaction;
import net.peak.datamodel.communication.PutEnergyResult;
import net.peak.datamodel.communication.PutFlexibilityTransaction;
import net.peak.datamodel.configuration.PeakConfiguration;

public class MessageReceiveBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = 254907738451044781L;
	
	private Vector<MessageTemplate> ignoreList; // List of message templates to ignore
	private MessageTemplate messageTemplate; // Template used to filter messages
	private CongestionManagingAgent congestionManagingAgent; // Reference to the managing agent
	private EnergyResult oldEnergyResult; // Stores the previous energy result
	private List<FlexibilityResult> oldFlexibilityResult; // Stores the previous flexibility results
	
	// Constructor to initialize the behaviour with the managing agent
	public MessageReceiveBehaviour(CongestionManagingAgent marketAgent2) {
		congestionManagingAgent = marketAgent2;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
	
		ACLMessage msg = congestionManagingAgent.receive(); // Receive a message from the queue
		
		// If a message is received, process it
		if (msg != null) {
			
			// Check if the message is from the energy AMS agent
			if (msg.getSender().getLocalName().equals(this.congestionManagingAgent.getInternalDataModel().getEnergyAMDAID().getLocalName())) {
				try {
					// Convert the received content into a list of AID (Agent Identifiers)
					@SuppressWarnings("rawtypes")
					ArrayList openGridNodes = (java.util.ArrayList) msg.getContentObject();
					this.congestionManagingAgent.getInternalDataModel().setPhoneBook((java.util.ArrayList<AID>) openGridNodes);
					System.out.println("Agents found in: " + this.congestionManagingAgent.getLocalName() + " " + openGridNodes.size());

				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
			
			// Handle failure messages
			if (msg.getPerformative() == ACLMessage.FAILURE) {
				System.err.println("[" + this.myAgent.getLocalName() + "] Received failure message from " + msg.getSender().getName() + ":");
				System.err.println(msg.getContent());
				return;
			}
			
			// Process messages with ontology content
			if (msg.getOntology() != null) {
				
				Action contentAction;
				contentAction = new Action();
				
				try {
					contentAction = (Action) this.congestionManagingAgent.getContentManager().extractContent(msg);
				} catch (CodecException | OntologyException e1) {
					e1.printStackTrace();
				}
				
				// Handle Flexibility Transactions
				if (contentAction.getAction() instanceof PutFlexibilityTransaction || contentAction.getAction() instanceof FlexibilityTransaction) {
					PutFlexibilityTransaction putFlexibilityTransaction = (PutFlexibilityTransaction) contentAction.getAction();
					FlexibilityTransaction flexibilityTransaction = putFlexibilityTransaction.getFlexibilityTransaction();
					
					// Check if the calculation is complete
					if (flexibilityTransaction.getCalculationComplete() == true) {
						this.congestionManagingAgent.getInternalDataModel().setEndIteration(this.congestionManagingAgent.getInternalDataModel().getIteration());
						this.congestionManagingAgent.getInternalDataModel().addPeriodCalculationDone(this.congestionManagingAgent.getInternalDataModel().getTradingPeriod(), true);
						
						// Send the congestion management results
						SendCMResults scmr = new SendCMResults(this.congestionManagingAgent);
						this.congestionManagingAgent.addBehaviour(scmr);
					} else {
						// Add the received transaction to the temporary list
						this.congestionManagingAgent.getInternalDataModel().addTemporaryListFlexibilityTransaction(msg.getSender(), flexibilityTransaction);
					}
				} 
				// Handle Energy Results
				else if (contentAction.getAction() instanceof PutEnergyResult) {
					
					// Ensure that all nodes are accounted for before processing
					if (this.congestionManagingAgent.getInternalDataModel().getPhoneBook().size() < this.congestionManagingAgent.getInternalDataModel().getMaxNodes()) {
						PeakConfiguration peakConfiguration = new PeakConfiguration();
						
						ACLMessage informMessage = new ACLMessage(ACLMessage.INFORM);
						informMessage.addReceiver(peakConfiguration.getTargetAID());
						informMessage.setContent("This is an INFORM message.");
						this.congestionManagingAgent.send(informMessage);
						
				        try {
				            Thread.sleep(3000);
				        } catch (InterruptedException e) {
				            e.printStackTrace();
				        }
					}
					
					PutEnergyResult putEnergyResult = (PutEnergyResult) contentAction.getAction();
					EnergyResult energyResult = putEnergyResult.getEnergyResult();
					HashMap<Integer, Double> currentEnergyList = this.congestionManagingAgent.getInternalDataModel().getPlannedEnergyBalanceList().get(energyResult.getActualPeriod());
					
					if (currentEnergyList == null) {
						currentEnergyList = new HashMap<Integer, Double>();
					}
					
					AID aid = msg.getSender();
					int tempoAID = Integer.parseInt(msg.getSender().getLocalName());
					
					if (tempoAID > 999) {
						tempoAID = tempoAID / 1000;
					}
					
					if (!currentEnergyList.containsKey(tempoAID)) {
						this.congestionManagingAgent.getInternalDataModel().setEDATradingPeriod(energyResult.getActualPeriod()); 

						HashMap<Integer, HashMap<Integer, Double>> temporaryPlannedEnergyList = this.congestionManagingAgent.getInternalDataModel().getPlannedEnergyBalanceList();
						
						if (messageIsFromOwnEDA(msg) == true) {
							this.congestionManagingAgent.getInternalDataModel().addPlannedEnergyBalance2List(Integer.parseInt(msg.getSender().getLocalName()) / 1000, energyResult.getEnergyAmountFloat(), energyResult.getActualPeriod());
							
							// Invert the energy amount if it is an "Offer" type
							if (energyResult.getTradeTypeString().equals("Offer")) {
								float temporaryEnergyAmount = energyResult.getDeliveredEnergyFloat();
								energyResult.setEnergyAmountFloat(-temporaryEnergyAmount);
								energyResult.setDeliveredEnergy(-temporaryEnergyAmount);
							}
							
							sendEnergyResultsAsBroadcastToCMA(energyResult);
							this.congestionManagingAgent.getInternalDataModel().setTradingPeriod(energyResult.getActualPeriod());
							
						} else {
							this.congestionManagingAgent.getInternalDataModel().addPlannedEnergyBalance2List(Integer.parseInt(msg.getSender().getLocalName()), energyResult.getEnergyAmountFloat(), energyResult.getActualPeriod());
							
							// If all nodes have sent their planned energy balances, start grid congestion identification
							if (temporaryPlannedEnergyList.containsKey(this.congestionManagingAgent.getInternalDataModel().getTradingPeriod()) && !this.congestionManagingAgent.getInternalDataModel().getPeriodCalculationDone().containsKey(this.congestionManagingAgent.getInternalDataModel().getTradingPeriod())) {
								if (temporaryPlannedEnergyList.get(this.congestionManagingAgent.getInternalDataModel().getTradingPeriod()).size() == this.congestionManagingAgent.getInternalDataModel().getMaxNodes()) {
									IdentifyGridCongestion igc = new IdentifyGridCongestion(congestionManagingAgent);
									this.congestionManagingAgent.addBehaviour(igc);		
								}
							} 
						}
					}
				}
			}
		}
	}
	
	// Sends energy results to all agents in the phonebook
	private void sendEnergyResultsAsBroadcastToCMA(EnergyResult energyResult) {
		
		List<AID> phoneBook = this.congestionManagingAgent.getInternalDataModel().getPhoneBook();
		
		for (int i = 0; i < phoneBook.size(); i++) {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			
			PutEnergyResult putEnergyResult = new PutEnergyResult();
			putEnergyResult.setEnergyResult(energyResult);
			
			Action action = new Action();
			action.setActor(this.congestionManagingAgent.getAID());
			action.setAction(putEnergyResult);
			
			msg.setSender(this.congestionManagingAgent.getAID());
			msg.setLanguage(this.congestionManagingAgent.getAgentCodec().getName());
			msg.setOntology(this.congestionManagingAgent.getPeakOntology().getName());
			msg.addReceiver(phoneBook.get(i));
			
			try {
				this.congestionManagingAgent.getContentManager().fillContent(msg, action);
				this.congestionManagingAgent.send(msg);
				
			} catch (CodecException | OntologyException ex) {
				ex.printStackTrace();
			}
		}
	}

	// Checks if the message is from the agent's own EDA
	private boolean messageIsFromOwnEDA(ACLMessage msg) {
		int senderIntName = Integer.parseInt(msg.getSender().getLocalName()) / 1000;
		int ownIntName = Integer.parseInt(this.congestionManagingAgent.getLocalName());
		
		return senderIntName == ownIntName;
	}

	// Builds a message template that excludes messages matching any template in the ignore list
	private MessageTemplate buildMessageTemplate() {
		
		int numOfTemplates = this.getIgnoreList().size();
		if (numOfTemplates == 0) {
			return null; // No templates to ignore
		} else {
			// Combine all templates in the ignore list into a single template using OR
			MessageTemplate ignore = getIgnoreList().get(0);
			
			for (int i = 1; i < numOfTemplates; i++) {
				ignore = MessageTemplate.or(ignore, this.getIgnoreList().get(i));
			}
			
			// Negate the combined template to create a filter that ignores those templates
			return MessageTemplate.not(ignore);
		}
	}

	// Gets the list of ignored message templates
	public Vector<MessageTemplate> getIgnoreList() {
		if (ignoreList == null) {
			ignoreList = new Vector<MessageTemplate>();
		}
		return ignoreList;
	}
	
	// Sets the ignore list
	public void setIgnoreList(Vector<MessageTemplate> ignoreList) {
		this.ignoreList = ignoreList;
		if (this.messageTemplate != null) {
			this.messageTemplate = this.buildMessageTemplate(); // Rebuild the template if necessary
		}
	}

	// Adds a message template to the ignore list and rebuilds the filter template
	public void addMessageTemplateToIgnoreList(MessageTemplate template) {
		this.getIgnoreList().addElement(template);
		if (this.messageTemplate != null) {
			this.messageTemplate = this.buildMessageTemplate();
		}
	}

	// Removes a template from the ignore list and rebuilds the filter template
	public void removeTemplateFromIgnoreList(MessageTemplate template) {
		this.getIgnoreList().remove(template);
		if (this.messageTemplate != null) {
			this.messageTemplate = this.buildMessageTemplate();
		}
	}

	// Removes a template from the ignore list by index and rebuilds the filter template
	public void removeTemplateFromIgnoreList(int index) {
		this.getIgnoreList().remove(index);
		if (this.messageTemplate != null) {
			this.messageTemplate = this.buildMessageTemplate();
		}
	}
	
	// Clears all templates from the ignore list
	public void clearIgnoreList() {
		this.getIgnoreList().clear();
		if (this.messageTemplate != null) {
			this.messageTemplate = this.buildMessageTemplate();
		}
	}

	// Retrieves the name of the current agent
	private String getAgentName() {
		return myAgent != null ? myAgent.getLocalName() : "null";
	}

}
