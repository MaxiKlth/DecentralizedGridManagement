package net.peak.agent.flexibilityTradingAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import agentgui.core.common.AbstractUserObject;
import jade.core.AID;
import net.peak.datamodel.communication.FlexibilityTransaction;

// TODO: Auto-generated Javadoc
public class InternalDataModel extends AbstractUserObject {
	
	private static final long serialVersionUID = 5290543889811221330L;

	// Agent and Transaction Management
	private CongestionManagingAgent congestionManagingAgent;
	private FlexibilityTransaction flexibilityTransaction;
	private HashMap<AID, FlexibilityTransaction> temporaryListFlexibilityTransaction;
	private HashMap<Integer, HashMap<AID, FlexibilityTransaction>> totalListFlexibilityTransaction;
	private HashMap<Integer, HashMap<Integer, HashMap<AID, FlexibilityTransaction>>> overallFlexibilityTransaction;

	// Energy Balance and Calculation Data
	private HashMap<Integer, HashMap<Integer, Double>> plannedEnergyBalanceList;
	private HashMap<Integer, Double> temporaryEnergyBalanceList;
	private HashMap<Integer, HashMap<Integer, Double>> adaptedTemporaryEnergyBalanceList;
	private HashMap<Integer, HashMap<Integer, Double>> currentValuesMap;
	private HashMap<Integer, HashMap<Integer, Double>> currentValuesAfterMatching;
	private HashMap<Integer, Double> calculationTime;
	private HashMap<Integer, Boolean> periodCalculationDone;

	// ADMM (Alternating Direction Method of Multipliers) Related Variables
	private boolean isLeader = false;
	private int iteration = 0;
	private int tradingPeriod = 1;
	private int EDATradingPeriod = 0;
	private int countReceivedMessages = 0;
	private List<AID> phoneBook;

	// ADMM Variables for Calculations
	private double x;
	private double previousX;
	private double z;
	private double previousZ;
	private double minX;
	private double minZ;
	private double lambda;
	private double previousLambda;
	private double previousDiff2ZeroZ;
	private double previousDiff2ZeroX;

	// Control Flags
	private boolean isADMMCalculating = false;
	private boolean recalculateAlreadyDone = false;
	private boolean CMACalculationFinished = true;
	private boolean EnergyResultAlreadySent = false;

	// Timing and Calculation Tracking
	private long startTimeCalculation;
	private long endTimeCalculation;
	private double decentRate;

	// Configuration and Limits
	private String metaFolderPath;
	private int endIteration;
	private int voltage;
	private double maxCurrent;
	private int timeResolution;

	// External Agent Management
	private int maxNodes;
	private AID energyAMDAID = new AID("energyAMS@192.168.0.51:1099/JADE", AID.ISGUID);

	
	
	public AID getEnergyAMDAID() {
		return energyAMDAID;
	}

	public void setEnergyAMDAID(AID energyAMDAID) {
		this.energyAMDAID = energyAMDAID;
	}

	public int getTimeResolution() {
		return timeResolution;
	}

	public void setTimeResolution(int timeResolution) {
		this.timeResolution = timeResolution;
	}

	public double getMaxCurrent() {
		return maxCurrent;
	}

	public void setMaxCurrent(double maxCurrent) {
		this.maxCurrent = maxCurrent;
	}

	public String getMetaFolderPath() {
		return metaFolderPath;
	}

	public void setMetaFolderPath(String metaFolderPath) {
		this.metaFolderPath = metaFolderPath;
	}

	public int getVoltage() {
		return voltage;
	}

	public void setVoltage(int voltage) {
		this.voltage = voltage;
	}

	
	
	public double getDecentRate() {
		return decentRate;
	}

	public void setDecentRate(double decentRate) {
		this.decentRate = decentRate;
	}

	public int getEndIteration() {
		return endIteration;
	}

	public void setEndIteration(int endIteration) {
		this.endIteration = endIteration;
	}

