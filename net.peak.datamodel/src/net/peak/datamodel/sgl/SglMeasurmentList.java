package net.peak.datamodel.sgl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * The Class SglMeasurmentList.
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class SglMeasurmentList implements Serializable {

	private static final long serialVersionUID = -7948229610821787759L;
	
	private TreeMap<String, SglMeasurment> sglMeasurements;
	
	
	/**
	 * Returns the SGL measurements.
	 * @return the sgl measurements
	 */
	public TreeMap<String, SglMeasurment> getSglMeasurements() {
		if (sglMeasurements==null) {
			sglMeasurements = new TreeMap<>();
		}
		return sglMeasurements;
	}
	
	/**
	 * Adds the SglMeasurenent.
	 * @param sglMeasurment the SglMeasurenent
	 */
	public void addSglMeasurenent(SglMeasurment sglMeasurment) {
		this.getSglMeasurements().put(sglMeasurment.getName(), sglMeasurment);
	}
	
	/**
	 * Returns the SgLMeasurenent for the specified name.
	 *
	 * @param name the name
	 * @return the SglMeasurenent
	 */
	public SglMeasurment getSglMeasurenent(String name) {
		return this.getSglMeasurements().get(name);
	}
	/**
	 * Gets the SglMeasurenent as list.
	 * @return the SglMeasurenent as list
	 */
	public List<SglMeasurment> getSglMeasurmentAsList() {
		return new ArrayList<>(this.getSglMeasurements().values());
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj==null || !(obj instanceof SglMeasurmentList)) return false;
		if (obj==this) return true;
		
		SglMeasurmentList sglmListComp = (SglMeasurmentList) obj;

		if (sglmListComp.getSglMeasurements().size()!=this.getSglMeasurements().size()) return false;
		
		// --- Compare each measurement in the list of measurements
		List<String> nameList = new ArrayList<>(sglmListComp.getSglMeasurements().keySet());
		for (String name : nameList) {
			
			SglMeasurment sglmComp = sglmListComp.getSglMeasurenent(name);
			SglMeasurment sglmLocal= this.getSglMeasurenent(name); 
			if (sglmComp==null && sglmLocal==null) {
				// --- nothing to do here (not false) ---
			} else if ((sglmComp!=null && sglmLocal==null) || (sglmComp==null && sglmLocal!=null)) {
				return false;
			} else {
				if (sglmComp.equals(sglmLocal)==false) {
					return false;
				}
			}
		}
		return true;
	}
	
}
