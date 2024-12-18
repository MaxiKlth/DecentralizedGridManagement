package net.peak.agent.energyTradingAgent;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import net.peak.datamodel.configuration.PeakConfiguration;

public class MainContainer {

    public static void main(String[] args) {
        // Load configuration and environment variables
        PeakConfiguration config = new PeakConfiguration();
        String mainHost = System.getenv("MAIN_HOST"); // Host of the main JADE container
        String mainPort = System.getenv("MAIN_PORT"); // Port of the main JADE container
        String host = System.getenv("HOST_IP");  // IP address of this Raspberry Pi
        String mtpHost = System.getenv().getOrDefault("MTP_HOST", mainHost); // MTP host for message transport
        String mtpPort = System.getenv().getOrDefault("MTP_PORT", "7778"); // MTP port for message transport

        // Initial agent ID values
        int agentStartId = 1000;  // Start ID for EnergyTradingAgents
        int agentStartIdCMA = 1;  // Start ID for CongestionManagingAgents (CMAs)

        // Initialize JADE runtime and main container profile
        Runtime rt = Runtime.instance();
        Profile pMain = new ProfileImpl();
        pMain.setParameter(Profile.MAIN_HOST, mainHost != null ? mainHost : "localhost");
        pMain.setParameter(Profile.MAIN_PORT, mainPort != null ? mainPort : "1099");
        pMain.setParameter(Profile.MTPS, "jade.mtp.http.MessageTransportProtocol(http://" + mtpHost + ":" + mtpPort + "/acc)");
        pMain.setParameter(Profile.GUI, "true"); // Enable GUI for the JADE container

        // Create the main container
        AgentContainer mainContainer = rt.createMainContainer(pMain);

        // Start the AMS agent if this host is the AMS host
        if (mainHost.equals(System.getenv("AMS_IP"))) {
            try {
                AgentController amsAgent = mainContainer.createNewAgent("energyAMS", "net.peak.agent.energyTradingAgent.EnergyAMS", null);
                amsAgent.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }

        // Start the EnergyTradingAgents and CongestionManagingAgents (CMAs)
        try {
            int agentCount = 0; // Counter for the number of agents started
            int raspberryCount = config.raspberryIPs.size(); // Number of Raspberry Pi devices

            for (int i = 1; i <= config.numberOfAgents; i++) {
                // Calculate the Raspberry Pi index for this agent
                int raspberryIndex = (i - 1) % raspberryCount;

                // Debugging: Output the current Raspberry Pi and agent index
                System.out.println("Agent Index: " + i + ", Raspberry Index: " + raspberryIndex);

                // Check if the current host matches the Raspberry Pi's IP
                if (config.raspberryIPs.get(raspberryIndex).equals(host)) {
                    // Start EnergyTradingAgent
                    String agentName = (agentStartId + (i - 1) * 1000) + ""; // Generate unique agent name
                    AgentController agent = mainContainer.createNewAgent(agentName, "net.peak.agent.energyTradingAgent.EnergyTradingAgent", null);
                    agent.start();
                    
                    // Start CongestionManagingAgent (CMA)
                    String agentNameCMA = (agentStartIdCMA + (i - 1)) + ""; // Generate unique CMA name
                    AgentController agentCMA = mainContainer.createNewAgent(agentNameCMA, "net.peak.agent.flexibilityTradingAgent.CongestionManagingAgent", null);
                    agentCMA.start();
                    
                    agentCount++; // Increment the agent count
                }
            }
            // Output the total number of agents started on this host
            System.out.println("Started " + agentCount + " EDAs and CMAs on host " + host);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
