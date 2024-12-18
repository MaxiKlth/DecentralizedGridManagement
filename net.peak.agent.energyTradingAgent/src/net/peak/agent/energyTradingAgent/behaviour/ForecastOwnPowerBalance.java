package net.peak.agent.energyTradingAgent.behaviour;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.sun.management.OperatingSystemMXBean;

import de.enflexit.jade.behaviour.AbstractTimingBehaviour;
import net.peak.agent.ConnetionCRIEPILab.ReceiveMeasureDataOfCRIEPILab;
import net.peak.agent.ConnetionCRIEPILab.SendControlSignalsToCRIEPILab;
import net.peak.agent.energyTradingAgent.EnergyTradingAgent;
import net.peak.agent.energyTradingAgent.InternalDataModel.Storage;
import net.peak.agent.energyTradingAgent.InternalDataModel.SystemTypeEnum;
import net.peak.datamodel.communication.EnergyResult;
import net.peak.datamodel.communication.EnergyTransaction;
import net.peak.datamodel.configuration.PeakConfiguration;
import net.peak.datamodel.configuration.PeakConfiguration.SimulationTypeEnum;
import net.peak.topology.ReadAndWrite;

public class ForecastOwnPowerBalance extends AbstractTimingBehaviour {
    
    private static final long serialVersionUID = 1L;
    private EnergyTradingAgent energyTradingAgent;
    private List<Double> energyDataList = new ArrayList<>();
    private double defaultValue = 100;
    private long waitTimeBase = 5000;
    private int waitTimeMinFactor = 1;
    private int waitTimeMaxFactor = 10;
    private String offer;
    private String ask;
    private boolean storageOn = true;

    public ForecastOwnPowerBalance(EnergyTradingAgent networkAgent, Duration interval, Duration offset, ExecutionTiming executionTiming) {
        super(networkAgent, calculateStartInstant(networkAgent, interval, offset), offset, executionTiming);
        this.energyTradingAgent = networkAgent;
    }

    /**
     * Main method to execute the trading cycle, initializing parameters, 
     * performing actions, and managing trading cycles.
     */
    public void performAction() {
        initTradingParameters();

        if (shouldContinueTrading()) {
            startTradingCycle();
            EnergyTransaction energyOffer = prepareEnergyTransaction();

            if (energyOffer != null) {
                finalizeEnergyTransaction(energyOffer);
            }

            updateTradingCycle();
        } else {
            stopTrading();
        }
    }

    /**
     * Initializes the trading parameters by setting up offer and ask strings, 
     * and clearing grid node counts.
     */
    private void initTradingParameters() {
		offer = this.energyTradingAgent.getInternalDataModel().Offer;
		ask = this.energyTradingAgent.getInternalDataModel().Ask;
        this.energyTradingAgent.getInternalDataModel().setOpenGridNodes(this.energyTradingAgent.gridNodes);
        this.energyTradingAgent.getInternalDataModel().setGridNodesCnt(0);
    }

    /**
     * Determines if the agent should continue trading based on the current period number.
     */
    private boolean shouldContinueTrading() {
        return this.energyTradingAgent.getInternalDataModel().getPeriodNumber() <= this.energyTradingAgent.getInternalDataModel().getMaxPeriodNumber();
    }

    /**
     * Starts a new trading cycle by setting the start time, updating energy offers, 
     * and clearing previous offers.
     */
    private void startTradingCycle() {
        long startTime = System.currentTimeMillis();
        this.energyTradingAgent.getInternalDataModel().setStartTimeEnergyTrading(startTime);

        if (this.energyTradingAgent.updateOwnEnergyOffer() > 0) {
            getExternalEnergyAmount();
        }
        clearPreviousOffers();
    }

