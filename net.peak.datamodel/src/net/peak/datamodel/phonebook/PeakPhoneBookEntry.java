package net.peak.datamodel.phonebook;

import de.enflexit.ea.core.dataModel.phonebook.EnergyAgentPhoneBookEntry;
import de.enflexit.geography.coordinates.AbstractGeoCoordinate;
import de.enflexit.jade.phonebook.AbstractPhoneBookEntry;

/**
 * {@link AbstractPhoneBookEntry} implementation for the peak project.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 *
 */
public class PeakPhoneBookEntry extends EnergyAgentPhoneBookEntry {
	
	private static final long serialVersionUID = 2678533379782796614L;

	public static enum PeakRole{
		PROSUMER_AGENT, MARKET_AGENT, GRID_AGENT, PLATFORM_AGENT, SGL_AGENT, MAPPER_AGENT
	}
	
	private Integer peakMemberID;
	private String peakUserID;
	private AbstractGeoCoordinate location;
	private String nodeID;
	private String gridID;
	
	private PeakRole peakRole;
	
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
	public void setPeakMemberID(Integer peakMemberID) {
		this.peakMemberID = peakMemberID;
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
	public void setPeakUserID(String peakUserID) {
		this.peakUserID = peakUserID;
	}
	
	/**
	 * Gets the location.
	 * @return the location
	 */
	public AbstractGeoCoordinate getLocation() {
		return location;
	}
	
	/**
	 * Sets the location.
	 * @param location the new location
	 */
	public void setLocation(AbstractGeoCoordinate location) {
		this.location = location;
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
	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
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
	public void setGridID(String gridID) {
		this.gridID = gridID;
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
	public void setPeakRole(PeakRole peakRole) {
		this.peakRole = peakRole;
	}

	/* (non-Javadoc)
	 * @see de.enflexit.jade.phonebook.AbstractPhoneBookEntry#isValid()
	 */
	@Override
	public boolean isValid() {
		// --- Make sure at least the AID and role are specified ----
		return (this.getAgentAID()!=null&&this.peakRole!=null);
	}

	
}
