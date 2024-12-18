package net.peak.agent.energyTradingAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import agentgui.core.common.AbstractUserObject;
import de.enflexit.ea.core.dataModel.deployment.AgentOperatingMode;
import de.enflexit.jade.phonebook.PhoneBook;
import jade.core.AID;
import net.peak.agent.ConnetionCRIEPILab.MeasurementDataModel;
import net.peak.datamodel.communication.EnergyResult;
import net.peak.datamodel.communication.EnergyTransaction;
import net.peak.datamodel.configuration.PeakConfiguration;
import net.peak.datamodel.configuration.PeakConfiguration.SimulationTypeEnum;


// TODO: Auto-generated Javadoc
public class InternalDataModel extends AbstractUserObject {
	
	private static final long serialVersionUID = 5290543889811221330L;

	// Reference to the main EnergyTradingAgent
	private EnergyTradingAgent energyTradingAgent;

	// Market-related constants
	public boolean isFive = true;
	public String Offer = "Offer";
	public String Ask = "Ask";

	// Agent and platform-related fields
	private AID platformAgentAID;
	private AID energyAMDAID = new AID("energyAMS@192.168.0.51:1099/JADE", AID.ISGUID);
	private List<AID> offerAgentList;
	private List<AID> openGridNodes;

	// Energy transactions and offers
	private HashMap<String, EnergyTransaction> listEnergyOffers;
	private HashMap<String, EnergyTransaction> listOpenEnergyOffers;
	private EnergyTransaction ownEnergyOffer;
	private List<EnergyTransaction> listOwnEnergyNeed;
	private HashMap<String, EnergyResult> resultList;
	private HashMap<String, EnergyResult> resultListExtended;
	private HashMap<Integer, EnergyResult> resultListExtendedTemporary;
	private HashMap<Integer, Double> SOCList;
	private HashMap<Integer, HashMap<Integer, MeasurementDataModel>> periodMeasurements;
	private HashMap<Integer, MeasurementDataModel> periodIntervallMeasurements;
	private HashMap<Integer, EnergyResult> energyList2Adapt;
	private HashMap<Integer, Double> energyTradingTime;
	private HashMap<Integer, HashMap<Integer, double[]>> computationalLoadMap;

	// Market parameters and pricing
	private boolean gateClosureEnergyMarket = false;
	private boolean gateClousureFlexMarket = false;
	private OfferAnswerType offerAnswerType;
	private double maxDifferenceOfferd2OwnPrice = 0.5;
	protected double gridEnergyPrice = 0.36;
	protected double gridEnergyPriceJapan = 0.22;
	protected double feedInTariff = 0.08;
	protected double feedInTariffJapan = 0.06;
	public PriceTypeEnum priceTypeEnum;
	private double highPriceOffset = 0.16;
	private double highPriceOffsetJapan = 0.12;
	private double lowPriceOffset = highPriceOffset / 4;
	private String externalEnergySupplierName = "99";

	// Trading control and state management
	private boolean TopoOptimizationActive = false;
	private boolean TradingToken = false;
	private int tradingCyclesCnt;
	private boolean TradingAllowed = false;
	private int NumberAgentOfferedEnergy;
	private int periodNumber;
	private int startWaitingIteration;
	private boolean Result2CMAAlreadySent = false;
	private boolean congestionManagementActive = false;
	private boolean debugMode = false;
	private int amountMessagesSended = 0;
	private int criepiMeasurementCnt = 0;
	private int maxCriepiMeasurementCnt = 2;
	private double openEnergyAmount;

	// Energy and resource management
	private Storage storage;
	private SimulationTypeEnum simulationTypeEnum;
	private float initialEnergyAmountOfIntervall;
	private double consumptionMultiplicator = 1;
	private float maxConsumption = 5000;
	private double openEnergyAmount2Adapt;
	private double adaptedEnergy = 0;
	private int maxPeriodNumber;
	private int gridNodesCnt;
	private int gridNodeMax;

	// Time management
	private long startTimeEnergyTrading;
	private long endTimeEnergyTrading;
	private int timeResolution;

	// File paths for data storage
	private String metaFolderPathResults;
	private String metaFolderPathInputData;

