package net.peak.datamodel.phonebook;

import java.util.List;

import de.enflexit.jade.phonebook.AbstractPhoneBookEntry;
import de.enflexit.jade.phonebook.PhoneBook;
import de.enflexit.jade.phonebook.behaviours.PhoneBookRegistrationResponder;
import jade.core.Agent;

/**
 * {@link PhoneBookRegistrationResponder} implementation for the Peak project. 
 * Extends the superclass behaviour by assigning peakMemberIDs.
 * 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class PeakPhoneBookRegistrationResponder extends PhoneBookRegistrationResponder {

	private static final long serialVersionUID = 3687043751819147371L;
	
	private int nextMemberID;

	/**
	 * Instantiates a new peak phone book registration responder.
	 * @param agent the agent
	 * @param localPhoneBook the local phone book
	 */
	public PeakPhoneBookRegistrationResponder(Agent agent, PhoneBook localPhoneBook) {
		super(agent, localPhoneBook);
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.jade.phonebook.behaviours.PhoneBookRegistrationResponder#processPhoneBookEntry(de.enflexit.jade.phonebook.AbstractPhoneBookEntry)
	 */
	@Override
	protected AbstractPhoneBookEntry processPhoneBookEntry(AbstractPhoneBookEntry entry) {
		
		if (entry instanceof PeakPhoneBookEntry) {
			
			// --- Assigns the peak member id -----------------
			int peakMemberID;
			
			// --- Check for an existing entry ----------------
			List<AbstractPhoneBookEntry> searchResults = this.getLocalPhoneBook().getEntries(PeakPhoneBookSearchFilter.matchAgentAID(entry.getAgentAID()));
			if (searchResults.size()>0 && searchResults.get(0) instanceof PeakPhoneBookEntry) {
				// --- Assign the same ID ---------------------
				peakMemberID =  ((PeakPhoneBookEntry)searchResults.get(0)).getPeakMemberID();
			} else {
				// --- Generate a new ID ----------------------
				peakMemberID = this.getNextFreeMemberID();
			}
			((PeakPhoneBookEntry)entry).setPeakMemberID(peakMemberID);
		}
		
		return entry;
	}
	
	/**
	 * Gets the next free member ID.
	 * @return the next free member ID
	 */
	private int getNextFreeMemberID() {
		int id;
		
		do {
			// --- Pick the next ID and increase the counter --------
			id = this.nextMemberID++;
		// --- Repeat if the ID is already in use -------------------
		} while (this.getLocalPhoneBook().getEntries(PeakPhoneBookSearchFilter.matchPeakMemberID(id)).size()>0);
		
		return id;
	}
	
}