    /**
     * Clears the list of previous energy offers and prepares the system for the next cycle.
     */
    private void clearPreviousOffers() {
        this.energyTradingAgent.getInternalDataModel().clearListEnergyOffers();
        this.energyTradingAgent.getInternalDataModel().getListOpenEnergyOffers().clear();

        ReadAndWrite readAndWrite = new ReadAndWrite(energyTradingAgent);
        PeakConfiguration peakConfiguration = new PeakConfiguration();
        if (peakConfiguration.simulationTypeEnum.equals(SimulationTypeEnum.TESTCRIEPI)) {
            readAndWrite.writeIntervallMeasurementsToCSV();
        }
        
        
        //Write ComputationalLoad to CSV
        readAndWrite.writeComputationalLoadToCsv();

        this.energyTradingAgent.getInternalDataModel().setTradingCyclesCnt(0);
        this.energyTradingAgent.getInternalDataModel().setResult2CMAAlreadySent(false);
        this.energyTradingAgent.getInternalDataModel().setTradingAllowed(true);
    }

    /**
     * Prepares an energy transaction based on whether the agent is an offer agent 
     * or an asking agent.
     */
    private EnergyTransaction prepareEnergyTransaction() {
        EnergyTransaction energyOffer = new EnergyTransaction();
        if (this.energyTradingAgent.checkIfOfferAgent()) {
            energyOffer = calculateEnergyBalance();
        } else {
            float energyAmount = (float) getEnergyData(SystemTypeEnum.LOAD);
            energyOffer.setEnergyAmountFloat(adjustEnergyAmount(energyAmount));
            energyOffer = askingSettings(energyOffer);
        }
        return energyOffer;
    }

    /**
     * Adjusts the energy amount based on the agent's adapted energy and additional loads, 
     * then updates the energy maps.
     */
    private float adjustEnergyAmount(float energyAmount) {
        energyAmount += this.energyTradingAgent.getInternalDataModel().getAdaptedEnergy();
        energyAmount += calculateAdditionalLoad(SystemTypeEnum.EV_LOAD, this.energyTradingAgent.getInternalDataModel().getEVBoolean());
        energyAmount += calculateAdditionalLoad(SystemTypeEnum.HEAT_PUMP, this.energyTradingAgent.getInternalDataModel().getHeatPumpBoolean());

        updateEnergyMaps(energyAmount);
        return energyAmount;
    }

    /**
     * Calculates additional load based on the system type (e.g., EV, heat pump) 
     * and whether it is enabled.
     */
    private float calculateAdditionalLoad(SystemTypeEnum systemType, boolean isEnabled) {
        return isEnabled ? (float) getEnergyData(systemType) : 0;
    }

    /**
     * Updates the energy maps with the calculated energy amount for the current period.
     */
    private void updateEnergyMaps(float energyAmount) {
        int periodNumber = this.energyTradingAgent.getInternalDataModel().getPeriodNumber();
        this.energyTradingAgent.getInternalDataModel().getPlannedEnergyAmountMapOriginal().put(periodNumber, (double) energyAmount);

        if (periodNumber > 0) {
            double lastPeriodEnergy = this.energyTradingAgent.getInternalDataModel().getPlannedEnergyAmountMapOriginal().get(periodNumber - 1);
            double newLastPeriodEnergy = lastPeriodEnergy - this.energyTradingAgent.getInternalDataModel().getAdaptedEnergy();
            this.energyTradingAgent.getInternalDataModel().getPlannedEnergyAmountMapAdaptedMeasurement().put(periodNumber - 1, newLastPeriodEnergy);
            this.energyTradingAgent.getInternalDataModel().setAdaptedEnergy(0);
        }

        if (new PeakConfiguration().simulationTypeEnum.equals(SimulationTypeEnum.TESTCRIEPI)) {
            setCalculatedEnergyBalanceForCRIEPILab(energyAmount, ask);
        }
    }