	// Boolean flags for specific resources
	private boolean EVBoolean;
	private boolean HeatPumpBoolean;
	private boolean PVBoolean;

	// Energy planning maps
	HashMap<Integer, Double> plannedEnergyAmountMapOriginal;
	HashMap<Integer, Double> measuredEnergyAmountMap;
	HashMap<Integer, Double> plannedEnergyAmountMapAdaptedMeasurement;

	// Configuration object
	PeakConfiguration peakConfiguration = new PeakConfiguration();

		
	
	public HashMap<Integer, HashMap<Integer, double[]>> getComputationalLoadMap() {
		
		if (computationalLoadMap==null) {
			computationalLoadMap = new HashMap<Integer, HashMap<Integer, double[]>>();
		}
		return computationalLoadMap;
	}

	public void setComputationalLoadMap(HashMap<Integer, HashMap<Integer, double[]>> computationalLoadMap) {
		this.computationalLoadMap = computationalLoadMap;
	}
	
	

	public double getOpenEnergyAmount() {
		return openEnergyAmount;
	}

	public void setOpenEnergyAmount(double openEnergyAmount) {
		this.openEnergyAmount = openEnergyAmount;
	}

	public double getGridEnergyPriceJapan() {
		return gridEnergyPriceJapan;
	}

	public void setGridEnergyPriceJapan(double gridEnergyPriceJapan) {
		this.gridEnergyPriceJapan = gridEnergyPriceJapan;
	}

	public double getFeedInTariffJapan() {
		return feedInTariffJapan;
	}

	public void setFeedInTariffJapan(double feedInTariffJapan) {
		this.feedInTariffJapan = feedInTariffJapan;
	}

	public boolean isPVBoolean() {
		return PVBoolean;
	}

	public void setPVBoolean(boolean pVBoolean) {
		PVBoolean = pVBoolean;
	}

	public HashMap<Integer, EnergyResult> getResultListExtendedTemporary() {
		return resultListExtendedTemporary;
	}

	public void setResultListExtendedTemporary(HashMap<Integer, EnergyResult> resultListExtendedTemporary) {
		this.resultListExtendedTemporary = resultListExtendedTemporary;
	}

	public HashMap<String, EnergyResult> getResultListExtended() {
		if(resultListExtended == null) {
			resultListExtended = new HashMap<String, EnergyResult>();
		}
		
		return resultListExtended;
	}

	public void setResultListExtended(HashMap<String, EnergyResult> resultListExtended) {
		this.resultListExtended = resultListExtended;
	}

	public AID getEnergyAMDAID() {
		return energyAMDAID;
	}

	public void setEnergyAMDAID(AID energyAMDAID) {
		this.energyAMDAID = energyAMDAID;
	}

	public boolean isCongestionManagementActive() {
		return congestionManagementActive;
	}

	public void setCongestionManagementActive(boolean congestionManagementActive) {
		this.congestionManagementActive = congestionManagementActive;
	}

	public int getMaxCriepiMeasurementCnt() {
		return maxCriepiMeasurementCnt;
	}

	public void setMaxCriepiMeasurementCnt(int maxCriepiMeasurementCnt) {
		this.maxCriepiMeasurementCnt = maxCriepiMeasurementCnt;
	}

	public void increaseCriepiMeasurementCnt() {
		criepiMeasurementCnt += 1;
	}

	public int getCriepiMeasurementCnt() {
		return criepiMeasurementCnt;
	}