	public HashMap<Integer, Double> getCalculationTime() {
		if (calculationTime==null) {
			calculationTime = new HashMap<Integer, Double>();
		}
		return calculationTime;
	}
	
	public void addCalculationTime (double calculationTimeInSeconds) {
		calculationTime = getCalculationTime();
		int period = getTradingPeriod();
		calculationTime.put(period, calculationTimeInSeconds);
	}

	public void setCalculationTime(HashMap<Integer, Double> calculationTime) {
		this.calculationTime = calculationTime;
	}

	public long getStartTimeCalculation() {
		return startTimeCalculation;
	}

	public void setStartTimeCalculation(long startTimeCalculation) {
		this.startTimeCalculation = startTimeCalculation;
	}

	public long getEndTimeCalculation() {
		return endTimeCalculation;
	}

	public void setEndTimeCalculation(long endTimeCalculation) {
		this.endTimeCalculation = endTimeCalculation;
	}

	public void updateCurrentValuesMap(int tradingPeriod, HashMap<Integer, Double> currentValues, boolean afterMatching) {
	    if (!afterMatching) {
	        getCurrentValuesMap().put(tradingPeriod, currentValues);
	    } else {
	        addCurrentValueAfterMatching(currentValues);
	    }
	}
	
	public HashMap<Integer, HashMap<Integer, Double>> getAdaptedTemporaryEnergyBalanceList() {
		if(adaptedTemporaryEnergyBalanceList==null) {
			adaptedTemporaryEnergyBalanceList = new HashMap<Integer, HashMap<Integer, Double>>();
		}
		
		return adaptedTemporaryEnergyBalanceList;
	}
	
	public void addAdaptedTemporaryEnergyBalanceList (int temporaryPeriod, HashMap<Integer, Double> adaptedTemporaryEnergyBalanceList) {
		this.adaptedTemporaryEnergyBalanceList = getAdaptedTemporaryEnergyBalanceList();
		this.adaptedTemporaryEnergyBalanceList.put(temporaryPeriod, adaptedTemporaryEnergyBalanceList);
	}

	public void setAdaptedTemporaryEnergyBalanceList(HashMap<Integer, HashMap<Integer, Double>> adaptedTemporaryEnergyBalanceList) {
		this.adaptedTemporaryEnergyBalanceList = adaptedTemporaryEnergyBalanceList;
	}

	public double getPreviousDiff2ZeroX() {
		return previousDiff2ZeroX;
	}

	public void setPreviousDiff2ZeroX(double previousDiff2ZeroX) {
		this.previousDiff2ZeroX = previousDiff2ZeroX;
	}
	

	public double getPreviousDiff2ZeroZ() {
		return previousDiff2ZeroZ;
	}

	public void setPreviousDiff2ZeroZ(double previousDiff2ZeroZ) {
		this.previousDiff2ZeroZ = previousDiff2ZeroZ;
	}

	public HashMap<Integer, Boolean> getPeriodCalculationDone() {
		if (periodCalculationDone==null) {
			this.periodCalculationDone = new HashMap<Integer, Boolean>();
		}
		
		return periodCalculationDone;
	}
	
	public void addPeriodCalculationDone(int period, boolean done){
		periodCalculationDone = this.getPeriodCalculationDone();
		if (periodCalculationDone.containsKey(period)) {
			periodCalculationDone.replace(period, done);
		} else {
			periodCalculationDone.put(period, done);
		}
	}

	public void setPeriodCalculationDone(HashMap<Integer, Boolean> periodCalculationDone) {
		this.periodCalculationDone = periodCalculationDone;
	}

	public boolean isEnergyResultAlreadySent() {
		return EnergyResultAlreadySent;
	}

	public void setEnergyResultAlreadySent(boolean energyResultAlreadySent) {
		EnergyResultAlreadySent = energyResultAlreadySent;
	}

	public boolean isCMACalculationFinished() {
		return CMACalculationFinished;
	}

