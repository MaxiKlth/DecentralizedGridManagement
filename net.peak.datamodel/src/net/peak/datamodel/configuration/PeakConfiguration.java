package net.peak.datamodel.configuration;

import java.util.Arrays;
import java.util.List;

import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;

public class PeakConfiguration {

	// Trading cycle duration in milliseconds
    public int tradingCyclingInSeconds = 4500; // in milliseconds

    // Trading interval duration in milliseconds
    public int tradingIntervallInSeconds = 30000; // in milliseconds

    // Voltage level for the simulation
    public int voltage = 230;

    // Maximum current allowed in the system
    public double maxCurrent = 10000;

    // Resolution of the simulation in minutes (only 1 and 15 minutes supported, default is 15)
    public int simulationResolutionInMin = 15;

    // Type of simulation to be used (e.g., IEEE5)
    public SimulationTypeEnum simulationTypeEnum = SimulationTypeEnum.IEEE33;

    // Season for the simulation, affecting PV and consumption patterns
    public SeasonEnum seasonEnum = SeasonEnum.SUMMER;

    // Percentage of nodes with PV installations
    public double percentagePV = 0.8;

    // Percentage of nodes with heat pumps
    public double percentageHeatPump = 1;

    // Percentage of nodes with electric vehicles (EVs)
    public double percentageEV = 0.4;

    // Decomposition rate for ADMM algorithm
    public double decenteRateADMM = 0.7;

    // Decomposition rate for energy dispatch
    public double decentRateED = 0.7;

    // Maximum number of iterations to wait for convergence
    public int maxWaitingIteration = 20;

    // Time divider used in energy calculations
    public double timeDivider = 4;

    // Indicates whether congestion management is active
    public boolean congestionManagementActive = true;

    // Indicates if trading starts from the first iteration
    public boolean tradingStartsFromFirstIteration = false;

    // Indicates if storage systems are active in the simulation
    public boolean storageOn = false;

    // AID (Agent Identifier) of the target agent in the simulation
    private AID targetAgent; 

    // Number of agents in the simulation
    public int numberOfAgents;

    // Multiplicator for PV generation
    public double pvMultiplicator = 4;

    // IP address of the host machine
    public String ownIPAddress = "192.168.0.51";

    // List of IP addresses of Raspberry Pis in the simulation setup
    public List<String> raspberryIPs;

    // Type of computer running the simulation
    public ComputerTypeEnum computerTypeEnum = ComputerTypeEnum.KILTHAU_HSU;
    
    // Constructor to initialize the configuration based on the environment
    public PeakConfiguration() {
        computerTypeEnum = this.getComputerType();
        if (this.computerTypeEnum.equals(ComputerTypeEnum.DOCKER)) {
            // Load configuration values from environment variables if running in a Docker environment
            this.tradingCyclingInSeconds = Integer.parseInt(System.getenv().getOrDefault("TRADING_CYCLING_IN_SECONDS", "3000"));
            this.tradingIntervallInSeconds = Integer.parseInt(System.getenv().getOrDefault("TRADING_INTERVALL_IN_SECONDS", "50000"));
            this.voltage = Integer.parseInt(System.getenv().getOrDefault("VOLTAGE", "100"));
            this.maxCurrent = Double.parseDouble(System.getenv().getOrDefault("MAX_CURRENT", "10"));
            this.simulationResolutionInMin = Integer.parseInt(System.getenv().getOrDefault("SIMULATION_RESOLUTION_IN_MIN", "15"));
            this.simulationTypeEnum = SimulationTypeEnum.valueOf(System.getenv().getOrDefault("SIMULATION_TYPE_ENUM", "IEEE5"));
            this.decenteRateADMM = Double.parseDouble(System.getenv().getOrDefault("DECENTE_RATE_ADMM", "0.3"));
            this.maxWaitingIteration = Integer.parseInt(System.getenv().getOrDefault("MAX_WAITING_ITERATION", "20"));
            this.timeDivider = Double.parseDouble(System.getenv().getOrDefault("TIME_DIVIDER", "4"));
            this.congestionManagementActive = Boolean.parseBoolean(System.getenv().getOrDefault("CONGESTION_MANAGEMENT_ACTIVE", "false"));
            this.tradingStartsFromFirstIteration = Boolean.parseBoolean(System.getenv().getOrDefault("TRADING_FIRST_ITERATION", "true"));
            this.numberOfAgents = setNumberNodes();
            this.ownIPAddress = System.getenv("MAIN_HOST");
            this.targetAgent = getTargetAID();

            // Set the Jade pool size if specified
            String poolSize = System.getenv().getOrDefault("JADE_POOLSIZE", "1000");
            Profile profile = new ProfileImpl();
            if (poolSize != null) {
                profile.setParameter("jade_core_messaging_MessageManager_poolsize", poolSize);
            }

            // Parse the centralized configuration for Raspberry Pis and the number of agents
            String config = System.getenv("RASPBERRY_CONFIG");
            String[] parts = config.split(";");
            this.raspberryIPs = Arrays.asList(parts[0].split(","));
            this.numberOfAgents = Integer.parseInt(parts[1]);
        }
    }