	public void setCriepiMeasurementCnt(int criepiMeasurementCnt) {
		this.criepiMeasurementCnt = criepiMeasurementCnt;
	}


public HashMap<Integer, HashMap<Integer, MeasurementDataModel>> getPeriodMeasurements() {
    if (periodMeasurements == null) {
        periodMeasurements = new HashMap<Integer, HashMap<Integer, MeasurementDataModel>>();
    }
    return periodMeasurements;
}

public HashMap<Integer, MeasurementDataModel> getPeriodIntervallMeasurements() {
	if (this.periodIntervallMeasurements==null) {
		this.periodIntervallMeasurements = new HashMap<Integer, MeasurementDataModel>();
	}
	return periodIntervallMeasurements;
}

public void setPeriodIntervallMeasurements(HashMap<Integer, MeasurementDataModel> periodIntervallMeasurements) {
	this.periodIntervallMeasurements = periodIntervallMeasurements;
}

public void addPeriodMeasurementInIntervall(MeasurementDataModel intervalMeasurement) {
    this.periodMeasurements = this.getPeriodMeasurements();

    // Überprüfen Sie, ob die Periode bereits existiert
    if (!periodMeasurements.containsKey(periodNumber)) {
        // Initialisieren Sie periodIntervallMeasurements für die neue Periode
        periodIntervallMeasurements = new HashMap<Integer, MeasurementDataModel>();
    } else {
        // Holen Sie sich die bestehende periodIntervallMeasurements für die vorhandene Periode
        periodIntervallMeasurements = periodMeasurements.get(periodNumber);
    }

    periodIntervallMeasurements.put(tradingCyclesCnt, intervalMeasurement);
    periodMeasurements.put(periodNumber, periodIntervallMeasurements);
}

	public void setPeriodMeasurements(HashMap<Integer, HashMap<Integer, MeasurementDataModel>> periodMeasurements) {
		this.periodMeasurements = periodMeasurements;
	}

	public HashMap<Integer, Double> getPlannedEnergyAmountMapOriginal() {
		if (plannedEnergyAmountMapOriginal==null) {
			plannedEnergyAmountMapOriginal = new HashMap<Integer, Double>();
		}
		return plannedEnergyAmountMapOriginal;
	}

	public void setPlannedEnergyAmountMapOriginal(HashMap<Integer, Double> plannedEnergyAmountMapOriginal) {
		this.plannedEnergyAmountMapOriginal = plannedEnergyAmountMapOriginal;
	}

	public HashMap<Integer, Double> getMeasuredEnergyAmountMap() {
		if(measuredEnergyAmountMap==null) {
			measuredEnergyAmountMap = new HashMap<Integer, Double>();
		}
		return measuredEnergyAmountMap;
	}

	public void setMeasuredEnergyAmountMap(HashMap<Integer, Double> measuredEnergyAmountMap) {
		this.measuredEnergyAmountMap = measuredEnergyAmountMap;
	}

	public HashMap<Integer, Double> getPlannedEnergyAmountMapAdaptedMeasurement() {
		if (plannedEnergyAmountMapAdaptedMeasurement==null) {
			plannedEnergyAmountMapAdaptedMeasurement = new HashMap<Integer, Double>();
		}
		return plannedEnergyAmountMapAdaptedMeasurement;
	}

	public void setPlannedEnergyAmountMapAdaptedMeasurement(
			HashMap<Integer, Double> plannedEnergyAmountMapAdaptedMeasurement) {
		this.plannedEnergyAmountMapAdaptedMeasurement = plannedEnergyAmountMapAdaptedMeasurement;
	}

	public int getStartWaitingIteration() {
		return startWaitingIteration;
	}

	public void setStartWaitingIteration(int startWaitingIteration) {
		this.startWaitingIteration = startWaitingIteration;
	}

	public void setMaxPeriodNumber(int maxPeriodNumber) {
		this.maxPeriodNumber = maxPeriodNumber;
	}

	public int getTimeResolution() {
		return timeResolution;
	}

	public void setTimeResolution(int timeResolution) {
		this.timeResolution = timeResolution;
	}

	public String getMetaFolderPathResults() {
		return metaFolderPathResults;
	}

	public void setMetaFolderPathResults(String metaFolderPathResults) {
		this.metaFolderPathResults = metaFolderPathResults;
	}

	public String getMetaFolderPathInputData() {
		return metaFolderPathInputData;
	}

	public void setMetaFolderPathInputData(String metaFolderPathInputData) {
		this.metaFolderPathInputData = metaFolderPathInputData;
	}

	

	public SimulationTypeEnum getSimulationTypeEnum() {
		return simulationTypeEnum;
	}

	public void setSimulationTypeEnum(SimulationTypeEnum simulationTypeEnum) {
		this.simulationTypeEnum = simulationTypeEnum;
	}

