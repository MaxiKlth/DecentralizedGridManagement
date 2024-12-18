package net.peak.datamodel.sgl;

import java.io.Serializable;

/**
 * The Class SglMeasurment describes a single measurement of the SGL environment.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class SglMeasurment implements Serializable {

	private static final long serialVersionUID = -8543682134967928756L;

	private long timeStamp;
	private String name;
	private String datapointType;
	private String type;
	
	private double value;
	private String unit;
	private String phase;
	
	
	/**
	 * Gets the time stamp.
	 * @return the time stamp
	 */
	public long getTimeStamp() {
		return timeStamp;
	}
	/**
	 * Sets the time stamp.
	 * @param timeStamp the new time stamp
	 */
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	/**
	 * Gets the name.
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Sets the name.
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the datapoint type.
	 * @return the datapoint type
	 */
	public String getDatapointType() {
		return datapointType;
	}
	/**
	 * Sets the datapoint type.
	 * @param datapointType the new datapoint type
	 */
	public void setDatapointType(String datapointType) {
		this.datapointType = datapointType;
	}
	
	/**
	 * Gets the type.
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * Sets the type.
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Gets the value.
	 * @return the value
	 */
	public double getValue() {
		return value;
	}
	/**
	 * Sets the value.
	 * @param value the new value
	 */
	public void setValue(double value) {
		this.value = value;
	}
	
	/**
	 * Gets the unit.
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}
	/**
	 * Sets the unit.
	 * @param unit the new unit
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	/**
	 * Gets the phase.
	 * @return the phase
	 */
	public String getPhase() {
		return phase;
	}
	/**
	 * Sets the phase.
	 * @param phase the new phase
	 */
	public void setPhase(String phase) {
		this.phase = phase;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj==null || !(obj instanceof SglMeasurment)) return false;
		if (obj==this) return true;
		
		SglMeasurment sglmComp = (SglMeasurment) obj;
		if (sglmComp.getName().equals(this.getName())==false) return false;
		return sglmComp.getValue()==this.getValue();
	}
	
}