	public void setCMACalculationFinished(boolean cMACalculationFinished) {
		CMACalculationFinished = cMACalculationFinished;
	}

	public int getEDATradingPeriod() {
		return EDATradingPeriod;
	}

	public void setEDATradingPeriod(int eDATradingPeriod) {
		EDATradingPeriod = eDATradingPeriod;
	}

	public boolean isRecalculateAlreadyDone() {
		return recalculateAlreadyDone;
	}

	public void setRecalculateAlreadyDone(boolean recalculateAlreadyDone) {
		this.recalculateAlreadyDone = recalculateAlreadyDone;
	}

	public HashMap<Integer, HashMap<Integer, Double>> getCurrentValuesAfterMatching() {
		if (currentValuesAfterMatching == null) {
			currentValuesAfterMatching = new HashMap<Integer, HashMap<Integer, Double>>();
		}
		
		return currentValuesAfterMatching;
	}
	
	public void addCurrentValueAfterMatching(HashMap<Integer, Double> currentValue){
		currentValuesAfterMatching = getCurrentValuesAfterMatching();
		currentValuesAfterMatching.put(tradingPeriod, currentValue);
	}

	public void setCurrentValuesAfterMatching(HashMap<Integer, HashMap<Integer, Double>> currentValuesAfterMatching) {
		this.currentValuesAfterMatching = currentValuesAfterMatching;
	}



	public HashMap<Integer, HashMap<Integer, Double>> getCurrentValuesMap() {
		if (this.currentValuesMap == null) {
			this.currentValuesMap = new HashMap<Integer, HashMap<Integer, Double>>();
		}
		return currentValuesMap;
	}

	public void setCurrentValuesMap(HashMap<Integer, HashMap<Integer, Double>> currentValuesMap) {
		this.currentValuesMap = currentValuesMap;
	}

	public boolean isADMMCalculating() {
		return isADMMCalculating;
	}

	public void setADMMCalculating(boolean isADMMCalculating) {
		this.isADMMCalculating = isADMMCalculating;
	}

	public int getMaxNodes() {
		return maxNodes;
	}

	public void setMaxNodes(int maxNodes) {
		this.maxNodes = maxNodes;
	}

	public HashMap<Integer, Double> getTemporaryEnergyBalanceList() {
		if(temporaryEnergyBalanceList==null) {
			temporaryEnergyBalanceList = new HashMap<Integer, Double>();
		}
		
		return temporaryEnergyBalanceList;
	}

	public void setTemporaryEnergyBalanceList(HashMap<Integer, Double> temporaryEnergyBalanceList) {
		this.temporaryEnergyBalanceList = temporaryEnergyBalanceList;
	}

	public int getTradingPeriod() {
		return tradingPeriod;
	}

	public void setTradingPeriod(int tradingPeriod) {
		this.tradingPeriod = tradingPeriod;
	}

	public HashMap<Integer, HashMap<Integer, Double>> getPlannedEnergyBalanceList() {
		if (plannedEnergyBalanceList==null) {
			plannedEnergyBalanceList = new HashMap<Integer, HashMap<Integer,Double>>();
		}
		
		return plannedEnergyBalanceList;
	}

	public void setPlannedEnergyBalanceList(HashMap<Integer, HashMap<Integer, Double>> plannedEnergyBalance) {
		this.plannedEnergyBalanceList = plannedEnergyBalance;
	}
	