	public HashMap<Integer, Double> getEnergyTradingTime() {
		if (energyTradingTime==null){
			energyTradingTime = new HashMap<Integer, Double>();
		}
		return energyTradingTime;
	}
	
	public void addEnergyTradingTime(double tradingTime) {
		this.energyTradingTime = getEnergyTradingTime();
		this.energyTradingTime.put(this.periodNumber, tradingTime);
	}



	public void setEnergyTradingTime(HashMap<Integer, Double> energyTradingTime) {
		this.energyTradingTime = energyTradingTime;
	}



	public long getStartTimeEnergyTrading() {
		return startTimeEnergyTrading;
	}



	public void setStartTimeEnergyTrading(long startTimeEnergyTrading) {
		this.startTimeEnergyTrading = startTimeEnergyTrading;
	}



	public long getEndTimeEnergyTrading() {
		return endTimeEnergyTrading;
	}



	public void setEndTimeEnergyTrading(long endTimeEnergyTrading) {
		this.endTimeEnergyTrading = endTimeEnergyTrading;
	}



	public boolean getEVBoolean() {
		return EVBoolean;
	}



	public void setEVBoolean(boolean isEVAgent) {
		EVBoolean = isEVAgent;
	}



	public boolean getHeatPumpBoolean() {
		return HeatPumpBoolean;
	}



	public void setHeatPumpBoolean(boolean isHeatPumpAgent) {
		HeatPumpBoolean = isHeatPumpAgent;
	}

	public double getAdaptedEnergy() {
		return adaptedEnergy;
	}

	public void setAdaptedEnergy(double adaptedEnergy) {
		this.adaptedEnergy = adaptedEnergy;
	}
	
	public void increaseAdaptedEnergy (double energyAmount) {
		this.adaptedEnergy+=energyAmount;
	}

	public boolean isResult2CMAAlreadySent() {
		return Result2CMAAlreadySent;
	}

	public void setResult2CMAAlreadySent(boolean result2cmaAlreadySent) {
		Result2CMAAlreadySent = result2cmaAlreadySent;
	}

	public double getOpenEnergyAmount2Adapt() {
		return openEnergyAmount2Adapt;
	}

	public void setOpenEnergyAmount2Adapt(double openEnergyAmount2Adapt) {
		this.openEnergyAmount2Adapt = openEnergyAmount2Adapt;
	}

	public HashMap<Integer, EnergyResult> getEnergyList2Adapt() {
		if (this.energyList2Adapt == null) {
			this.energyList2Adapt = new HashMap<Integer, EnergyResult>();
		}
		
		return energyList2Adapt;
	}

	public void setEnergyList2Adapt(HashMap<Integer, EnergyResult> energyList2Adapt) {
		this.energyList2Adapt = energyList2Adapt;
	}

	public float getMaxConsumption() {
		return maxConsumption;
	}

	public void setMaxConsumption(float maxConsumption) {
		this.maxConsumption = maxConsumption;
	}

	public double getConsumptionMultiplicator() {
		return consumptionMultiplicator;
	}

	public void setConsumptionMultiplicator(double d) {
		this.consumptionMultiplicator = d;
	}

	public HashMap<Integer, Double> getSOCList() {
		if (SOCList == null) {
			SOCList = new HashMap<Integer, Double>();
		}
		return SOCList;
	}

	public void setSOCList(HashMap<Integer, Double> sOCList) {
		SOCList = sOCList;
	}
	
	public void addSOC2SOCList (Storage storage) {
		if (SOCList == null) {
			SOCList = new HashMap<Integer, Double>();
		}
		SOCList.put(this.periodNumber, storage.getStorageSOC());
	}

	public float getInitialEnergyAmountOfIntervall() {
		return initialEnergyAmountOfIntervall;
	}

	public void setInitialEnergyAmountOfIntervall(float initialEnergyAmountOfIntervall) {
		this.initialEnergyAmountOfIntervall = initialEnergyAmountOfIntervall;
	}

	public double getLowPriceOffset() {
		return lowPriceOffset;
	}

	public void setLowPriceOffset(double lowPriceOffset) {
		this.lowPriceOffset = lowPriceOffset;
	}

