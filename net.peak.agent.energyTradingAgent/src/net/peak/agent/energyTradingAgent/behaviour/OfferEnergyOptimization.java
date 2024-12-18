package net.peak.agent.energyTradingAgent.behaviour;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.enflexit.jade.behaviour.AbstractTimingBehaviour;
import jade.core.AID;
import net.peak.agent.ConnetionCRIEPILab.GetMeasurementBehaviour;
import net.peak.agent.energyTradingAgent.EnergyTradingAgent;
import net.peak.agent.energyTradingAgent.InternalDataModel;
import net.peak.agent.energyTradingAgent.InternalDataModel.OfferAnswerType;
import net.peak.datamodel.communication.EnergyTransaction;
import net.peak.datamodel.communication.PutEnergyTransaction;
import net.peak.datamodel.configuration.PeakConfiguration;
import net.peak.datamodel.configuration.PeakConfiguration.SimulationTypeEnum;
import com.sun.management.OperatingSystemMXBean;

public class OfferEnergyOptimization extends AbstractTimingBehaviour {

	private static final long serialVersionUID = -8722052770033937966L;
	private EnergyTradingAgent energyTradingAgent;
	private HashMap<String, EnergyTransaction> listReceivedEnergyOffers;
	private HashMap<String, EnergyTransaction> originalListReceivedEnergyOffers;
	private HashMap<String, EnergyTransaction> listOpenEnergyOffers;
	private AID receiverAID;
	private EnergyTransaction receivedEnergyTransaction = new EnergyTransaction();
	private EnergyTransaction offeredEnergyTransaction = new EnergyTransaction();
	private EnergyTransaction ownEnergyNeed; 
	private OfferAnswerType offerAnswerType;
	private long waitTimeBase = 5000;
	private int waitTimeMinFactor = 1;
	private int waitTimeMaxFactor = 10;
	private float openEnergyAmount = 0;
	private double maxDifferenceOfferd2OwnPrice; 
	private String offer;
	private String ask;
	private double decentRateED;
	private List<String> temporaryTransactionID = new ArrayList<>();
	
	/**
	 * Constructor to initialize the OfferEnergyOptimization behaviour.
	 */
	public OfferEnergyOptimization(EnergyTradingAgent energyTradingAgent2, Duration interval, Duration offset,
			ExecutionTiming executionTiming) {
		super(energyTradingAgent2, calculateStartInstant(energyTradingAgent2, interval, offset), offset, executionTiming);
		this.energyTradingAgent = energyTradingAgent2;
	}