    /**
     * Finalizes the energy transaction by setting grid nodes, energy offers, 
     * and potentially sending results to the congestion management authority.
     */
    private void finalizeEnergyTransaction(EnergyTransaction energyOffer) {
        this.energyTradingAgent.getInternalDataModel().setOpenGridNodes(this.energyTradingAgent.gridNodes);
        this.energyTradingAgent.getInternalDataModel().setOwnEnergyOffer(energyOffer);
        this.energyTradingAgent.getInternalDataModel().setOpenEnergyAmount(energyOffer.getEnergyAmountFloat());

        String transactionID = generateRandomTransactionID();
        energyOffer.setLocalTransactionID(transactionID);
        energyOffer.setPeakMemberID(Integer.parseInt(this.energyTradingAgent.getLocalName()));

        this.energyTradingAgent.getInternalDataModel().setInitialEnergyAmountOfIntervall(energyOffer.getEnergyAmountFloat());

        this.energyTradingAgent.addBehaviour(new CreateEnergyOffer(this.energyTradingAgent, energyOffer));

        if (shouldSendResultToCMA()) {
            sendResultToCMA();
        }
    }

    /**
     * Checks whether the result should be sent to the congestion management authority (CMA).
     */
    private boolean shouldSendResultToCMA() {
        return !this.energyTradingAgent.getInternalDataModel().isResult2CMAAlreadySent() && this.energyTradingAgent.getInternalDataModel().isCongestionManagementActive();
    }

    /**
     * Sends the energy result to the congestion management authority (CMA).
     */
    private void sendResultToCMA() {
        this.energyTradingAgent.addBehaviour(new SendEnergyResult2CMA(energyTradingAgent));
        this.energyTradingAgent.getInternalDataModel().setResult2CMAAlreadySent(true);
    }

    /**
     * Updates the trading cycle by either incrementing the period number or resetting it.
     */
    private void updateTradingCycle() {
        if (this.energyTradingAgent.getInternalDataModel().getPeriodNumber() < this.energyTradingAgent.getInternalDataModel().getMaxPeriodNumber()) {
            this.energyTradingAgent.getInternalDataModel().increasePeriodNumber();
        } else {
            this.energyTradingAgent.getInternalDataModel().setPeriodNumber(0);
        }
    }

    /**
     * Stops the trading process and performs KPI calculations at the end.
     */
    private void stopTrading() {
        this.energyTradingAgent.getForecastOwnPowerBalance().stop();
        this.energyTradingAgent.getPutTradingCyclingTimingBehaviour().stop();
        new ReadAndWrite(this.energyTradingAgent).doKPICalculations();
    }

    /**
     * Calculates the energy balance based on load, production, and storage parameters.
     * Manages the storage system and adjusts the balance accordingly.
     */
    private EnergyTransaction calculateEnergyBalance() {
        double load = calculateTotalLoad();
        double production = getEnergyData(SystemTypeEnum.PRODUCTION);
        double adaptedEnergy = this.energyTradingAgent.getInternalDataModel().getAdaptedEnergy();
        double energyBalance = calculateTemporaryEnergyBalance(load, production, adaptedEnergy);

        if (storageOn) {
            energyBalance = manageStorageAndBalance(energyBalance);
        }

        EnergyTransaction energyOffer = setEnergyOffer(energyBalance);
        updateEnergyMaps(energyOffer.getEnergyAmountFloat());

        if (new PeakConfiguration().simulationTypeEnum.equals(SimulationTypeEnum.TESTCRIEPI)) {
            setCalculatedEnergyBalanceForCRIEPILab(energyOffer.getEnergyAmountFloat(), offer);
        }

        return energyOffer;
    }

    /**
     * Calculates the total load including EV and heat pump loads.
     */
    private double calculateTotalLoad() {
        double load = getEnergyData(SystemTypeEnum.LOAD);
        load += calculateAdditionalLoad(SystemTypeEnum.EV_LOAD, this.energyTradingAgent.getInternalDataModel().getEVBoolean());
        load += calculateAdditionalLoad(SystemTypeEnum.HEAT_PUMP, this.energyTradingAgent.getInternalDataModel().getHeatPumpBoolean());
        return load;
    }

    /**
     * Calculates the temporary energy balance based on load, production, and adapted energy.
     */
    private double calculateTemporaryEnergyBalance(double load, double production, double adaptedEnergy) {
        PeakConfiguration peakConfiguration = new PeakConfiguration();
        double consumptionMultiplier = this.energyTradingAgent.getInternalDataModel().getConsumptionMultiplicator();
        return production * peakConfiguration.pvMultiplicator - load * consumptionMultiplier - adaptedEnergy;
    }