	public HashMap<String, EnergyTransaction> getListOpenEnergyOffers() {
		if (this.listOpenEnergyOffers==null) this.listOpenEnergyOffers = new HashMap<String, EnergyTransaction>();
		return listOpenEnergyOffers;
	}

	public void setListOpenEnergyOffers(HashMap<String, EnergyTransaction> listOpenEnergyOffers) {
		this.listOpenEnergyOffers = listOpenEnergyOffers;
	}

	public int getGridNodesCnt() {
		return gridNodesCnt;
	}

	public void setGridNodesCnt(int gridNodesCnt) {
		this.gridNodesCnt = gridNodesCnt;
	}
	
	public void increaseGridNodeCnt() {
		this.gridNodesCnt += 1;
	}

	public int getGridNodeMax() {
		return gridNodeMax;
	}

	public void setGridNodeMax(int gridNodeMax) {
		this.gridNodeMax = gridNodeMax;
	}

	public List<AID> getOpenGridNodes() {
		return openGridNodes;
	}


	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public double getHighPriceOffset() {
		if (peakConfiguration.simulationTypeEnum.equals(SimulationTypeEnum.SIMULATIONCRIEPI)) {
			return highPriceOffsetJapan;
		} else return highPriceOffset;
	}

	public void setHighPriceOffset(double highPriceOffset) {
		this.highPriceOffset = highPriceOffset;
	}

	public int getMaxPeriodNumber() {
		return maxPeriodNumber;
	}
		
	public HashMap<String, EnergyResult> getResultList() {
		if (this.resultList==null) this.resultList = new HashMap<String, EnergyResult>();
		return resultList;
	}

	public void setResultList(HashMap<String, EnergyResult> resultList) {
		this.resultList = resultList;
	}

	public void addResult2List (EnergyResult e, boolean isResult) {
		
		if(isResult == true) {
			if (resultList==null) {
				resultList = new HashMap<String, EnergyResult>();
			} 
			e.setTradingFinished(true);
			if(resultList.containsKey(e.getLocalTransactionID())) {
				System.out.println(this.energyTradingAgent.getLocalName()+": double ResultEntry");
				resultList.put(e.getLocalTransactionID(), e);
			} else 	resultList.put(e.getLocalTransactionID(), e);
		}
		
		resultListExtended = this.getResultListExtended();
		String extendedTransactionID = e.getLocalTransactionID()+this.periodNumber+this.tradingCyclesCnt;
		resultListExtended.put(extendedTransactionID, e);
		
	}
	
	public List<AID> getOfferAgentList() {
		if (offerAgentList == null) {
			offerAgentList = new ArrayList<AID>();
		}
		return offerAgentList;
	}

	public void setOfferAgentList(List<AID> offerAgentList) {
		this.offerAgentList = offerAgentList;
	}
	
	public void addAgent2OfferList(AID agentAID) {
		if (offerAgentList == null) {
			offerAgentList = new ArrayList<AID>();
		}
		offerAgentList.add(agentAID);
	}

	
	public String getExternalEnergySupplierName() {
		return externalEnergySupplierName;
	}
	
	public AID getExternalEnergySupplierAID() {
		AID aid = new AID(externalEnergySupplierName, true);
		return aid;
	}

	public void setExternalEnergySupplierName(String externalEnergySupplierName) {
		this.externalEnergySupplierName = externalEnergySupplierName;
	}

	public void increasePeriodNumber() {
		this.setPeriodNumber(this.getPeriodNumber()+1);
	}

	public int getPeriodNumber() {
		return periodNumber;
	}

	public void setPeriodNumber(int periodNumber) {
		this.periodNumber = periodNumber;
	}

	public int getNumberAgentOfferedEnergy() {
		return NumberAgentOfferedEnergy;
	}

	public void setNumberAgentOfferedEnergy(int numberAgentOfferedEnergy) {
		NumberAgentOfferedEnergy = numberAgentOfferedEnergy;
	}

	public Storage getStorage() {
		if (storage == null) {
			storage = new Storage();
		}		
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}

	public boolean isTradingAllowed() {
		return TradingAllowed;
	}

