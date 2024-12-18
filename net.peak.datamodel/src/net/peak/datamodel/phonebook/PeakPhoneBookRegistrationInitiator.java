package net.peak.datamodel.phonebook;

import de.enflexit.jade.phonebook.AbstractPhoneBookEntry;
import de.enflexit.jade.phonebook.behaviours.PhoneBookRegistrationInitiator;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/**
 * {@link PhoneBookRegistrationInitiator} implementation for the peak project. Extends
 * the superclass behaviour by expecting the phone book entry to be returned, with the
 * peak member ID that is assigned by the platform agent.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class PeakPhoneBookRegistrationInitiator extends PhoneBookRegistrationInitiator {

	private static final long serialVersionUID = 1218542660534415438L;
	
	private boolean debug = false;

	/**
	 * Instantiates a new peak phone book registration initiator.
	 *
	 * @param agent the agent
	 * @param myPhoneBookEntry the my phone book entry
	 * @param phoneBookMaintainer the phone book maintainer
	 * @param retryOnFailure the retry on failure
	 */
	public PeakPhoneBookRegistrationInitiator(Agent agent, AbstractPhoneBookEntry myPhoneBookEntry,	AID phoneBookMaintainer, boolean retryOnFailure) {
		super(agent, myPhoneBookEntry, phoneBookMaintainer, retryOnFailure);
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.jade.phonebook.behaviours.PhoneBookRegistrationInitiator#handleInform(jade.lang.acl.ACLMessage)
	 */
	@Override
	protected void handleInform(ACLMessage msg) {
		super.handleInform(msg);
		
		try {
			PeakPhoneBookEntry phoneBookEntry = (PeakPhoneBookEntry) msg.getContentObject();
			//TODO what to to with this?
			if (this.debug==true) {
				System.out.println("[" + this.getClass().getSimpleName() + "] Received registration response, my assigned peak member ID is " + phoneBookEntry.getPeakMemberID());
			}
		} catch (UnreadableException e) {
			System.err.println("[" + this.getClass().getSimpleName() + "] Could not extract phone book entry from registration response!");
			e.printStackTrace();
		}
	}

}