    /**
     * Manages the storage system by charging or discharging it based on the energy balance.
     * Returns the adjusted energy balance after storage management.
     */
    private double manageStorageAndBalance(double temporaryEnergyBalance) {
        Storage myStorage = this.energyTradingAgent.getInternalDataModel().getStorage();
        double restEnergyCapacitykWh = myStorage.getStorageCapacity() - myStorage.getStorageSOC() * myStorage.getStorageCapacity();
        double currentEnergyInStorage = myStorage.getStorageSOC() * myStorage.getStorageCapacity();

        if (temporaryEnergyBalance >= 0) {
            if (temporaryEnergyBalance > restEnergyCapacitykWh) {
                myStorage.chargeStorage(restEnergyCapacitykWh);
                return temporaryEnergyBalance - restEnergyCapacitykWh;
            } else {
                myStorage.chargeStorage(temporaryEnergyBalance);
                return 0;
            }
        } else {
            if (Math.abs(temporaryEnergyBalance) > currentEnergyInStorage) {
                myStorage.dischargeStorage(currentEnergyInStorage);
                return temporaryEnergyBalance - currentEnergyInStorage;
            } else {
                myStorage.dischargeStorage(Math.abs(temporaryEnergyBalance));
                return 0;
            }
        }
    }

    /**
     * Sets up an energy offer based on the calculated energy balance.
     * If the balance is negative, the agent asks for energy; if positive, it offers energy.
     */
    private EnergyTransaction setEnergyOffer(double energyBalance) {
        EnergyTransaction energyOffer = new EnergyTransaction();
        PeakConfiguration peakConfig = new PeakConfiguration();
        boolean debug = peakConfig.tradingStartsFromFirstIteration;
        float energyAmountFloat;

        if (debug) {
            energyAmountFloat = (float) Math.abs(energyBalance);
            energyOffer = offeringSettings(energyOffer);
        } else {
            if (energyBalance < 0) {
                energyAmountFloat = (float) -energyBalance;
                energyOffer = askingSettings(energyOffer);
            } else {
                energyAmountFloat = (float) energyBalance;
                energyOffer = offeringSettings(energyOffer);
            }
        }

        energyOffer.setEnergyAmountFloat(energyAmountFloat);
        return energyOffer;
    }

    /**
     * Sets the calculated energy balance for the CRIEPI lab environment, 
     * adjusting the balance based on measured data and system feedback.
     */
    private void setCalculatedEnergyBalanceForCRIEPILab(double temporaryEnergyBalance, String offerType) {
        int periodNumber = this.energyTradingAgent.getInternalDataModel().getPeriodNumber();
        HashMap<Integer, Double> plannedEnergyAmountMapAdaptedMeasurement = this.energyTradingAgent.getInternalDataModel().getPlannedEnergyAmountMapAdaptedMeasurement();
        HashMap<Integer, Double> measuredEnergyAmountMap = this.energyTradingAgent.getInternalDataModel().getMeasuredEnergyAmountMap();
        measuredEnergyAmountMap.put(periodNumber, getMeasuredEnergyData());

        boolean closedControlLoop = false;
        if (periodNumber > 0 && closedControlLoop) {
            double plannedEnergyBalanceLastPeriod = plannedEnergyAmountMapAdaptedMeasurement.get(periodNumber - 1);
            double adaptedEnergyBalance = temporaryEnergyBalance + plannedEnergyBalanceLastPeriod - measuredEnergyAmountMap.get(periodNumber);
            temporaryEnergyBalance = adaptedEnergyBalance;
        }

        if (periodNumber > 0) {
            temporaryEnergyBalance = plannedEnergyAmountMapAdaptedMeasurement.get(periodNumber - 1);
        } else {
            plannedEnergyAmountMapAdaptedMeasurement.put(periodNumber, temporaryEnergyBalance);
        }

        startCRIEPILabTest(temporaryEnergyBalance, offerType);
        new ReadAndWrite(energyTradingAgent).writeEnergyDataCRIEPI();
    }