	/**
	 * The main action method that is triggered periodically.
	 */
	@Override
	public void performAction() {
		// Update waiting iteration count
		this.energyTradingAgent.getInternalDataModel().setStartWaitingIteration(
				this.energyTradingAgent.getInternalDataModel().getStartWaitingIteration() + 1);

		PeakConfiguration peakConfiguration = new PeakConfiguration();
		decentRateED = peakConfiguration.decentRateED;

		// Special handling for CRIEPI simulations
		if(peakConfiguration.simulationTypeEnum.equals(SimulationTypeEnum.TESTCRIEPI)) {
			handleCRIEPISimulation();
		}
		
		//Write CPU and RAM Load
		writeCPUandRAMLoad();
		
		// Retrieve and update internal data
		maxDifferenceOfferd2OwnPrice = this.energyTradingAgent.getInternalDataModel().getMaxDifferenceOfferd2OwnPrice();
		this.energyTradingAgent.getInternalDataModel().increaseTradingCyclesCnt();
		offer = this.energyTradingAgent.getInternalDataModel().Offer;
		ask = this.energyTradingAgent.getInternalDataModel().Ask;

		originalListReceivedEnergyOffers = this.energyTradingAgent.getInternalDataModel().getListEnergyOffers();
		int sizeList = this.originalListReceivedEnergyOffers.size();
		
		if (sizeList > 0) {
			openEnergyAmount = this.energyTradingAgent.updateOpenEnergyAmount();
			listReceivedEnergyOffers = this.energyTradingAgent.getInternalDataModel().getListEnergyOffers();
		} else {
			listReceivedEnergyOffers = new HashMap<String, EnergyTransaction>();
		}

		listOpenEnergyOffers = this.energyTradingAgent.getInternalDataModel().getListOpenEnergyOffers();
		ownEnergyNeed = this.energyTradingAgent.getInternalDataModel().getOwnEnergyOffer();
		offerAnswerType = this.energyTradingAgent.getInternalDataModel().getOfferAnswerType();
		String myTradeType = this.energyTradingAgent.getInternalDataModel().getOwnEnergyOffer().getTradeTypeString();
		
		if (myTradeType == null) {
			myTradeType = "";
		}
		
		float amountOfferedEnergy = 0;
		SendEnergyOffer sendEnergyOffer;

		// Iterate through received energy offers and process them
		Iterator<Map.Entry<String, EnergyTransaction>> temporaryEnergyTransactionMap = listReceivedEnergyOffers.entrySet().iterator();
		while(temporaryEnergyTransactionMap.hasNext()) {
			Map.Entry<String, EnergyTransaction> nextFieldReceivedEnergyOffers = temporaryEnergyTransactionMap.next();
			String transactionID = nextFieldReceivedEnergyOffers.getKey();
			EnergyTransaction energyTransaction = nextFieldReceivedEnergyOffers.getValue();
			
			if (myTradeType.equals(ask) || (myTradeType.equals(offer) && energyTransaction.getTradeTypeString().equals(ask))) {
				if (listOpenEnergyOffers.containsKey(transactionID)) {
					calculateMiddlePrice(energyTransaction);
				} else {
					processNewEnergyOffer(myTradeType, energyTransaction, transactionID, amountOfferedEnergy);
				}
			} else {
				sendEnergyOffer = new SendEnergyOffer(energyTradingAgent, energyTransaction, energyTransaction.getOfferingAgent(), OfferAnswerType.REJECT);
				temporaryTransactionID.add(transactionID);
				this.energyTradingAgent.addBehaviour(sendEnergyOffer);
			}
		}
		
		removeItemsOfList();

		// Further process the received energy offers based on the trading state
		if (this.listReceivedEnergyOffers.size() > 0) {
			if (amountOfferedEnergy > this.energyTradingAgent.updateOpenEnergyAmount()) {
				priceOptimization();
			} else if (shouldReoffer()) {
				processReoffer();
			} else {
				rejectAllOffers();
			}
		}

		// Handle the case where the agent is in the offering state
		if (myTradeType.equals(this.offer)) {
			handleReofferCreation();
		}
	}

	private void writeCPUandRAMLoad() {
		
		if (this.energyTradingAgent.getInternalDataModel().getCriepiMeasurementCnt() < this.energyTradingAgent.getInternalDataModel().getMaxCriepiMeasurementCnt()) {
			this.energyTradingAgent.getInternalDataModel().increaseCriepiMeasurementCnt();
		} else {
			// CPU- und Speicherberechnungen nur einmal durchführen
			OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
			double cpuLoad = osBean.getProcessCpuLoad() * 100;
			double usedMemoryPercentage = (double) (osBean.getTotalPhysicalMemorySize() - osBean.getFreePhysicalMemorySize()) / osBean.getTotalPhysicalMemorySize();

			// Datenmodell direkt aufrufen, um unnötige Zwischenvariablen zu vermeiden
			InternalDataModel dataModel = this.energyTradingAgent.getInternalDataModel();
			int period = dataModel.getPeriodNumber();
			int interval = dataModel.getTradingCyclesCnt();

			// HashMap nur einmal durch computeIfAbsent aufrufen
			HashMap<Integer, HashMap<Integer, double[]>> computationalLoadMap = dataModel.getComputationalLoadMap();
			HashMap<Integer, double[]> comutationalLoadMapPeriod = computationalLoadMap.computeIfAbsent(period, k -> new HashMap<>());

			// Berechnete Werte direkt speichern
			comutationalLoadMapPeriod.put(interval, new double[]{cpuLoad, usedMemoryPercentage});

			// Messwertzähler zurücksetzen
			dataModel.setCriepiMeasurementCnt(0);

		}
		
	}