	public void setTradingAllowed(boolean tradingAllowed) {
		TradingAllowed = tradingAllowed;
	}

	public boolean isTradingToken() {
		return TradingToken;
	}

	public void setTradingToken(boolean tradingToken) {
		TradingToken = tradingToken;
	}

	public int getTradingCyclesCnt() {
		return tradingCyclesCnt;
	}

	public void setTradingCyclesCnt(int tradingCyclesCnt) {
		this.tradingCyclesCnt = tradingCyclesCnt;
	}
	
	public void increaseTradingCyclesCnt() {
		this.tradingCyclesCnt = this.tradingCyclesCnt+1;
	}

	public boolean isTopoOptimizationActive() {
		return TopoOptimizationActive;
	}

	public void setTopoOptimizationActive(boolean topoOptimizationActive) {
		TopoOptimizationActive = topoOptimizationActive;
	}

	public PriceTypeEnum getPriceTypeEnum() {
		return priceTypeEnum;
	}

	public void setPriceTypeEnum(PriceTypeEnum priceTypeEnum) {
		this.priceTypeEnum = priceTypeEnum;
	}

	public double getGridEnergyPrice() {
		if(this.peakConfiguration.simulationTypeEnum.equals(SimulationTypeEnum.SIMULATIONCRIEPI)) {
			return gridEnergyPriceJapan;
		} else {
			return gridEnergyPrice;
		}
	}

	public void setGridEnergyPrice(double energyPrice) {
		this.gridEnergyPrice = energyPrice;
	}

	public double getFeedInTariff() {
		if(this.peakConfiguration.simulationTypeEnum.equals(SimulationTypeEnum.SIMULATIONCRIEPI)) {
			return feedInTariffJapan;
		} else {
			return feedInTariff;
		}
		
	}

	public void setFeedInTariff(double feedInTariff) {
		this.feedInTariff = feedInTariff;
	}

	public List<EnergyTransaction> getListOwnEnergyNeed() {
		if (this.listOwnEnergyNeed == null) {
			this.listOwnEnergyNeed = new ArrayList<EnergyTransaction>();
		}
		return listOwnEnergyNeed;
	}

	public void setListOwnEnergyNeed(List<EnergyTransaction> listOwnEnergyNeed) {
		this.listOwnEnergyNeed = listOwnEnergyNeed;
	}
	
	public void addEnergyTrade2ListEnergyOffers(EnergyTransaction e) {
		if (this.listEnergyOffers == null) {
			this.listEnergyOffers = new HashMap<>();
		}
		this.listEnergyOffers.put(e.getLocalTransactionID(), e);
	}

	public double getMaxDifferenceOfferd2OwnPrice() {
		return maxDifferenceOfferd2OwnPrice;
	}

	public void setMaxDifferenceOfferd2OwnPrice(int maxDifferenceOfferd2OwnPrice) {
		this.maxDifferenceOfferd2OwnPrice = maxDifferenceOfferd2OwnPrice;
	}

	public HashMap<String, EnergyTransaction> getListEnergyOffers() {
		if (this.listEnergyOffers==null) this.listEnergyOffers = new HashMap<>();
		return listEnergyOffers;
	}
	
	public void clearListEnergyOffers() {
		if (this.listEnergyOffers==null) this.listEnergyOffers = new HashMap<>();
		this.listEnergyOffers.clear();
	}

	public void setListEnergyOffers(HashMap<String, EnergyTransaction> listEnergyOffers) {
		this.listEnergyOffers = listEnergyOffers;
	}

	public EnergyTransaction getOwnEnergyOffer() {
		if (this.ownEnergyOffer == null) {
			this.ownEnergyOffer = new EnergyTransaction();
		}
		return ownEnergyOffer;
	}

	public void setOwnEnergyOffer(EnergyTransaction ownEnergyOffer) {
		this.ownEnergyOffer = ownEnergyOffer;
	}

	public OfferAnswerType getOfferAnswerType() {
		return offerAnswerType;
	}

	public void setOfferAnswerType(OfferAnswerType offerAnswerType) {
		this.offerAnswerType = offerAnswerType;
	}