    // Method to get the type of computer running the simulation
    private ComputerTypeEnum getComputerType() {
        return this.computerTypeEnum;
    }

    // Method to get the target AID (Agent Identifier) for communication
    public AID getTargetAID() {
        String ipAddress;
        if (this.computerTypeEnum.equals(ComputerTypeEnum.DOCKER)) {
            ipAddress = System.getenv().getOrDefault("AMS_IP", "192.168.0.53");
            targetAgent = new AID("energyAMS@" + ipAddress + ":1099/JADE", AID.ISGUID);
            targetAgent.addAddresses("http://" + ipAddress + ":7778/acc");
        } else {
            ipAddress = System.getenv().getOrDefault("AMS_IP", "172.28.112.1");
            targetAgent = new AID("energyAMS@" + ipAddress + ":7778/JADE", AID.ISGUID);
        }
        
        return targetAgent;
    }
    
	
	private String metaFolderPathResults = getPathOutput();  
	private String metaFolderPathInputData =  getPathInput();
	
	
	private String getPathInput() {
		ComputerTypeEnum type = this.computerTypeEnum;
        switch (type) {
            case KILTHAU_HSU:
                return "D:\\Git\\peak\\eclipseProjects\\net.peak\\bundles\\net.peak.agent.energyTradingAgent\\csv\\";
            case KILTHAU_PRIVATE:
                return "D:\\Git\\SimulationPromotion\\de.data.energy\\";
            case DOCKER:
                return "/JavaApp/csv/";
            default:
            	return "D:\\Git\\SimulationPromotion\\de.data.energy\\";
        }
	}
	
	private String getPathOutput() {
		ComputerTypeEnum type = this.computerTypeEnum;
        switch (type) {
            case KILTHAU_HSU:
                return "D:\\Git\\peak\\eclipseProjects\\net.peak\\bundles\\net.peak.agent.energyTradingAgent\\csv\\";
            case KILTHAU_PRIVATE:
                return "D:\\Git\\SimulationPromotion\\de.data.energy\\";
            case DOCKER:
                return "/JavaApp/output/";
            default:
            	return "D:\\Git\\SimulationPromotion\\de.data.energy\\";
        }
	}
	
	public int setNumberNodes() {
		SimulationTypeEnum type = this.getSimulationTypeEnum();
        switch (type) {
            case IEEE5:
                return 5;
            case IEEE10:
            	return 10;
            case IEEE33:
                return 33;
            case IEEE119:
                return 119;
            case SIMULATIONCRIEPI:
                return 10;
            case TESTCRIEPI:
                return 3;
            default:
                // Default case can be set to an error value or zero,
                // depending on how you want to handle an undefined type
                return -1; 
        }
    }
	
	