	/**
	 * Handles CRIEPI simulation-specific behavior.
	 */
	private void handleCRIEPISimulation() {
		if (this.energyTradingAgent.getInternalDataModel().getCriepiMeasurementCnt() < this.energyTradingAgent.getInternalDataModel().getMaxCriepiMeasurementCnt()) {
			this.energyTradingAgent.getInternalDataModel().increaseCriepiMeasurementCnt();
		} else {
			GetMeasurementBehaviour gmb = new GetMeasurementBehaviour(this.energyTradingAgent);
			this.energyTradingAgent.addBehaviour(gmb);
			this.energyTradingAgent.getInternalDataModel().setCriepiMeasurementCnt(0);
		}
	}

	/**
	 * Processes a new energy offer based on the trade type.
	 */
	private void processNewEnergyOffer(String myTradeType, EnergyTransaction energyTransaction, String transactionID, float amountOfferedEnergy) {
		SendEnergyOffer sendEnergyOffer;
		if (myTradeType.equals(ask) || (myTradeType.equals(offer) && energyTransaction.getTradeTypeString().equals(ask))) {
			amountOfferedEnergy += energyTransaction.getEnergyAmountFloat();
		} else {
			sendEnergyOffer = new SendEnergyOffer(energyTradingAgent, energyTransaction, energyTransaction.getOfferingAgent(), OfferAnswerType.REJECT);
			temporaryTransactionID.add(transactionID);
			this.energyTradingAgent.addBehaviour(sendEnergyOffer);
		}
	}

	/**
	 * Determines if a reoffer should be made based on the current state.
	 */
	private boolean shouldReoffer() {
		return this.offerAnswerType != OfferAnswerType.REJECT && this.openEnergyAmount > 0 && this.energyTradingAgent.getInternalDataModel().getOwnEnergyOffer().getEnergyAmountFloat() > 0;
	}

	/**
	 * Processes reoffers by iterating through the received energy offers and calculating a middle price.
	 */
	private void processReoffer() {
		Iterator<Map.Entry<String, EnergyTransaction>> temporaryEnergyTransactionMapReoffer = listReceivedEnergyOffers.entrySet().iterator();
		while (temporaryEnergyTransactionMapReoffer.hasNext()) {
			Map.Entry<String, EnergyTransaction> nextFieldReceivedEnergyOffers = temporaryEnergyTransactionMapReoffer.next();
			String transactionID = nextFieldReceivedEnergyOffers.getKey();
			EnergyTransaction energyTransaction = nextFieldReceivedEnergyOffers.getValue();
			if (this.openEnergyAmount > 0 && this.energyTradingAgent.getInternalDataModel().getOwnEnergyOffer().getEnergyAmountFloat() > 0) {
				if (!listOpenEnergyOffers.containsKey(transactionID)) {
					if (energyTransaction.getEnergyAmountFloat() > openEnergyAmount) {
						energyTransaction.setEnergyAmountFloat(openEnergyAmount);
					}
					energyTransaction.setInitialEnergyAmountAsked(this.openEnergyAmount);
					this.listOpenEnergyOffers.put(transactionID, energyTransaction);
					this.openEnergyAmount = this.energyTradingAgent.updateOpenEnergyAmount();
				}
				calculateMiddlePrice(energyTransaction);
			} else {
				sendReofferOrReject(energyTransaction, OfferAnswerType.REJECT);
				temporaryTransactionID.add(energyTransaction.getLocalTransactionID());
			}
		}
		removeItemsOfListWithoutDeleting();
	}

	/**
	 * Rejects all remaining energy offers.
	 */
	private void rejectAllOffers() {
		for (Map.Entry<String, EnergyTransaction> entry : listReceivedEnergyOffers.entrySet()) {
			String key = entry.getKey();
			EnergyTransaction value = entry.getValue();
			SendEnergyOffer sendEnergyOffer = new SendEnergyOffer(this.energyTradingAgent, value, value.getOfferingAgent(), OfferAnswerType.REJECT);
			this.energyTradingAgent.addBehaviour(sendEnergyOffer);
			temporaryTransactionID.add(key);
		}
		removeItemsOfList();
	}