	public void addPlannedEnergyBalance2List(Integer edaAID, double energyConsumption, int receivedTradingPeriod) {
//		plannedEnergyBalanceList = this.getPlannedEnergyBalanceList();
//		temporaryEnergyBalanceList = this.getTemporaryEnergyBalanceList();
//		temporaryEnergyBalanceList.put(edaAID, energyConsumption);
//		plannedEnergyBalanceList.put(receivedTradingPeriod, temporaryEnergyBalanceList);
		HashMap<Integer, Double> temporaryHelpEnergyBalanceList;
		if (plannedEnergyBalanceList.containsKey(receivedTradingPeriod)) {
			temporaryHelpEnergyBalanceList = plannedEnergyBalanceList.get(receivedTradingPeriod);
			temporaryHelpEnergyBalanceList.put(edaAID, energyConsumption);
			plannedEnergyBalanceList.replace(receivedTradingPeriod, temporaryHelpEnergyBalanceList);
		} else {
			temporaryHelpEnergyBalanceList = new HashMap<Integer, Double>();
			temporaryHelpEnergyBalanceList.put(edaAID, energyConsumption);
			plannedEnergyBalanceList.put(receivedTradingPeriod, temporaryHelpEnergyBalanceList);
		}
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	public double getPreviousLambda() {
		return previousLambda;
	}

	public void setPreviousLambda(double previousLambda) {
		this.previousLambda = previousLambda;
	}

	public double getMinX() {
		return minX;
	}

	public void setMinX(double minX) {
		this.minX = minX;
	}

	public double getMinZ() {
		return minZ;
	}

	public void setMinZ(double minZ) {
		this.minZ = minZ;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getPreviousX() {
		return previousX;
	}

	public void setPreviousX(double previousX) {
		this.previousX = previousX;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public double getPreviousZ() {
		return previousZ;
	}

	public void setPreviousZ(double previousZ) {
		this.previousZ = previousZ;
	}

	public List<AID> getPhoneBook() {
		if (phoneBook ==null) {
			phoneBook = new ArrayList<AID>();
		}
		return phoneBook;
	}

	public void setPhoneBook(List<AID> phoneBook) {
		this.phoneBook = phoneBook;
	}
	
	public void addAID2PhoneBook(AID aid) {
		if (phoneBook ==null) {
			phoneBook = new ArrayList<AID>();
		}
		this.phoneBook.add(aid);
	}

	public int getCountReceivedMessages() {
		return countReceivedMessages;
	}

	public void setCountReceivedMessages(int countReceivedMessages) {
		this.countReceivedMessages = countReceivedMessages;
	}
	
	public void increaseCountReceivedMessages() {
		this.countReceivedMessages+=1;
	}

	public int getIteration() {
		return iteration;
	}

	public void setIteration(int iteration) {
		this.iteration = iteration;
	}

	public HashMap<Integer, HashMap<AID, FlexibilityTransaction>> getTotalListFlexibilityTransaction() {
	    if (totalListFlexibilityTransaction == null) {
	        totalListFlexibilityTransaction = new HashMap<>();
	    }
	    return totalListFlexibilityTransaction;
	}

	public void increaseIteration() {
	    this.iteration += 1;
	}

	public void setTotalListFlexibilityTransaction(
	        HashMap<Integer, HashMap<AID, FlexibilityTransaction>> totalListFlexibilityTransaction) {
	    this.totalListFlexibilityTransaction = totalListFlexibilityTransaction;
	}

//	public void addTotalListFlexibilityTransaction(HashMap<AID, FlexibilityTransaction> flexList) {
//	    if (totalListFlexibilityTransaction == null) {
//	        totalListFlexibilityTransaction = new HashMap<>();
//	    }
//
//	    HashMap<AID, FlexibilityTransaction> copyOfFlexList = new HashMap<>(flexList);
//	    if (totalListFlexibilityTransaction.containsKey(iteration)) {
//	        totalListFlexibilityTransaction.replace(iteration, copyOfFlexList);
//	    } else {
//	        this.getTotalListFlexibilityTransaction().put(iteration, copyOfFlexList);
//	    }
//	}
//
//	private void addTotalListFlexibilityTransaction(HashMap<AID, FlexibilityTransaction> temporaryListFlexibilityTransaction2, int iteration2) {
//	    if (totalListFlexibilityTransaction == null) {
//	        totalListFlexibilityTransaction = new HashMap<>();
//	    }
//
//	    HashMap<AID, FlexibilityTransaction> copyOfFlexList = new HashMap<>(temporaryListFlexibilityTransaction2);
//	    if (totalListFlexibilityTransaction.containsKey(iteration2)) {
//	        totalListFlexibilityTransaction.replace(iteration2, copyOfFlexList);
//	    } else {
//	        this.getTotalListFlexibilityTransaction().put(iteration2, copyOfFlexList);
//	    }
//	}
//
//	public HashMap<Integer, HashMap<Integer, HashMap<AID, FlexibilityTransaction>>> getOverallFlexibilityTransaction() {
//	    if (overallFlexibilityTransaction == null) {
//	        overallFlexibilityTransaction = new HashMap<>();
//	    }
//	    return overallFlexibilityTransaction;
//	}
//
//	public void setOverallFlexibilityTransaction(HashMap<Integer, HashMap<Integer, HashMap<AID, FlexibilityTransaction>>> overallFlexibilityTransaction) {
//	    this.overallFlexibilityTransaction = overallFlexibilityTransaction;
//	}
//
//	public void addOverallFlexibilityTransaction(HashMap<Integer, HashMap<AID, FlexibilityTransaction>> hm) {
//	    if (overallFlexibilityTransaction == null) {
//	        overallFlexibilityTransaction = new HashMap<>();
//	    }
//	    overallFlexibilityTransaction.put(tradingPeriod, hm);
//	}
//
//	public HashMap<AID, FlexibilityTransaction> getTemporaryListFlexibilityTransaction() {
//	    if (this.temporaryListFlexibilityTransaction == null) {
//	        this.temporaryListFlexibilityTransaction = new HashMap<>();
//	    }
//	    return temporaryListFlexibilityTransaction;
//	}
//
//	public void addTemporaryListFlexibilityTransaction(AID aid, FlexibilityTransaction ft) {
//	    if (this.temporaryListFlexibilityTransaction == null) {
//	        this.temporaryListFlexibilityTransaction = new HashMap<>();
//	    }
//	    this.temporaryListFlexibilityTransaction.put(aid, ft);
//	    HashMap<AID, FlexibilityTransaction> copyOfTempList = new HashMap<>(this.temporaryListFlexibilityTransaction);
//	    addTotalListFlexibilityTransaction(copyOfTempList);
//	    addOverallFlexibilityTransaction(new HashMap<>(totalListFlexibilityTransaction));
//	}
//
//	public void addTemporaryListFlexibilityTransaction(AID aid, FlexibilityTransaction ft, int iteration) {
//	    if (this.temporaryListFlexibilityTransaction == null) {
//	        this.temporaryListFlexibilityTransaction = new HashMap<>();
//	    }
//	    this.temporaryListFlexibilityTransaction.put(aid, ft);
//	    HashMap<AID, FlexibilityTransaction> copyOfTempList = new HashMap<>(this.temporaryListFlexibilityTransaction);
//	    addTotalListFlexibilityTransaction(copyOfTempList, iteration);
//	    addOverallFlexibilityTransaction(new HashMap<>(totalListFlexibilityTransaction));
//	}

	public void addTotalListFlexibilityTransaction(HashMap<AID, FlexibilityTransaction> flexList) {
	    if (totalListFlexibilityTransaction == null) {
	        totalListFlexibilityTransaction = new HashMap<>();
	    }

	    // Leere die HashMap, bevor neue Daten hinzugefügt werden
	    totalListFlexibilityTransaction.clear();

	    HashMap<AID, FlexibilityTransaction> copyOfFlexList = new HashMap<>(flexList);
	    totalListFlexibilityTransaction.put(iteration, copyOfFlexList);
	}

	private void addTotalListFlexibilityTransaction(HashMap<AID, FlexibilityTransaction> temporaryListFlexibilityTransaction2, int iteration2) {
	    if (totalListFlexibilityTransaction == null) {
	        totalListFlexibilityTransaction = new HashMap<>();
	    }

	    // Leere die HashMap, bevor neue Daten hinzugefügt werden
	    totalListFlexibilityTransaction.clear();

	    HashMap<AID, FlexibilityTransaction> copyOfFlexList = new HashMap<>(temporaryListFlexibilityTransaction2);
	    totalListFlexibilityTransaction.put(iteration2, copyOfFlexList);
	}


	public HashMap<Integer, HashMap<Integer, HashMap<AID, FlexibilityTransaction>>> getOverallFlexibilityTransaction() {
	    if (overallFlexibilityTransaction == null) {
	        overallFlexibilityTransaction = new HashMap<>();
	    }
	    return overallFlexibilityTransaction;
	}

	public void setOverallFlexibilityTransaction(HashMap<Integer, HashMap<Integer, HashMap<AID, FlexibilityTransaction>>> overallFlexibilityTransaction) {
	    this.overallFlexibilityTransaction = overallFlexibilityTransaction;
	}

	public void addOverallFlexibilityTransaction(int tradingPeriod, int iteration, AID aid, FlexibilityTransaction ft) {
	    // Sicherstellen, dass die Overall-Map existiert
	    if (overallFlexibilityTransaction == null) {
	        overallFlexibilityTransaction = new HashMap<>();
	    }

	    // Sicherstellen, dass die Periode existiert
	    if (!overallFlexibilityTransaction.containsKey(tradingPeriod)) {
	        overallFlexibilityTransaction.put(tradingPeriod, new HashMap<>());
	    }

	    // Sicherstellen, dass die Iteration existiert
	    HashMap<Integer, HashMap<AID, FlexibilityTransaction>> periodMap = overallFlexibilityTransaction.get(tradingPeriod);
	    if (!periodMap.containsKey(iteration)) {
	        periodMap.put(iteration, new HashMap<>());
	    }

	    // Hinzufügen des neuen Werts zur spezifischen Iteration in der spezifischen Periode
	    periodMap.get(iteration).put(aid, ft);
	}


	public HashMap<AID, FlexibilityTransaction> getTemporaryListFlexibilityTransaction() {
	    if (this.temporaryListFlexibilityTransaction == null) {
	        this.temporaryListFlexibilityTransaction = new HashMap<>();
	    }
	    return temporaryListFlexibilityTransaction;
	}

	public void addTemporaryListFlexibilityTransaction(AID aid, FlexibilityTransaction ft) {
		
		
	    if (this.temporaryListFlexibilityTransaction == null) {
	        this.temporaryListFlexibilityTransaction = new HashMap<>();
	    }
	    
	    // Hinzufügen des neuen Werts zur temporären Liste
	    this.temporaryListFlexibilityTransaction.put(aid, ft);

	    // Speichern in der Gesamtstruktur für die spezifische Periode und Iteration
	    addOverallFlexibilityTransaction(tradingPeriod, iteration, aid, ft);
	}

	
	public void setTemporaryListFlexibilityTransaction(HashMap<AID, FlexibilityTransaction> listFlexibilityTransaction) {
		this.temporaryListFlexibilityTransaction = listFlexibilityTransaction;
	}

	public CongestionManagingAgent getCongestionManagingAgent() {
		return congestionManagingAgent;
	}

	public boolean isLeader() {
		return isLeader;
	}

	public void setLeader(boolean isLeader) {
		this.isLeader = isLeader;
	}

	public void setCongestionManagingAgent(CongestionManagingAgent congestionManagingAgent) {
		this.congestionManagingAgent = congestionManagingAgent;
	}



	public FlexibilityTransaction getFlexibilityTransaction() {
		return flexibilityTransaction;
	}



	public void setFlexibilityTransaction(FlexibilityTransaction flexibilityTransaction) {
		this.flexibilityTransaction = flexibilityTransaction;
	}

	public InternalDataModel(CongestionManagingAgent congestionManagingAgent) {
		this.congestionManagingAgent = congestionManagingAgent;
	}

	
}