    /**
     * Starts a CRIEPI lab test by configuring measurement data receiving 
     * and control signals sending based on the energy balance.
     */
    private void startCRIEPILabTest(double energyAmountFloat, String tradeType) {
        PeakConfiguration peakConf = new PeakConfiguration();

        // Config receiving measurement data
        ReceiveMeasureDataOfCRIEPILab rmd = new ReceiveMeasureDataOfCRIEPILab(this.energyTradingAgent);
        int aidAgent = Integer.parseInt(this.energyTradingAgent.getLocalName());
        rmd.getTotalEnergy(aidAgent, (float) peakConf.timeDivider);

        energyAmountFloat = energyAmountFloat * 2;
        double zeroPowerVoltage = 4.7;
        double maximumOutPower = 4700;
        double voltage = zeroPowerVoltage;

        if (energyAmountFloat < 0) {
            voltage = (maximumOutPower + energyAmountFloat) / 1000;
        } else if (energyAmountFloat < maximumOutPower) {
            voltage = (maximumOutPower + energyAmountFloat) / 1000;
        }

        double current = 0;
        new SendControlSignalsToCRIEPILab().main(null, voltage, current, this.energyTradingAgent, tradeType);
    }

    /**
     * Retrieves measured energy data from the CRIEPI lab.
     */
    private Double getMeasuredEnergyData() {
        PeakConfiguration peakConf = new PeakConfiguration();

        // Config receiving measurement data
        ReceiveMeasureDataOfCRIEPILab rmd = new ReceiveMeasureDataOfCRIEPILab(this.energyTradingAgent);
        int aidAgent = Integer.parseInt(this.energyTradingAgent.getLocalName());
        return (double) rmd.getTotalEnergy(aidAgent, (float) peakConf.timeDivider);
    }

    /**
     * Generates a random transaction ID for energy transactions.
     */
    public String generateRandomTransactionID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Configures the settings for offering energy, setting trade type 
     * and the offering agent.
     */
    private EnergyTransaction offeringSettings(EnergyTransaction energyOffer) {
        energyOffer.setTradeTypeString(offer);
        energyOffer.setOfferingAgent(this.energyTradingAgent.getAID());
        return energyOffer;
    }

    /**
     * Configures the settings for asking energy, setting trade type 
     * and the asking agent.
     */
    private EnergyTransaction askingSettings(EnergyTransaction energyOffer) {
        energyOffer.setTradeTypeString(ask);
        energyOffer.setAskingAgent(this.energyTradingAgent.getAID());
        return energyOffer;
    }

