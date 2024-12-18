package net.peak.agent.flexibilityTradingAgent.behaviour;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import net.peak.agent.flexibilityTradingAgent.CongestionManagingAgent;
import net.peak.datamodel.communication.FlexibilityTransaction;
import net.peak.datamodel.configuration.PeakConfiguration;

public class SendCMResults extends OneShotBehaviour {

	private static final long serialVersionUID = 1L;
	
	// Reference to the Congestion Managing Agent
	private CongestionManagingAgent congestionManagingAgent;
	
	// Path where the output results will be stored
	private String OUTPUT_FILE_PATH; 
	
	// Constructor initializing the agent reference
	public SendCMResults(CongestionManagingAgent congestionManagingAgent) {
		this.congestionManagingAgent = congestionManagingAgent;
	}

	@Override
	public void action() {
		// Mark the ADMM calculation as complete
		this.congestionManagingAgent.getInternalDataModel().setADMMCalculating(false);
		
		// Mark the period calculation as done for the current trading period
		this.congestionManagingAgent.getInternalDataModel().addPeriodCalculationDone(this.congestionManagingAgent.getInternalDataModel().getTradingPeriod(), true);
		
		// Set the output file path for result storage
		this.OUTPUT_FILE_PATH = this.congestionManagingAgent.getInternalDataModel().getMetaFolderPath();
		
		// Calculate the total time taken for the computation
		long endTime = System.currentTimeMillis();
		this.congestionManagingAgent.getInternalDataModel().setEndTimeCalculation(endTime);
		double calculationTime = (endTime - this.congestionManagingAgent.getInternalDataModel().getStartTimeCalculation());
		this.congestionManagingAgent.getInternalDataModel().addCalculationTime(calculationTime);
		
		// Send the results to the corresponding output file
		sendResult();
		
		// If recalculation hasn't been done, perform it and send adapted energy values to EDA
		if (this.congestionManagingAgent.getInternalDataModel().isRecalculateAlreadyDone() == false) {
			recalculateCurrentValues();
			this.congestionManagingAgent.sendAdaptedEnergyValues2EDA(true);
		}
	}
	
	// Method to recalculate the current values after the optimization process
	private void recalculateCurrentValues() {
		HashMap<Integer, Double> energyValues = new HashMap<>();
		
		// Retrieve the flexibility transactions for the current period
		HashMap<Integer, HashMap<AID, FlexibilityTransaction>> listFlexibilityForActualPeriod = 
			this.congestionManagingAgent.getInternalDataModel()
				.getOverallFlexibilityTransaction()
				.get(this.congestionManagingAgent.getInternalDataModel().getTradingPeriod());

		// Determine the last iteration (highest key value)
		int lastIteration = listFlexibilityForActualPeriod.keySet()
			.stream()
			.max(Integer::compare)
			.orElse(-1); // Fallback if no entry is found

		// Get the list of flexibility transactions for the last iteration
		HashMap<AID, FlexibilityTransaction> listFlexibilityTransactions = 
			lastIteration != -1 ? listFlexibilityForActualPeriod.get(lastIteration) : null;

		if (listFlexibilityTransactions == null) {
			listFlexibilityTransactions = new HashMap<>();
			System.out.println("[SendCMResults, listFlexibilityTransactions is null]");
		}

		int voltage = this.congestionManagingAgent.getInternalDataModel().getVoltage();

		// Calculate the energy values based on flexibility transactions
		for (Map.Entry<AID, FlexibilityTransaction> listFlexibilityTempo : listFlexibilityTransactions.entrySet()) {
			AID aid = listFlexibilityTempo.getKey();
			FlexibilityTransaction flexValue = listFlexibilityTempo.getValue();
			int aidInt = Integer.parseInt(aid.getLocalName());
			PeakConfiguration pc = new PeakConfiguration();
			float timeDivider = (float) pc.timeDivider;
			float energyValue = flexValue.getPowerFlow().getFloatValue() * voltage * timeDivider;
			energyValues.put(aidInt, (double) energyValue);
		}

		// Store the adapted energy values in the internal data model
		this.congestionManagingAgent.getInternalDataModel()
			.addAdaptedTemporaryEnergyBalanceList(this.congestionManagingAgent.getInternalDataModel().getTradingPeriod(), energyValues);
		
		// Mark recalculation as done
		this.congestionManagingAgent.getInternalDataModel().setRecalculateAlreadyDone(true);

		// Initiate grid congestion identification after recalculation
		IdentifyGridCongestion identifyGridCongestion = new IdentifyGridCongestion(congestionManagingAgent, true);
		this.congestionManagingAgent.addBehaviour(identifyGridCongestion);
	}
	