	public enum OfferAnswerType {
	    ACCEPT("Accept"),
	    
	    REJECT("Reject"),
		
		REOFFER("Reoffer"),
		
		FIRSTOFFER("FirstOffer");

	    OfferAnswerType(String value) {
	    }
	}
	
	public enum SystemTypeEnum {
	    PRODUCTION("Production"),
	    
	    LOAD("Load"),
	    
	    EV_LOAD("EV_Load"),
	    
	    HEAT_PUMP("Heat_Pump"),
		
		BATTERY("Battery");

	    SystemTypeEnum(String value) {
		}
	}
	
	
	public class Storage{
		double storageSOC;
		double storageCapacity;
		double storageEfficiency;
		
		public double getStorageSOC() {
			return storageSOC;
		}

		public void setStorageSOC(double storageSOC) {
			this.storageSOC = storageSOC;
		}
		
		public void chargeStorage(double energyAmount2Charge) {
			if ((1-this.storageSOC)*this.storageCapacity > energyAmount2Charge) {
				this.storageSOC += energyAmount2Charge/storageCapacity;
			}
		}
		
		public void dischargeStorage(double energyAmount2Discharge) {
			if ((this.storageSOC)*this.storageCapacity > energyAmount2Discharge) {
				this.storageSOC -= energyAmount2Discharge/storageCapacity;
			}
		}

		public double getStorageCapacity() {
			return storageCapacity;
		}

		public void setStorageCapacity(double storageCapacity) {
			this.storageCapacity = storageCapacity;
		}

		public double getStorageEfficiency() {
			return storageEfficiency;
		}

		public void setStorageEfficiency(double storageEfficiency) {
			this.storageEfficiency = storageEfficiency;
		}

	}
	

	public boolean isGateClosureEnergyMarket() {
		return gateClosureEnergyMarket;
	}

	public void setGateClosureEnergyMarket(boolean gateClosureEnergyMarket) {
		this.gateClosureEnergyMarket = gateClosureEnergyMarket;
	}

	public boolean isGateClousureFlexMarket() {
		return gateClousureFlexMarket;
	}

	public void setGateClousureFlexMarket(boolean gateClousureFlexMarket) {
		this.gateClousureFlexMarket = gateClousureFlexMarket;
	}

	public InternalDataModel(EnergyTradingAgent energyTradingAgent) {
		this.energyTradingAgent = energyTradingAgent;
	}
	
	public enum PriceTypeEnum {
	    HIGH("High"),
	    
	    NORMAL("Normal"),
		
		LOW("Low");

	    PriceTypeEnum(String value) {
		}
	}

	/**
	 * Sets the order book.
	 *
	 * @param orderBook the new order book
	 */

	/**
	 * Gets the platform agent AID.
	 *
	 * @return the platform agent AID
	 */
	public AID getPlatformAgentAID() {
		return platformAgentAID;
	}
	
	/**
	 * Sets the platform agent AID.
	 *
	 * @param platformAgentAID the new platform agent AID
	 */
	public void setPlatformAgentAID(AID platformAgentAID) {
		this.platformAgentAID = platformAgentAID;
	}
	

	public boolean getTradingToken() {
		// TODO Auto-generated method stub
		return TradingToken;
	}

	public int getAmountMessagesSended() {
		// TODO Auto-generated method stub
		return amountMessagesSended;
	}
	
	public void increaseAmountMessagesSended() {
		this.amountMessagesSended += 1;
	}
	
	public void setAmountMessagesSended(int i) {
		this.amountMessagesSended += i;
	}

	public void setOpenGridNodes(List<AID> gridNodes) {
		this.openGridNodes=gridNodes;		
	}

	public void decreaseOwnEnergyAmount(float deliveredEnergyFloat) {
		float e = this.ownEnergyOffer.getEnergyAmountFloat()- deliveredEnergyFloat;
		this.ownEnergyOffer.setEnergyAmountFloat(e);
		
	}

	public int getNumberOfNodes() {
		// TODO Auto-generated method stub
		return gridNodeMax;
	}

	public void incrementStartWaitingIteration() {
		this.startWaitingIteration = this.startWaitingIteration+1;
		
	}

}
