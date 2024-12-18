package net.peak.datamodel.phonebook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import de.enflexit.jade.phonebook.AbstractPhoneBookEntry;
import de.enflexit.jade.phonebook.PhoneBook;
import de.enflexit.jade.phonebook.PhoneBookEvent;
import de.enflexit.jade.phonebook.PhoneBookEvent.Type;
import de.enflexit.jade.phonebook.PhoneBookListener;
import de.enflexit.jade.phonebook.behaviours.PhoneBookQueryInitiator;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import net.peak.datamodel.phonebook.PeakPhoneBookEntry.PeakRole;

/**
 * The Class AbstractWaitForCentralPeakAgentRegistrationBehaviour can be extended and used to wait for the 
 * registration of the central PEAK agents, specified by the corresponding {@link PeakRole}.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public abstract class AbstractWaitForCentralPeakAgentRegistrationBehaviour extends SimpleBehaviour implements PhoneBookListener {

	private static final long serialVersionUID = -4067832951564776319L;

	private boolean done;

	private PhoneBook phoneBook;
	private AID centralPhoneBookMaintainerAID;

	private int timeOutMillis;
	private Long timeOut;
	
	private HashSet<PeakRole> peakRolesToWaitFor; 
	
	/**
	 * Instantiates a new wait for peak agents.
	 *
	 * @param agent the agent
	 * @param phoneBook the phone book
	 * @param centralPhoneBookMaintainerAID the agent AID of the central phone book maintainer
	 * @param timeOutMillis the time out milliseconds
	 * @param peakRolesToWaitFor the peak roles
	 */
	public AbstractWaitForCentralPeakAgentRegistrationBehaviour(Agent agent, PhoneBook phoneBook, AID centralPhoneBookMaintainerAID, int timeOutMillis, PeakRole ... peakRolesToWaitFor) {
		super(agent);
		this.phoneBook = phoneBook;
		this.phoneBook.addPhoneBookListener(this);
		this.centralPhoneBookMaintainerAID = centralPhoneBookMaintainerAID;
		this.timeOutMillis = timeOutMillis;
		this.peakRolesToWaitFor = new HashSet<>(Arrays.asList(peakRolesToWaitFor));
		this.initialize();
	}
	
	/**
	 * Initializes this class by requesting the AID from the central phone book maintainer agent.
	 */
	private void initialize() {
		for (PeakRole prSeach : this.peakRolesToWaitFor) {
			this.myAgent.addBehaviour(new PhoneBookQueryInitiator<>(this.myAgent, this.phoneBook, this.centralPhoneBookMaintainerAID, PeakPhoneBookSearchFilter.matchPeakRole(prSeach), true));
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.jade.phonebook.PhoneBookListener#handlePhoneBookEvent(de.enflexit.jade.phonebook.PhoneBookEvent)
	 */
	@Override
	public void handlePhoneBookEvent(PhoneBookEvent pbEvent) {
		
		if (pbEvent.getType()==Type.ENTRIES_ADDED) {
			// --- New or updated PhoneBook entries -----------------
			List<? extends AbstractPhoneBookEntry> addedEntries = pbEvent.getAffectedEntries();
			for (AbstractPhoneBookEntry pbEntry : addedEntries) {
				if (pbEntry instanceof PeakPhoneBookEntry) {
					PeakPhoneBookEntry peakPbEntry = (PeakPhoneBookEntry) pbEntry;
					PeakRole peakRole = peakPbEntry.getPeakRole();
					if (this.peakRolesToWaitFor.contains(peakRole)==true) {
						this.peakRolesToWaitFor.remove(peakRole);
					}
				}
			}
			// --- Restart this behaviour ---------------------------
			this.restart();
		}
	}
	
	/* (non-Javadoc)
	 * @see jade.core.behaviours.Behaviour#action()
	 */
	@Override
	public void action() {
		
		// --- Define the time out --------------
		if (this.timeOut==null) timeOut = System.currentTimeMillis() + this.timeOutMillis;
		
		// --- Check the phone book entries -----
		this.checkPhoneBookEntries();
		if (this.peakRolesToWaitFor.size()==0) {
			this.done = true;
			this.phoneBook.removePhoneBookListener(this);
			this.onCentralPhoneBookEntriesReceived();
			
		} else {
			if (System.currentTimeMillis()<=this.timeOut) {
				this.block(this.timeOut - System.currentTimeMillis());
			} else {
				// --- Time out reached ---------
				this.done = true;
				this.phoneBook.removePhoneBookListener(this);
				this.onCentralPhoneBookEntriesMissing(this.peakRolesToWaitFor);
			}
		}
	}
	/* (non-Javadoc)
	 * @see jade.core.behaviours.Behaviour#done()
	 */
	@Override
	public boolean done() {
		return this.done;
	}
	
	
	/**
	 * Checks the phone book entries for the specified roles to search for and 
	 * removes them from the search list if there are available.
	 */
	private void checkPhoneBookEntries() {
		
		List<PeakRole> prWorkList = new ArrayList<>(this.peakRolesToWaitFor);
		for (PeakRole prSeach : prWorkList) {
			// --- Get the result from the phone book ---------------
			ArrayList<AbstractPhoneBookEntry> pbEntries = this.phoneBook.getEntries(PeakPhoneBookSearchFilter.matchPeakRole(prSeach));
			for (AbstractPhoneBookEntry pbEntry : pbEntries) {
				if (pbEntry instanceof PeakPhoneBookEntry) {
					PeakPhoneBookEntry peakPbEntry = (PeakPhoneBookEntry) pbEntry;
					if (peakPbEntry.getPeakRole()==prSeach) {
						this.peakRolesToWaitFor.remove(prSeach);
					}
				}
			}
		}
	}
	
	/**
	 * Will be invoked, if all required phone book entries were received.
	 */
	protected abstract void onCentralPhoneBookEntriesReceived();
	
	/**
	 * Will be invoked, if required phone book entries are still missing after the specified time out.
	 * On phone book entries missed.
	 * @param peakRolesMissed the peak roles missed
	 */
	protected abstract void onCentralPhoneBookEntriesMissing(HashSet<PeakRole> peakRolesMissed);
	
	
}
