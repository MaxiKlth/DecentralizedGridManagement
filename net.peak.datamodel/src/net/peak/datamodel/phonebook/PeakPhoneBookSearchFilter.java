package net.peak.datamodel.phonebook;

import de.enflexit.jade.phonebook.search.PhoneBookSearchFilter;
import jade.core.AID;
import net.peak.datamodel.phonebook.PeakPhoneBookEntry.PeakRole;

/**
 * This class implements a search filter for phone book entries of type {@link PeakPhoneBookEntry}.
 * If looking for one criterion only, use the corresponding static factory method to easily create
 * a matching filter. You can also look for multiple criteria, in this case please use the default 
 * constructor, and then the setter methods to specify what you are looking for.
 *     
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class PeakPhoneBookSearchFilter implements PhoneBookSearchFilter<PeakPhoneBookEntry> {
	
	private static final long serialVersionUID = -1757224877453250780L;
	
	private AID agentAID;
	private String localName;
	private String componentType;
	
	private Integer peakMemberID;
	private String peakUserID;
	private String nodeID;
	private String gridID;
	private PeakRole peakRole;
	
	/**
	 * This factory method creates a search filter that matches the provided AID. 
	 * @param aid the aid
	 * @return the search filter
	 */
	public static PeakPhoneBookSearchFilter matchAgentAID(AID agentAID) {
		PeakPhoneBookSearchFilter searchFilter = new PeakPhoneBookSearchFilter();
		searchFilter.setAgentAID(agentAID);
		return searchFilter;
	}
	
	/**
	 * This factory method creates a search filter that matches the provided local name.
	 * @param localName the local name
	 * @return the search filter
	 */
	public static PeakPhoneBookSearchFilter matchLocalName(String localName) {
		PeakPhoneBookSearchFilter searchFilter = new PeakPhoneBookSearchFilter();
		searchFilter.setLocalName(localName);
		return searchFilter;
	}
	
	/**
	 * This factory method creates a search filter that matches the provided component type.
	 * @param componentType the component type
	 * @return the search filter
	 */
	public static PeakPhoneBookSearchFilter matchComponentType(String componentType) {
		PeakPhoneBookSearchFilter searchFilter = new PeakPhoneBookSearchFilter();
		searchFilter.setComponentType(componentType);
		return searchFilter;
	}
	
	/**
	 * This factory member creates a search filter that matches the specified member ID. 
	 * @param peakMemberID the peak member ID
	 * @return the peak phone book search filter
	 */
	public static PeakPhoneBookSearchFilter matchPeakMemberID(int peakMemberID) {
		PeakPhoneBookSearchFilter searchFilter = new PeakPhoneBookSearchFilter();
		searchFilter.setPeakMemberID(peakMemberID);
		return searchFilter;
	}
	
	/**
	 * This factory member creates a search filter that matches the specified peak user ID.
	 * @param peakUserID the peak user ID
	 * @return the peak phone book search filter
	 */
	public static PeakPhoneBookSearchFilter matchPeakUserID(String peakUserID) {
		PeakPhoneBookSearchFilter searchFilter = new PeakPhoneBookSearchFilter();
		searchFilter.setPeakUserID(peakUserID);
		return searchFilter;
	}
	
	/**
	 * This factory member creates a search filter that matches the specified node ID.
	 * @param nodeID the node ID
	 * @return the peak phone book search filter
	 */
	public static PeakPhoneBookSearchFilter matchNodeID(String nodeID) {
		PeakPhoneBookSearchFilter searchFilter = new PeakPhoneBookSearchFilter();
		searchFilter.setNodeID(nodeID);
		return searchFilter;
	}
	
	/**
	 * This factory member creates a search filter that matches the specified grid ID.
	 * @param gridID the grid ID
	 * @return the peak phone book search filter
	 */
	public static PeakPhoneBookSearchFilter matchGridID(String gridID) {
		PeakPhoneBookSearchFilter searchFilter = new PeakPhoneBookSearchFilter();
		searchFilter.setGridID(gridID);
		return searchFilter;
	}
	
	/**
	 * This factory member creates a search filter that matches the specified peak role.
	 * @param peakRole the peak role
	 * @return the peak phone book search filter
	 */
	public static PeakPhoneBookSearchFilter matchPeakRole(PeakRole peakRole) {
		PeakPhoneBookSearchFilter searchFilter = new PeakPhoneBookSearchFilter();
		searchFilter.setPeakRole(peakRole);
		return searchFilter;
	}
	
	/**
	 * Gets the agent AID.
	 * @return the agent AID
	 */
	public AID getAgentAID() {
		return agentAID;
	}
	/**
	 * Sets the agent AID.
	 * @param agentAID the new agent AID
	 */
	public PeakPhoneBookSearchFilter setAgentAID(AID agentAID) {
		this.agentAID = agentAID;
		return this;
	}
	
	/**
	 * Gets the local name.
	 * @return the local name
	 */
	public String getLocalName() {
		return localName;
	}
	/**
	 * Sets the local name.
	 * @param localName the new local name
	 */
	public PeakPhoneBookSearchFilter setLocalName(String localName) {
		this.localName = localName;
		return this;
	}

	/**
	 * Gets the component type.
	 * @return the component type
	 */
	public String getComponentType() {
		return componentType;
	}
	/**
	 * Sets the component type.
	 * @param componentType the new component type
	 */
	public PeakPhoneBookSearchFilter setComponentType(String componentType) {
		this.componentType = componentType;
		return this;
	}
	
	/**
	 * Gets the peak member ID.
	 * @return the peak member ID
	 */
	public Integer getPeakMemberID() {
		return peakMemberID;
	}
	/**
	 * Sets the peak member ID.
	 * @param peakMemberID the new peak member ID
	 */
	public PeakPhoneBookSearchFilter setPeakMemberID(Integer peakMemberID) {
		this.peakMemberID = peakMemberID;
		return this;
	}

	/**
	 * Gets the peak user ID.
	 * @return the peak user ID
	 */
	public String getPeakUserID() {
		return peakUserID;
	}
	/**
	 * Sets the peak user ID.
	 * @param peakUserID the new peak user ID
	 */
	public PeakPhoneBookSearchFilter setPeakUserID(String peakUserID) {
		this.peakUserID = peakUserID;
		return this;
	}

	/**
	 * Gets the node ID.
	 * @return the node ID
	 */
	public String getNodeID() {
		return nodeID;
	}
	/**
	 * Sets the node ID.
	 * @param nodeID the new node ID
	 */
	public PeakPhoneBookSearchFilter setNodeID(String nodeID) {
		this.nodeID = nodeID;
		return this;
	}

	/**
	 * Gets the grid ID.
	 * @return the grid ID
	 */
	public String getGridID() {
		return gridID;
	}
	/**
	 * Sets the grid ID.
	 * @param gridID the new grid ID
	 */
	public PeakPhoneBookSearchFilter setGridID(String gridID) {
		this.gridID = gridID;
		return this;
	}

	/**
	 * Gets the peak role.
	 * @return the peak role
	 */
	public PeakRole getPeakRole() {
		return peakRole;
	}
	/**
	 * Sets the peak role.
	 * @param peakRole the new peak role
	 */
	public PeakPhoneBookSearchFilter setPeakRole(PeakRole peakRole) {
		this.peakRole = peakRole;
		return this;
	}

	/* (non-Javadoc)
	 * @see de.enflexit.jade.phonebook.search.PhoneBookSearchFilter#matches(de.enflexit.jade.phonebook.AbstractPhoneBookEntry)
	 */
	@Override
	public boolean matches(PeakPhoneBookEntry entry) {
		
		// --- Is a filter defined for the member ID? -------------------------
		if (this.getAgentAID()!=null) {
			if (entry.getAgentAID()==null || entry.getAgentAID().equals(this.getAgentAID())==false) {
				// --- No match if the entry has no or a different AID --------
				return false;
			}
		}

		// --- See first check for comments -------------------------
		if (this.getLocalName()!=null) {
			if (entry.getAgentAID()==null || entry.getAgentAID().getLocalName().equals(this.getLocalName())==false) {
				return false;
			}
			
		}
		
		// --- See first check for comments -------------------------
		if (this.getComponentType()!=null) {
			if (entry.getComponentType()==null || entry.getComponentType().equals(this.getComponentType())==false) {
				return false;
			}
		}
		
		// --- See first check for comments -------------------------
		if (this.getPeakMemberID()!=null) {
			if (entry.getPeakMemberID()==null || entry.getPeakMemberID().equals(this.getPeakMemberID())==false) {
				return false;
			}
		}
		
		// --- See first check for comments -------------------------
		if (this.getPeakUserID()!=null) {
			if (entry.getPeakUserID()==null || entry.getPeakUserID().equals(this.getPeakUserID())==false) {
				return false;
			}
		}
		
		// --- See first check for comments -------------------------
		if (this.getNodeID()!=null) {
			if (entry.getNodeID()==null || entry.getNodeID().equals(this.getNodeID())==false) {
				return false;
			}
		}
		
		// --- See first check for comments -------------------------
		if (this.getGridID()!=null) {
			if (entry.getGridID()==null || entry.getGridID().equals(this.getGridID())==false) {
				return false;
			}
		}
		
		// --- See first check for comments -------------------------
		if (this.getPeakRole()!=null) {
			if (entry.getPeakRole()==null || entry.getPeakRole()!=this.getPeakRole()) {
				return false;
			}
		}
		
		// --- All checks passed -> match ---------------------------
		return true;
	}
	

}