	/**
	 * Handles the creation of a reoffer when the agent is in the offering state.
	 */
	private void handleReofferCreation() {
		EnergyTransaction reOffer = new EnergyTransaction();
		reOffer.setEnergyAmountFloat(this.energyTradingAgent.updateOpenEnergyAmount());

		if (reOffer.getEnergyAmountFloat() > 0) {
			reOffer.setInitialEnergyAmountOffered(reOffer.getEnergyAmountFloat());
			reOffer.setOfferingAgent(this.energyTradingAgent.getAID());
			reOffer.setTradeTypeString(this.offer);
			reOffer.setLocalTransactionID(UUID.randomUUID().toString());

			int gridCnt = this.energyTradingAgent.getInternalDataModel().getGridNodesCnt();
			if (gridCnt < this.energyTradingAgent.getInternalDataModel().getGridNodeMax() - 1) {
				this.energyTradingAgent.getInternalDataModel().increaseGridNodeCnt();
			} else {
				this.energyTradingAgent.getInternalDataModel().setGridNodesCnt(0);
			}
			CreateEnergyOffer ceo = new CreateEnergyOffer(energyTradingAgent, reOffer);
			this.energyTradingAgent.addBehaviour(ceo);
		}
	}

	/**
	 * Removes items from the list of received energy offers without clearing the transaction IDs.
	 */
	private void removeItemsOfListWithoutDeleting() {
		for (int i = 0; i < temporaryTransactionID.size(); i++) {
			listReceivedEnergyOffers.remove(temporaryTransactionID.get(i));
			originalListReceivedEnergyOffers.remove(temporaryTransactionID.get(i));
		}
	}

	/**
	 * Removes items from the list of received energy offers and clears the transaction IDs.
	 */
	private void removeItemsOfList() {
		removeItemsOfListWithoutDeleting();
		temporaryTransactionID.clear();
	}

	/**
	 * Optimizes the price by sorting the received offers and processing them in order.
	 */
	private void priceOptimization() {
		int amountOffers = listReceivedEnergyOffers.size();
		int amountColums = 3;
		int transactionIDIndex = 0;
		int energyIndex = 1;
		int priceIndex = 2;
		boolean energyAmountReached = false;

		Object[][] offerTable = new Object[amountOffers][amountColums];
		int j = 0;

		// Populate offer table with transaction data
		Iterator<Map.Entry<String, EnergyTransaction>> temporaryEnergyTransactionMapReoffer = listReceivedEnergyOffers.entrySet().iterator();
		while (temporaryEnergyTransactionMapReoffer.hasNext()) {
			Map.Entry<String, EnergyTransaction> nextFieldReceivedEnergyOffers = temporaryEnergyTransactionMapReoffer.next();
			String transactionID = nextFieldReceivedEnergyOffers.getKey();
			EnergyTransaction energyTransaction = nextFieldReceivedEnergyOffers.getValue();
			offerTable[j][transactionIDIndex] = transactionID;
			offerTable[j][energyIndex] = energyTransaction.getEnergyAmountFloat();
			offerTable[j][priceIndex] = energyTransaction.getTransactionPrice();
			j++;
		}

		// Sort offers by price (ascending)
		Arrays.sort(offerTable, (a, b) -> {
			double priceA = (float) a[priceIndex];
			double priceB = (float) b[priceIndex];
			return Double.compare(priceA, priceB);
		});

		for (int i = 0; i < offerTable.length; i++) {
			offeredEnergyTransaction = findEnergyOfferInArray(offerTable[i][transactionIDIndex]);

			if ((float) offerTable[i][energyIndex] <= this.energyTradingAgent.updateOpenEnergyAmount() && !energyAmountReached) {
				if (!listOpenEnergyOffers.containsKey(offerTable[i][transactionIDIndex])) {
					listOpenEnergyOffers.put((String) offerTable[i][transactionIDIndex], findEnergyOfferInArray(offerTable[i][transactionIDIndex]));
				}

				if (endCriteriaReached(offerTable[i][priceIndex])) {
					processAcceptedEnergyTransaction(offerTable[i]);
				} else {
					calculateMiddlePrice(offeredEnergyTransaction);
				}
				this.openEnergyAmount = this.energyTradingAgent.updateOpenEnergyAmount();
			} else if (this.energyTradingAgent.updateOpenEnergyAmount() > 0) {
				handleRemainingEnergyOffers(i, offerTable);
			} else {
				energyAmountReached = true;
				EnergyTransaction rejectedEnergyTransaction = findEnergyOfferInArray(offerTable[i][transactionIDIndex]);
				sendReofferOrReject(rejectedEnergyTransaction, OfferAnswerType.REJECT);
			}
		}
		removeItemsOfList();
	}

