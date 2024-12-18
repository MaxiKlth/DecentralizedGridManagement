package net.peak.agent.ConnetionCRIEPILab;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import jade.core.behaviours.OneShotBehaviour;
import net.peak.agent.energyTradingAgent.EnergyTradingAgent;

/**
 * The ReceiveMeasureDataOfCRIEPILab class is responsible for connecting to a CRIEPI lab device,
 * reading measurement data, and calculating energy consumption based on these measurements.
 */
public class ReceiveMeasureDataOfCRIEPILab extends OneShotBehaviour {
    
    // The IP address of the CRIEPI lab device
    private String HOST;
    
    // The port to connect to for data reading
    private static final int PORT = 3390;
    
    // Total energy calculated based on the measurements
    private float totalEnergy = 0.0f;
    
    // A map of agent IDs to their respective IP addresses
    private static Map<Integer, String> agentIPAdressMap = new HashMap<>();
    
    // The measurements collected over different periods
    private Map<Integer, Map<Integer, MeasurementDataModel>> periodMeasurements;
    
    // The agent responsible for energy trading
    private EnergyTradingAgent energyTradingAgent;
    
    // Constructor to initialize the agent and its corresponding IP address
    public ReceiveMeasureDataOfCRIEPILab(EnergyTradingAgent energyTradingAgent) {
        this.energyTradingAgent = energyTradingAgent;
        int aidAgent = Integer.parseInt(this.energyTradingAgent.getLocalName());
        HOST = agentIPAdressMap.get(aidAgent);
    }

    // Static block to initialize the agent IP address map
    static {
        agentIPAdressMap.put(1000, "192.168.1.4");
        agentIPAdressMap.put(2000, "192.168.3.4");
        agentIPAdressMap.put(3000, "192.168.2.4");
    }

    /**
     * Reads the current measurement and calculates the energy based on the provided time divider.
     *
     * @param agentName   The agent's name (or ID).
     * @param timeDivider The time divider for energy calculation.
     */
    public void readAndCalculateEnergy(int agentName, float timeDivider) {
        HOST = agentIPAdressMap.get(agentName);

        try {
            float current = readCurrent(HOST);
            totalEnergy = calculateEnergy(current, timeDivider);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the total energy calculated for the agent.
     *
     * @param agentName   The agent's name (or ID).
     * @param timeDivider The time divider for energy calculation.
     * @return The total energy.
     */
    public float getTotalEnergy(int agentName, float timeDivider) {
        readAndCalculateEnergy(agentName, timeDivider);
        return totalEnergy;
    }

    /**
     * Reads the current value from the CRIEPI lab device.
     *
     * @param host The IP address of the CRIEPI lab device.
     * @return The current value in amperes.
     * @throws Exception If there is an error in reading the value.
     */
    private float readCurrent(String host) throws Exception {
        try (Socket socket = new Socket(host, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send the command to query the current value with a \r\n terminator
            out.print(":MEAS? P1\r\n");
            out.flush();

            // Set a timeout of 10 seconds
            socket.setSoTimeout(1000);

            // Wait for the response
            String response = in.readLine();

            if (response == null || response.isEmpty()) {
                throw new Exception("Received an empty response from the device");
            }

            return Float.parseFloat(response.trim());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Calculates the energy based on the power and time divider.
     *
     * @param power       The power in watts.
     * @param timeDivider The time divider to adjust the energy calculation.
     * @return The calculated energy in watt-seconds.
     */
    private float calculateEnergy(float power, float timeDivider) {
        return power / timeDivider;
    }

    /**
     * Sends a command to the CRIEPI lab device and returns the measurement result.
     *
     * @param command The command to send to the device.
     * @return The measurement result.
     * @throws Exception If there is an error in reading the value.
     */
    private float getMeasurement(String command) throws Exception {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send the command to query the value with a \r\n terminator
            out.print(":MEAS? " + command + "\r\n");
            out.flush();

            // Set a timeout of 10 seconds
            socket.setSoTimeout(1000);

            // Wait for the response
            String response = in.readLine();

            if (response == null || response.isEmpty()) {
                throw new Exception("Received an empty response from the device");
            }

            return Float.parseFloat(response.trim());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Collects all measurements from the CRIEPI lab device.
     *
     * @return A MeasurementDataModel object containing all the measurements.
     */
    public MeasurementDataModel collectMeasurements() {
        float voltageRMS = 0;
        float currentRMS = 0;
        float activePower = 0;
        float activePowerP2 = 0;
        float activePowerP12 = 0;
        float powerFactor = 0;
        float apparentPower = 0;
        float reactivePower = 0;
        float phaseAngle = 0;
        float frequency = 0;

        try {
            voltageRMS = getMeasurement("Urms1");
            currentRMS = getMeasurement("Irms1");
            activePower = getMeasurement("P1");
            activePowerP2 = getMeasurement("P2");
            activePowerP12 = getMeasurement("P12");
            powerFactor = getMeasurement("PF1");
            apparentPower = getMeasurement("S1");
            reactivePower = getMeasurement("Q1");
            phaseAngle = getMeasurement("DEG1");
            frequency = getMeasurement("FREQ1");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new MeasurementDataModel(voltageRMS, currentRMS, activePower, activePowerP2, activePowerP12, powerFactor, apparentPower, reactivePower, phaseAngle, frequency);
    }

    @Override
    public void action() {
        // This method can be used to define the behavior when the object is executed as part of a JADE behavior.
    }
}
