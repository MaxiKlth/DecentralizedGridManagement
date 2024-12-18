package net.peak.agent.ConnetionCRIEPILab;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.peak.agent.energyTradingAgent.EnergyTradingAgent;

/**
 * The SendControlSignalsToCRIEPILab class is responsible for sending control signals 
 * to the CRIEPI lab power supplies over a network. This includes initializing the 
 * connection, sending voltage and current commands, and closing the connection.
 */
public class SendControlSignalsToCRIEPILab {

    private static final String[] IP_ADDRESSES = {
        "192.168.4.3", "192.168.4.5", "192.168.3.2",
        "192.168.3.3", "192.168.2.2", "192.168.2.3"
    }; // [0] = P1, [1] = Q1, [2] = P2, [3] = Q2, [4] = P3, [5] = Q3

    private static final int[] PORTS = {
        2268, 2268, 2268, 2268, 2268, 2268
    }; // [0] = P1, [1] = Q1, [2] = P2, [3] = Q2, [4] = P3, [5] = Q3

    private static Map<Integer, Integer> agentPortMapActivePower = new HashMap<>();

    static {
        agentPortMapActivePower.put(1000, 0);
        agentPortMapActivePower.put(2000, 2);
        agentPortMapActivePower.put(3000, 4);
    }

    // Method to get the port index for active power based on the agent ID
    public static int getSecondColumnValueActivePower(int firstColumnValue) {
        return agentPortMapActivePower.getOrDefault(firstColumnValue, -1);
    }
    
    private static Map<Integer, Integer> agentPortMapReactivePower = new HashMap<>();

    static {
        agentPortMapReactivePower.put(1000, 1);
        agentPortMapReactivePower.put(2000, 3);
        agentPortMapReactivePower.put(3000, 5);
    }

    // Method to get the port index for reactive power based on the agent ID
    public static int getSecondColumnValueReactivePower(int firstColumnValue) {
        return agentPortMapReactivePower.getOrDefault(firstColumnValue, -1);
    }

    private Socket[] sockets = new Socket[6];
    private DataOutputStream[] outputStreams = new DataOutputStream[6];
    private BufferedReader[] inputStreams = new BufferedReader[6];

    // Initializes the connection to the power supply at the specified index
    public void initialize(int index) {
        try {
            sockets[index] = new Socket(IP_ADDRESSES[index], PORTS[index]);
            outputStreams[index] = new DataOutputStream(sockets[index].getOutputStream());
            inputStreams[index] = new BufferedReader(new InputStreamReader(sockets[index].getInputStream()));
            System.out.println("Connected to the power supply at " + IP_ADDRESSES[index] + ":" + PORTS[index]);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    // Sends a command to the power supply at the specified index
    public void sendCommand(int index, String command) {
        try {
            outputStreams[index].writeBytes(command + "\n");
            outputStreams[index].flush();
            System.out.println("Sent command: " + command);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    // Reads the response from the power supply at the specified index
    public String readResponse(int index) {
        try {
            String response = inputStreams[index].readLine();
            System.out.println("Received response: " + response);
            return response;
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            return null;
        }
    }

    // Closes the connection to the power supply at the specified index
    public void close(int index) {
        try {
            if (outputStreams[index] != null) {
                outputStreams[index].close();
            }
            if (inputStreams[index] != null) {
                inputStreams[index].close();
            }
            if (sockets[index] != null) {
                sockets[index].close();
            }
            System.out.println("Disconnected from the power supply.");
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    // Main method for sending control signals to the CRIEPI lab power supplies
    public void main(String[] args, double voltage, double current, EnergyTradingAgent energyTradingAgent, String tradeTyp) {
        SendControlSignalsToCRIEPILab lanComm = new SendControlSignalsToCRIEPILab();
        SendControlSignalsToCRIEPILab lanCommReactive = new SendControlSignalsToCRIEPILab();
        
        int aidAgent = Integer.parseInt(energyTradingAgent.getLocalName());
        int portValueReactive = getSecondColumnValueReactivePower(aidAgent);
        int portValue = getSecondColumnValueActivePower(aidAgent);
        
        // Initialize connections
        lanComm.initialize(portValue);
        lanCommReactive.initialize(portValueReactive);
        
        // Format the commands to be sent
        String voltageCommand = formatCommand("VOLT", voltage);
        String voltageCommandReactive;
        if (aidAgent == 3000) {
            voltageCommandReactive = formatCommand("CURR", 4.7);
        } else {
            voltageCommandReactive = formatCommand("VOLT", 4.7);
        }
        
        // Send commands to set voltage and reactive power
        lanComm.sendCommand(portValue, voltageCommand);
        lanCommReactive.sendCommand(portValueReactive, voltageCommandReactive);
        
        // Activate output
        lanComm.sendCommand(portValue, "OUTP ON");
        lanCommReactive.sendCommand(portValueReactive, "OUTP ON");
        
        // Close connections
        lanComm.close(portValue);
        lanCommReactive.close(portValueReactive);
    }
    
    // Formats the command for voltage or current
    private static String formatCommand(String commandType, double value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("0.00", symbols);
        return commandType + " " + df.format(value);
    }
}