	// Method to send the computed results to a CSV file
	@SuppressWarnings("resource")
	private void sendResult() {
		String name = this.congestionManagingAgent.getLocalName();
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH + name + ".csv"))) {
		
			// Define the header of the CSV file
			writer.write("OfferAgentName;AskedAgentName;Period;Iteration;AmountFlexibilityOffered;AmountPriceOffered;AmountFlexibilityAsked;AmountPriceAsked;AmountFlexibilityMatched;PriceMatched;CalculationTime");
			writer.write("\n");
			
			// Get the overall flexibility transactions for all periods
			HashMap<Integer, HashMap<Integer, HashMap<AID, FlexibilityTransaction>>> overallFlexibilityTransaction = this.congestionManagingAgent.getInternalDataModel().getOverallFlexibilityTransaction();
			
			// Iterate through each period and transaction to write the results
			for (Map.Entry<Integer, HashMap<Integer, HashMap<AID, FlexibilityTransaction>>> overallMap : overallFlexibilityTransaction.entrySet()) {
				Integer period = overallMap.getKey();
				HashMap<Integer, HashMap<AID, FlexibilityTransaction>> totalListFlexibilityOriginal = overallMap.getValue();
				HashMap<Integer, HashMap<AID, FlexibilityTransaction>> totalListFlexibility = new HashMap<>();
				totalListFlexibility.putAll(totalListFlexibilityOriginal);
				
				if (totalListFlexibility != null) {
					for (Map.Entry<Integer, HashMap<AID, FlexibilityTransaction>> tempoListFlexibility : totalListFlexibility.entrySet()) {
						Integer key = tempoListFlexibility.getKey();
						HashMap<AID, FlexibilityTransaction> val = tempoListFlexibility.getValue();
						
						for (Map.Entry<AID, FlexibilityTransaction> tempoListFlexibilityPerIteration : val.entrySet()) {
							AID keyAID = tempoListFlexibilityPerIteration.getKey();
							FlexibilityTransaction valFT = tempoListFlexibilityPerIteration.getValue();
							
							// Write the results of each transaction to the CSV file
							writer.write(keyAID.getLocalName() + ";");
							writer.write(this.congestionManagingAgent.getLocalName() + ";");
							writer.write(period + ";");
							writer.write(key + ";");
							writer.write(String.format(Locale.GERMAN, "%f", valFT.getInitialEnergyAmountOffered()) + ";");
							writer.write(String.format(Locale.GERMAN, "%f", valFT.getInitialTransactionPriceOffered()) + ";");
							writer.write(String.format(Locale.GERMAN, "%f", valFT.getInitialEnergyAmountAsked()) + ";");
							writer.write(String.format(Locale.GERMAN, "%f", valFT.getInitialTransactionPriceAsked()) + ";");
							writer.write(String.format(Locale.GERMAN, "%f", valFT.getPowerFlow().getFloatValue()) + ";");
							writer.write(String.format(Locale.GERMAN, "%f", valFT.getTransactionPrice()) + ";");
							
							// Write the calculation time for each period
							double calculationTime;
							if (this.congestionManagingAgent.getInternalDataModel().getCalculationTime().containsKey(period)) {
								calculationTime = this.congestionManagingAgent.getInternalDataModel().getCalculationTime().get(period);
							} else {
								calculationTime = 0;
							}
							writer.write(String.format(Locale.GERMAN, "%.2f", calculationTime) + "\n");
						}
					}
				}
			}		
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
