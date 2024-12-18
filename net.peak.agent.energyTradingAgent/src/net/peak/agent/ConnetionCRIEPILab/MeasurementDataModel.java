package net.peak.agent.ConnetionCRIEPILab;

/**
 * The MeasurementDataModel class represents the various electrical measurements collected from the CRIEPI lab.
 * It stores data such as voltage, current, power, and other related metrics.
 */
public class MeasurementDataModel {
    
    // Electrical measurements
    private float voltageRMS;
    private float currentRMS;
    private float activePower;
    private float activePowerP2;
    private float activePowerP12;
    private float powerFactor;
    private float apparentPower;
    private float reactivePower;
    private float phaseAngle;
    private float frequency;

    /**
     * Constructor to initialize the MeasurementDataModel with all the relevant measurement parameters.
     *
     * @param voltageRMS     The RMS voltage value.
     * @param currentRMS     The RMS current value.
     * @param activePower    The active power.
     * @param activePowerP2  The active power on phase 2.
     * @param activePowerP12 The active power on phase 12.
     * @param powerFactor    The power factor.
     * @param apparentPower  The apparent power.
     * @param reactivePower  The reactive power.
     * @param phaseAngle     The phase angle.
     * @param frequency      The frequency.
     */
    public MeasurementDataModel(float voltageRMS, float currentRMS, float activePower, float activePowerP2, float activePowerP12, float powerFactor, float apparentPower, float reactivePower, float phaseAngle, float frequency) {
        this.voltageRMS = voltageRMS;
        this.currentRMS = currentRMS;
        this.activePower = activePower;
        this.activePowerP2 = activePowerP2;
        this.activePowerP12 = activePowerP12;
        this.powerFactor = powerFactor;
        this.apparentPower = apparentPower;
        this.reactivePower = reactivePower;
        this.phaseAngle = phaseAngle;
        this.frequency = frequency;
    }

    // Getter and setter methods for each measurement field

    public float getActivePowerP2() {
        return activePowerP2;
    }

    public void setActivePowerP2(float activePowerP2) {
        this.activePowerP2 = activePowerP2;
    }

    public float getActivePowerP12() {
        return activePowerP12;
    }

    public void setActivePowerP12(float activePowerP12) {
        this.activePowerP12 = activePowerP12;
    }

    public float getVoltageRMS() {
        return voltageRMS;
    }

    public void setVoltageRMS(float voltageRMS) {
        this.voltageRMS = voltageRMS;
    }

    public float getCurrentRMS() {
        return currentRMS;
    }

    public void setCurrentRMS(float currentRMS) {
        this.currentRMS = currentRMS;
    }

    public float getActivePower() {
        return activePower;
    }

    public void setActivePower(float activePower) {
        this.activePower = activePower;
    }

    public float getPowerFactor() {
        return powerFactor;
    }

    public void setPowerFactor(float powerFactor) {
        this.powerFactor = powerFactor;
    }

    public float getApparentPower() {
        return apparentPower;
    }

    public void setApparentPower(float apparentPower) {
        this.apparentPower = apparentPower;
    }

    public float getReactivePower() {
        return reactivePower;
    }

    public void setReactivePower(float reactivePower) {
        this.reactivePower = reactivePower;
    }

    public float getPhaseAngle() {
        return phaseAngle;
    }

    public void setPhaseAngle(float phaseAngle) {
        this.phaseAngle = phaseAngle;
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    /**
     * Converts the measurement data to a CSV (Comma-Separated Values) format.
     *
     * @return A string representing the measurement data in CSV format.
     */
    public String toCSV() {
        return voltageRMS + "," + currentRMS + "," + activePower + "," + powerFactor + "," + apparentPower + "," + reactivePower + "," + phaseAngle + "," + frequency;
    }

    /**
     * Provides the CSV header corresponding to the measurement data.
     *
     * @return A string representing the CSV header.
     */
    public static String getCSVHeader() {
        return "VoltageRMS,CurrentRMS,ActivePower,PowerFactor,ApparentPower,ReactivePower,PhaseAngle,Frequency";
    }
}