	public int getAmountPeriods() {
		int amountPeriods = 95;
		if (this.simulationResolutionInMin==1) {
			amountPeriods = 1440;
		} 

		return amountPeriods;
	}
	
	
	public String getMetafolderpathresults() {
		return metaFolderPathResults;
	}

	public String getMetafolderpathinputdata() {
		return metaFolderPathInputData;
	}

	public int getVoltage() {
		return voltage;
	}

	public void setVoltage(int voltage) {
		this.voltage = voltage;
	}

	public double getMaxCurrent() {
		return maxCurrent;
	}

	public void setMaxCurrent(double maxCurrent) {
		this.maxCurrent = maxCurrent;
	}

	public int getSimulationResolutionInMin() {
		return simulationResolutionInMin;
	}

	public void setSimulationResolutionInMin(int simulationResolutionInMin) {
		this.simulationResolutionInMin = simulationResolutionInMin;
	}

	public SimulationTypeEnum getSimulationTypeEnum() {
		return simulationTypeEnum;
	}

	public void setSimulationTypeEnum(SimulationTypeEnum simulationTypeEnum) {
		this.simulationTypeEnum = simulationTypeEnum;
	}

	public double getDecenteRateADMM() {
		return decenteRateADMM;
	}

	public void setDecenteRateADMM(double decenteRateADMM) {
		this.decenteRateADMM = decenteRateADMM;
	}

	public enum SimulationTypeEnum {
	    IEEE33("IEEE33"),
	    
	    IEEE119("IEEE119"),
	    
	    IEEE5("IEEE5"),
	    
	    IEEE10("IEEE10"),
	    
	    SIMULATIONCRIEPI("Simulation_Criepi"),
		
		TESTCRIEPI("Test_Criepi"),
		
		AGENTLAB("AgentLAB");

	    private String value;

	    SimulationTypeEnum(String value) {
	    	this.value = value;
		}
	    
        public String getValue() {
            return value;
        }
	}
	
	public enum ComputerTypeEnum {
		KILTHAU_HSU("Kilthau_HSU"),
		KILTHAU_PRIVATE("Kilthau_Private"),
		DOCKER("Docker");
		
		private String value;
		ComputerTypeEnum(String value) {
			this.value = value;
		}
	}
	
	public enum SeasonEnum{
		WINTER("Winter"),
		SUMMER("Summer");
		private String value;
		SeasonEnum (String value){
			this.value = value;
		}
        public String getValue() {
            return value;
        }
	}
	

	
	
	
	public int peakMarketCycleInSeconds = 250000;
	public boolean energyMarketStatus = false;
	public boolean flexibilityMarketStatus = true;

	public int getTradingCyclingInSeconds() {
		return tradingCyclingInSeconds;
	}

	public void setTradingCyclingInSeconds(int tradingCyclingInSeconds) {
		this.tradingCyclingInSeconds = tradingCyclingInSeconds;
	}

	public int getTradingIntervallInSeconds() {
		return tradingIntervallInSeconds;
	}

	public void setTradingIntervallInSeconds(int tradingIntervallInSeconds) {
		this.tradingIntervallInSeconds = tradingIntervallInSeconds;
	}

	public boolean isFlexibilityMarketStatus() {
		return flexibilityMarketStatus;
	}

	public void setFlexibilityMarketStatus(boolean flexibilityMarketStatus) {
		this.flexibilityMarketStatus = flexibilityMarketStatus;
	}

	public boolean isEnergyMarketStatus() {
		return energyMarketStatus;
	}

	public void setEnergyMarketStatus(boolean energyMarketStatus) {
		this.energyMarketStatus = energyMarketStatus;
	}

	public int getPeakMarketCycleInSeconds() {
		return peakMarketCycleInSeconds;
	}

	public void setPeakMarketCycleInSeconds(int peakMarketCycleInSeconds) {
		this.peakMarketCycleInSeconds = peakMarketCycleInSeconds;
	}
}