	/**
	 * Handles remaining energy offers after price optimization.
	 */
	private void handleRemainingEnergyOffers(int i, Object[][] offerTable) {
		if (!listOpenEnergyOffers.containsKey(offerTable[i][0])) {
			EnergyTransaction temporaryEnergyTransaction = findEnergyOfferInArray(offerTable[i][0]);
			if (temporaryEnergyTransaction.getEnergyAmountFloat() > this.openEnergyAmount) {
				temporaryEnergyTransaction.setEnergyAmountFloat(this.openEnergyAmount);
			}
			temporaryEnergyTransaction.setInitialEnergyAmountAsked(this.openEnergyAmount);
			listOpenEnergyOffers.put((String) offerTable[i][0], temporaryEnergyTransaction);
			this.openEnergyAmount = this.energyTradingAgent.updateOpenEnergyAmount();
		}
		this.openEnergyAmount = this.energyTradingAgent.updateOpenEnergyAmount();
		Iterator<Map.Entry<String, EnergyTransaction>> temporaryEnergyTransactionMap = listReceivedEnergyOffers.entrySet().iterator();
		while (temporaryEnergyTransactionMap.hasNext()) {
			Map.Entry<String, EnergyTransaction> nextFieldReceivedEnergyOffers = temporaryEnergyTransactionMap.next();
			String temporaryTransactionID = nextFieldReceivedEnergyOffers.getKey();
			EnergyTransaction temporaryEnergyTransaction = nextFieldReceivedEnergyOffers.getValue();
			if (this.energyTradingAgent.updateOpenEnergyAmount() > 0) {
				if (offerTable[i][0] == temporaryTransactionID) {
					this.offeredEnergyTransaction = temporaryEnergyTransaction;
					this.offeredEnergyTransaction.setInitialEnergyAmountAsked(this.openEnergyAmount);
					this.offeredEnergyTransaction.setEnergyAmountFloat(this.openEnergyAmount);
					calculateMiddlePrice(this.offeredEnergyTransaction);
					break;
				}
			}
		}
	}

	/**
	 * Processes accepted energy transactions by creating a new energy result.
	 */
	private void processAcceptedEnergyTransaction(Object[] offerTableEntry) {
		EnergyTransaction energyTransaction = findEnergyTransactionByOfferTable(offerTableEntry);
		AID resultReceiverAID = findResultReceivingAgent(energyTransaction);

		if (this.energyTradingAgent.updateOpenEnergyAmount() < energyTransaction.getEnergyAmountFloat()
				&& !this.listOpenEnergyOffers.containsKey(energyTransaction.getLocalTransactionID())) {
			sendReofferOrReject(energyTransaction, OfferAnswerType.REJECT);
		} else {
			if (!temporaryTransactionID.contains(energyTransaction.getLocalTransactionID())) {
				CreateEnergyResult cer = new CreateEnergyResult(energyTransaction, this.energyTradingAgent, resultReceiverAID, true);
				this.energyTradingAgent.addBehaviour(cer);
				temporaryTransactionID.add(energyTransaction.getLocalTransactionID());
			}
		}
	}

	/**
	 * Determines if the end criteria for the transaction price have been reached.
	 */
	private boolean endCriteriaReached(Object d) {
		return ((float) d - maxDifferenceOfferd2OwnPrice) <= ownEnergyNeed.getTransactionPrice();
	}

	/**
	 * Finds the corresponding energy transaction based on the offer table entry.
	 */
	private EnergyTransaction findEnergyTransactionByOfferTable(Object[] ds) {
		for (Map.Entry<String, EnergyTransaction> energyTransaction : listReceivedEnergyOffers.entrySet()) {
			if (energyTransaction.getValue().getLocalTransactionID().equals(ds[0])) {
				return energyTransaction.getValue();
			}
		}
		return null;
	}