    /**
     * Handles external energy amounts by configuring transactions 
     * and updating the internal data model.
     */
    public void getExternalEnergyAmount() {
        EnergyResult externalEnergyResult = new EnergyResult();
        EnergyTransaction ownEnergyOffer = new EnergyTransaction();
        float energyAmountFloat = this.energyTradingAgent.updateOwnEnergyOffer();
        ownEnergyOffer.setEnergyAmountFloat(energyAmountFloat);
        ownEnergyOffer.setLocalTransactionID(generateRandomTransactionID());
        externalEnergyResult.setDeliveredEnergyFloat(energyAmountFloat);
        externalEnergyResult.setLocalTransactionID(generateRandomTransactionID());
        externalEnergyResult.setExternalEnergyAmount(energyAmountFloat);

        if (this.energyTradingAgent.getInternalDataModel().getOwnEnergyOffer().getTradeTypeString().equals(offer)) {
            configureExternalEnergyOffer(externalEnergyResult, ownEnergyOffer);
        } else {
            configureExternalEnergyAsk(externalEnergyResult, ownEnergyOffer);
        }

        externalEnergyResult.setReceivedEnergyOffer(ownEnergyOffer);
        externalEnergyResult.setOwnEnergyOffer(this.energyTradingAgent.getInternalDataModel().getOwnEnergyOffer());

        externalEnergyResult.setActualPeriod(this.energyTradingAgent.getInternalDataModel().getPeriodNumber());
        externalEnergyResult.setActualTradingCycle(this.energyTradingAgent.getInternalDataModel().getTradingCyclesCnt());

        Date date = new Date();
        long timeStampInMillis = date.getTime();
        externalEnergyResult.setTimeStampMS(timeStampInMillis);
        
   		//Set CPU und RAM Load:
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
	        
        // Get CPU Load
        double cpuLoad = osBean.getProcessCpuLoad();
        externalEnergyResult.setCpuLoad(cpuLoad);
		
		//Get RAM Load
		long totalMemory = osBean.getTotalPhysicalMemorySize();
		long freeMemory = osBean.getFreePhysicalMemorySize();
        long usedMemory = totalMemory - freeMemory;
        
        // Prozentualen Anteil des genutzten Speichers berechnen
        double usedMemoryPercentage = (double) usedMemory / totalMemory;
		externalEnergyResult.setRamLoad(usedMemoryPercentage);

        this.energyTradingAgent.getInternalDataModel().addResult2List(externalEnergyResult, true);
        this.energyTradingAgent.getInternalDataModel().getOwnEnergyOffer().setEnergyAmountFloat(this.energyTradingAgent.updateOwnEnergyOffer());
        saveEnergyResults();
    }

    /**
     * Configures an energy offer for an external transaction.
     */
    private void configureExternalEnergyOffer(EnergyResult externalEnergyResult, EnergyTransaction ownEnergyOffer) {
        externalEnergyResult.setAgentAskedEnergy(this.energyTradingAgent.getInternalDataModel().getExternalEnergySupplierName());
        externalEnergyResult.setAgentOfferedEnergy(this.energyTradingAgent.getLocalName());
        externalEnergyResult.setEnergyPriceMatched(this.energyTradingAgent.getInternalDataModel().getFeedInTariff());
        externalEnergyResult.setInitialEnergyAmountAsked(ownEnergyOffer.getEnergyAmountFloat());
        externalEnergyResult.setInitialTransactionPriceAsked((float) this.energyTradingAgent.getInternalDataModel().getFeedInTariff());
        externalEnergyResult.setInitialEnergyAmountOffered(ownEnergyOffer.getEnergyAmountFloat());
        externalEnergyResult.setInitialTransactionPriceOffered(this.energyTradingAgent.getInternalDataModel().getOwnEnergyOffer().getTransactionPrice());
        ownEnergyOffer.setOfferingAgent(this.energyTradingAgent.getAID());
        ownEnergyOffer.setTransactionPrice((float) this.energyTradingAgent.getInternalDataModel().getFeedInTariff());
        ownEnergyOffer.setAskingAgent(this.energyTradingAgent.getInternalDataModel().getExternalEnergySupplierAID());
        ownEnergyOffer.addEnergyTransaction(ownEnergyOffer);
        externalEnergyResult.setEnergyTransaction(ownEnergyOffer.getEnergyTransaction());
    }

    /**
     * Configures an energy ask for an external transaction.
     */
    private void configureExternalEnergyAsk(EnergyResult externalEnergyResult, EnergyTransaction ownEnergyOffer) {
        externalEnergyResult.setAgentAskedEnergy(this.energyTradingAgent.getLocalName());
        externalEnergyResult.setAgentOfferedEnergy(this.energyTradingAgent.getInternalDataModel().getExternalEnergySupplierName());
        externalEnergyResult.setEnergyPriceMatched(this.energyTradingAgent.getInternalDataModel().getGridEnergyPrice());
        externalEnergyResult.setInitialEnergyAmountOffered(ownEnergyOffer.getEnergyAmountFloat());
        externalEnergyResult.setInitialTransactionPriceOffered((float) this.energyTradingAgent.getInternalDataModel().getGridEnergyPrice());
        externalEnergyResult.setInitialEnergyAmountAsked(ownEnergyOffer.getEnergyAmountFloat());
        externalEnergyResult.setInitialTransactionPriceAsked(this.energyTradingAgent.getInternalDataModel().getOwnEnergyOffer().getTransactionPrice());
        EnergyTransaction externalEnergyOffer = new EnergyTransaction();
        externalEnergyOffer.setTransactionPrice((float) this.energyTradingAgent.getInternalDataModel().getGridEnergyPrice());
        externalEnergyOffer.setEnergyAmountFloat(ownEnergyOffer.getEnergyAmountFloat());
        externalEnergyOffer.setOfferingAgent(this.energyTradingAgent.getInternalDataModel().getExternalEnergySupplierAID());
        externalEnergyOffer.setAskingAgent(this.energyTradingAgent.getAID());
        externalEnergyOffer.addEnergyTransaction(externalEnergyOffer);
        externalEnergyResult.setEnergyTransaction(externalEnergyOffer.getEnergyTransaction());
    }