	/**
	 * Finds the appropriate agent to receive the result of the energy transaction.
	 */
	private AID findResultReceivingAgent(EnergyTransaction energyTransaction) {
		if (energyTransaction.getOfferingAgent() != null) {
			if (energyTransaction.getOfferingAgent().equals(this.energyTradingAgent.getAID())) {
				return energyTransaction.getAskingAgent();
			} else {
				return energyTransaction.getOfferingAgent();
			}
		} else {
			energyTransaction.setOfferingAgent(this.energyTradingAgent.getAID());
			return energyTransaction.getAskingAgent();
		}
	}

	/**
	 * Finds an energy offer in the received offers based on the transaction ID.
	 */
	private EnergyTransaction findEnergyOfferInArray(Object transactionID) {
		for (Map.Entry<String, EnergyTransaction> energyTransaction : listReceivedEnergyOffers.entrySet()) {
			if (transactionID.equals(energyTransaction.getValue().getLocalTransactionID())) {
				return energyTransaction.getValue();
			}
		}
		return null;
	}

	/**
	 * Calculates the middle price between the offered and own price, then sends a reoffer or reject.
	 */
	private void calculateMiddlePrice(EnergyTransaction e) {
		if (this.energyTradingAgent.updateOpenEnergyAmount() >= e.getEnergyAmountFloat() || this.listOpenEnergyOffers.containsKey(e.getLocalTransactionID())) {
			if (endCriteriaReached(e.getTransactionPrice())) {
				receiverAID = findResultReceivingAgent(e);
				if (!temporaryTransactionID.contains(e.getLocalTransactionID())) {
					CreateEnergyResult cer = new CreateEnergyResult(e, this.energyTradingAgent, receiverAID, true);
					this.energyTradingAgent.addBehaviour(cer);
					temporaryTransactionID.add(e.getLocalTransactionID());
				}
			} else {
				double difference = Math.abs(e.getTransactionPrice() - this.ownEnergyNeed.getTransactionPrice());
				if (e.getTransactionPrice() > this.ownEnergyNeed.getTransactionPrice()) {
					e.setTransactionPrice((float) (this.ownEnergyNeed.getTransactionPrice() + difference * this.decentRateED));
				} else {
					e.setTransactionPrice((float) (this.ownEnergyNeed.getTransactionPrice() - difference * this.decentRateED));
				}
				this.ownEnergyNeed.setTransactionPrice(e.getTransactionPrice());
				sendReofferOrReject(e, OfferAnswerType.REOFFER);
				temporaryTransactionID.add(e.getLocalTransactionID());
			}
		} else {
			sendReofferOrReject(e, OfferAnswerType.REJECT);
		}
	}

	/**
	 * Sends a reoffer or rejection for a given energy transaction.
	 */
	private void sendReofferOrReject(EnergyTransaction e, OfferAnswerType oat) {
		AID receiverAID = e.getOfferingAgent().equals(energyTradingAgent.getAID()) ? e.getAskingAgent() : e.getOfferingAgent();
		e.setPeakMemberID(this.energyTradingAgent.transformAID2MemberID(this.energyTradingAgent.getLocalName()));
		if (oat.equals(OfferAnswerType.REJECT)) {
			this.listOpenEnergyOffers.remove(e.getLocalTransactionID());
			temporaryTransactionID.add(e.getLocalTransactionID());
		}
		SendEnergyOffer seo = new SendEnergyOffer(this.energyTradingAgent, e, receiverAID, oat);
		this.energyTradingAgent.addBehaviour(seo);
	}

	/**
	 * Calculates the start instant for the behaviour based on the provided interval and offset.
	 */
	private static Instant calculateStartInstant(EnergyTradingAgent networkAgent, Duration interval, Duration offset) {
		long simulationTime = networkAgent.getTimeMillis();
		ZonedDateTime startAt = Instant.ofEpochMilli(simulationTime).atZone(ZoneId.systemDefault());
		PeakConfiguration peakConfig = new PeakConfiguration();
		
		if (interval == null) {
			interval = Duration.ofSeconds(peakConfig.getTradingIntervallInSeconds());
		}

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

	// Getter and Setter methods for wait time factors
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