    /**
     * Saves the energy results to a file.
     */
    private void saveEnergyResults() {
        if (this.energyTradingAgent.getInternalDataModel().getResultListExtended() != null) {
        	

        	
            ReadAndWrite raw = new ReadAndWrite(this.energyTradingAgent);
            HashMap<String, EnergyResult> resultList = new HashMap<>();
            HashMap<String, EnergyResult> resultListOld = this.energyTradingAgent.getInternalDataModel().getResultListExtended();
            if (resultListOld != null) {
                resultList.putAll(resultListOld);
            }
            raw.writeTradingResults(resultList, this.energyTradingAgent.getInternalDataModel().getStorage(), this.energyTradingAgent.getLocalName());
        }
    }

    /**
     * Retrieves energy data based on the system type (e.g., load, production) 
     * and returns a default value if the type is not recognized.
     */
    private double getEnergyData(SystemTypeEnum systemType) {
        PeakConfiguration peakConfiguration = new PeakConfiguration();
        ReadAndWrite rw = new ReadAndWrite(this.energyTradingAgent);
        switch (systemType) {
            case LOAD:
                energyDataList = rw.loadDataDefault();
                break;

            case PRODUCTION:
                energyDataList = rw.pvProductionDefault();
                break;

            case EV_LOAD:
                energyDataList = rw.loadDataChargingStation();
                break;

            case HEAT_PUMP:
                energyDataList = rw.loadDataHeatPump();
                break;

            default:
                return defaultValue;
        }
        return energyDataList.get(this.energyTradingAgent.getInternalDataModel().getPeriodNumber()) / peakConfiguration.timeDivider;
    }

    /**
     * Calculates the start time for the agent's behavior based on the simulation time, 
     * interval, and offset.
     */
    private static Instant calculateStartInstant(EnergyTradingAgent networkAgent, Duration interval, Duration offset) {
        long simulationTime = networkAgent.getTimeMillis();
        ZonedDateTime startAt = Instant.ofEpochMilli(simulationTime).atZone(ZoneId.systemDefault());
        if (interval.getSeconds() >= 1) {
            startAt = startAt.withSecond(0);
        }
        if (interval.toMinutes() >= 1) {
            startAt = startAt.withMinute(0);
        }
        if (interval.toHours() >= 1) {
            startAt = startAt.withHour(0);
        }
        if (offset != null) {
            startAt = startAt.plusNanos(offset.toNanos());
        }
        return startAt.toInstant();
    }

    public long getWaitTimeBase() {
        return waitTimeBase;
    }

    public void setWaitTimeBase(long waitTimeBase) {
        this.waitTimeBase = waitTimeBase;
    }

    public int getWaitTimeMinFactor() {
        return waitTimeMinFactor;
    }

    public void setWaitTimeMinFactor(int waitTimeMinFactor) {
        this.waitTimeMinFactor = waitTimeMinFactor;
    }

    public int getWaitTimeMaxFactor() {
        return waitTimeMaxFactor;
    }

    public void setWaitTimeMaxFactor(int waitTimeMaxFactor) {
        this.waitTimeMaxFactor = waitTimeMaxFactor;
    }
}
