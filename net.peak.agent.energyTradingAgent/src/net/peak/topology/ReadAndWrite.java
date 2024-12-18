package net.peak.topology;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import net.peak.agent.ConnetionCRIEPILab.MeasurementDataModel;
import net.peak.agent.energyTradingAgent.EnergyTradingAgent;
import net.peak.agent.energyTradingAgent.InternalDataModel.Storage;
import net.peak.datamodel.communication.EnergyResult;
import net.peak.datamodel.configuration.PeakConfiguration;
import net.peak.datamodel.configuration.PeakConfiguration.SeasonEnum;
import net.peak.datamodel.configuration.PeakConfiguration.SimulationTypeEnum;

public class ReadAndWrite {
	
	private EnergyTradingAgent energyTradingAgent;
	private String metaPathResults;
	private String metaPathInputData;
	public List<Double> loadDataHeatPump;

	/**
	 * Constructor to initialize the ReadAndWrite class with the given EnergyTradingAgent.
	 * 
	 * @param networkAgent The agent associated with this class.
	 */
	public ReadAndWrite(EnergyTradingAgent networkAgent) {
		this.energyTradingAgent = networkAgent;
		this.metaPathResults = this.energyTradingAgent.getInternalDataModel().getMetaFolderPathResults();
		this.metaPathInputData = this.energyTradingAgent.getInternalDataModel().getMetaFolderPathInputData();
	}

	/**
	 * Reads a CSV file and updates the given map of nodes based on the content.
	 * 
	 * @param map The map to be updated with nodes.
	 * @param nodeName The name of the node for which data is being read.
	 * @return The updated map with node data.
	 */
	public Map<String, Node> readCSV(Map<String, Node> map, String nodeName) {
		SimulationTypeEnum type = this.energyTradingAgent.getInternalDataModel().getSimulationTypeEnum();
		String path;

		switch (type) {
			case IEEE5: 
				path = metaPathInputData + "IEEE5.csv";
				break;
			case IEEE10:
				path = metaPathInputData + "IEEE10.csv";
				break; 
			case IEEE33: 
				path = metaPathInputData + "IEEE33.csv";
				break;
			case IEEE119: 
				path = metaPathInputData + "IEEE119.csv";
				break;
			case SIMULATIONCRIEPI: 
				path = metaPathInputData + "CRIEPI-EV-Default.csv";
				break;
			case TESTCRIEPI: 
				path = metaPathInputData + "TestCriepiConfiguration.csv";
				break;
			default: 
				path = metaPathInputData + "IEEE33.csv";
				break;
		}

		String line = "";
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			while ((line = br.readLine()) != null) {
				String[] values = line.split(";");

				if (values[0].equals(nodeName)) {
					if (!map.containsKey(values[1]) && !values[1].isEmpty()) {
						map.put(values[1], new Node(values[1]));
					}
					if (!values[1].isEmpty() && !values[2].isEmpty()) {
						map.get(nodeName).addAdjacentNode(map.get(values[1]), Double.parseDouble(values[2]));
						map.get(values[1]).addAdjacentNode(map.get(nodeName), Double.parseDouble(values[2]));
					}
					if (!values[3].isEmpty()) {
						map.get(nodeName).setEnergySourceCapacity(Double.parseDouble(values[3]));
					}
					if (!values[4].isEmpty()) {
						map.get(nodeName).setEnergyStorageCapacity(Double.parseDouble(values[4]));
					}
					if (!values[5].isEmpty()) {
						map.get(nodeName).setEnergyConsumption(Double.parseDouble(values[5]));
					}
					if (!values[6].isEmpty()) {
						map.get(nodeName).setEVBoolean(Double.parseDouble(values[6]));
					}
					if (!values[7].isEmpty()) {
						map.get(nodeName).setHeatPumpBoolean(Double.parseDouble(values[7]));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * Reads PV production data from a CSV file.
	 * 
	 * @return A list of PV production data.
	 */
	public List<Double> pvProductionDefault() {
		String path;
		PeakConfiguration peakConfiguration = new PeakConfiguration();
		SimulationTypeEnum sType = peakConfiguration.simulationTypeEnum;
		SeasonEnum season = peakConfiguration.seasonEnum;

		if (sType == SimulationTypeEnum.SIMULATIONCRIEPI) {
			path = metaPathInputData + "/pvProductionJapan/15min/" + season.getValue() + "/PV_Generation_Japan_" + season.getValue() + "_" + this.energyTradingAgent.getLocalName() + ".csv";
		} else {
			path = metaPathInputData + "/pvProductionGermany/15min/" + season.getValue() + "/PV_Generation_Germany_" + season.getValue() + "_" + this.energyTradingAgent.getLocalName() + ".csv";
		}

		int timeResolution = this.energyTradingAgent.getInternalDataModel().getTimeResolution();
		if (timeResolution == 1) {
			path = metaPathInputData + "pvProduction_1min.csv";
		}

		List<Double> pvData = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {
				if (i > 0) {
					String[] values = line.split(",");
					if (!values[0].isEmpty()) {
						pvData.add(Double.parseDouble(values[1]));
					}
				}
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return pvData;
	}

	/**
	 * Reads load data from a CSV file.
	 * 
	 * @return A list of load data.
	 */
	public List<Double> loadDataDefault() {
		String path;
		PeakConfiguration peakConfiguration = new PeakConfiguration();
		SimulationTypeEnum sType = peakConfiguration.simulationTypeEnum;
		SeasonEnum season = peakConfiguration.seasonEnum;

		if (sType == SimulationTypeEnum.SIMULATIONCRIEPI) {
			path = metaPathInputData + "/loadScheduleConsumptionJapan/15min/" + season.getValue() + "/loadScheduleJapan" + season.getValue() + "_" + this.energyTradingAgent.getLocalName() + ".csv";
		} else {
			path = metaPathInputData + "/loadScheduleH0Germany/15min/" + season.getValue() + "/loadSchedule" + season.getValue() + "H0_" + this.energyTradingAgent.getLocalName() + ".csv";
		}

		int timeResolution = this.energyTradingAgent.getInternalDataModel().getTimeResolution();
		if (timeResolution == 1) {
			path = metaPathInputData + "/loadScheduleWinterH0/1min/loadScheduleWinterH0_" + this.energyTradingAgent.getLocalName() + ".csv";
		}

		List<Double> loadData = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {
				if (i > 0) {
					String[] values = (sType == SimulationTypeEnum.SIMULATIONCRIEPI) ? line.split(",") : line.split(";");
					if (!values[0].isEmpty()) {
						loadData.add(Double.parseDouble(values[1]));
					}
				}
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return loadData;
	}

	/**
	 * Reads EV charging station load data from a CSV file.
	 * 
	 * @return A list of load data for charging stations.
	 */
	public List<Double> loadDataChargingStation() {
		String path = metaPathInputData + "/loadScheduleLadesaeule/15min/loadScheduleLadesaeule_" + this.energyTradingAgent.getLocalName() + ".csv";
		int timeResolution = this.energyTradingAgent.getInternalDataModel().getTimeResolution();
		if (timeResolution == 1) {
			path = metaPathInputData + "/loadScheduleLadesaeule/1min/loadScheduleLadesaeule_" + this.energyTradingAgent.getLocalName() + ".csv";
		}

		List<Double> loadData = new ArrayList<>();
		int columEVData = 0;
		SimulationTypeEnum sType = this.energyTradingAgent.getInternalDataModel().getSimulationTypeEnum();

		if (sType == SimulationTypeEnum.SIMULATIONCRIEPI) {
			path = metaPathInputData + "loadScheduleEVCRIEPI_Default15min.csv";
			if (timeResolution == 1) {
				path = metaPathInputData + "loadScheduleEVCRIEPI_Default1min.csv";
			}
			columEVData = Integer.parseInt(this.energyTradingAgent.getLocalName()) / 1000;
		} else {
			columEVData = 1;
		}

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {
				if (i > 0) {
					String[] values = line.split(";");
					if (!values[0].isEmpty()) {
						loadData.add(Double.parseDouble(values[columEVData]));
					}
				}
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return loadData;
	}

	/**
	 * Reads heat pump load data from a CSV file.
	 * 
	 * @return A list of load data for heat pumps.
	 */
	public List<Double> loadDataHeatPump() {
		String path = metaPathInputData + "/loadScheduleWaermepumpe/15min/loadScheduleWaermepumpe_" + this.energyTradingAgent.getLocalName() + ".csv";
		int timeResolution = this.energyTradingAgent.getInternalDataModel().getTimeResolution();
		if (timeResolution == 1) {
			path = metaPathInputData + "/loadScheduleWaermepumpe/1min/loadScheduleWaermepumpe_" + this.energyTradingAgent.getLocalName() + ".csv";
		}

		List<Double> loadData = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {
				if (i > 0) {
					String[] values = line.split(";");
					if (values != null && !values[0].isEmpty()) {
						loadData.add(Double.parseDouble(values[1]));
					}
				}
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return loadData;
	}

	/**
	 * Exports the provided data to a text file.
	 * 
	 * @param name The name of the file to write to.
	 * @param list The list of nodes to be written.
	 * @param provide Whether the data is for providing energy or consuming energy.
	 */
	public void exportData(String name, List<Node> list, boolean provide) {
		if (provide) {
			name = name + "P.txt";
		} else {
			name = name + "C.txt";
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(metaPathResults + name))) {
			for (Node node : list) {
				if (node.getProvideScore() > 0) {
					writer.write(node.getName());
					if (provide) {
						writer.write(" -> ProvideScore: ");
						writer.write(String.format(Locale.GERMAN, "%.2f", node.getProvideScore()));
					} else {
						writer.write(" -> ConsumptionScore: ");
						writer.write(String.format(Locale.GERMAN, "%.2f", node.getConsumptionScore()));
					}
					writer.write("\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the current status of nodes to a CSV file.
	 * 
	 * @param name The name of the file to write to.
	 * @param map  The map containing node data.
	 */
	public void writeStatus(String name, Map<String, Node> map) {
		name = name + ".csv";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(metaPathResults + name))) {
			writer.write("Name;Distance;Gen_Capacity;Stor_Cap;Cons_Cap;ProvideScore;ConsumptionScore\n");
			for (Node node : map.values()) {
				writer.write(node.getName() + ";");
				writer.write(String.format(Locale.GERMAN, "%f", node.getDistance()) + ";");
				writer.write(String.format(Locale.GERMAN, "%f", node.getEnergySourceCapacity()) + ";");
				writer.write(String.format(Locale.GERMAN, "%f", node.getEnergyStorageCapacity()) + ";");
				writer.write(String.format(Locale.GERMAN, "%f", node.getEnergyConsumption()) + ";");
				writer.write(String.format(Locale.GERMAN, "%f", node.getProvideScore()) + ";");
				writer.write(String.format(Locale.GERMAN, "%f", node.getConsumptionScore()) + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the trading results to a CSV file.
	 * 
	 * @param resultListGetting The list of energy results to be written.
	 * @param storage The storage data associated with the energy results.
	 * @param name The name of the file to write to.
	 */
	public void writeTradingResults(HashMap<String, EnergyResult> resultListGetting, Storage storage, String name) {
		if (this.energyTradingAgent.getInternalDataModel().getPeriodNumber() < this.energyTradingAgent.getInternalDataModel().getMaxPeriodNumber()) {

			double deviation2Offer = 0;
			double amountTrades = 0;
			double sumFinancialGain = 0;
			double priceVariance = 0;
			double percentageExternalEnergyNeeded = 0;
			double amountEnergyTraded = 0;
			double amountExternalEnergy = 0;
			double sumTotalEnergy = 0;
			double sumEnergyBuied = 0;
			double sumEnergySold = 0;
			double sumEnergyCosts = 0;
			double sumEnergyEarnings = 0;
			String ownIpAddress;

			PeakConfiguration peakConfig = new PeakConfiguration();
			ownIpAddress = peakConfig.ownIPAddress;

			int i = 0;
			EnergyResult lastEnergyResult = new EnergyResult();
			HashMap<String, EnergyResult> resultList = new HashMap<>();
			if (resultListGetting != null) {
				resultList.putAll(resultListGetting);
			}
			HashMap<Integer, Double> SOCListOld = this.energyTradingAgent.getInternalDataModel().getSOCList();
			HashMap<Integer, Double> SOCListNew = new HashMap<>(SOCListOld);

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(metaPathResults + "results" + name + ".csv"))) {
				writer.write("OfferAgentName;AskedAgentName;OwnIPAddress;TradingPeriod;TradingCycle;TransactionID;CurrentTime;AmountEnergyOffered;AmountPriceOffered;AmountEnergyAsked;AmountPriceAsked;AmountEnergyMatched;PriceMatched;OpenEnergyOffer;OpenEnergyAsked;SOC;CommunicationTime;TradingTime;TradingFinished;CPULoad;RAMLoad\n");
				for (Map.Entry<String, EnergyResult> energyResultMap : resultList.entrySet()) {
					String transactionID = energyResultMap.getKey();
					EnergyResult energyResult = energyResultMap.getValue();
					writer.write(energyResult.getAgentOfferedEnergy() + ";");
					writer.write(energyResult.getAgentAskedEnergy() + ";");
					writer.write(ownIpAddress + ";");
					writer.write(energyResult.getActualPeriod() + ";");
					writer.write(energyResult.getActualTradingCycle() + ";");
					writer.write(transactionID + ";");
					writer.write(energyResult.getTimeStampMS() + ";");
					writer.write(String.format(Locale.GERMAN, "%f", energyResult.getInitialEnergyAmountOffered()) + ";");
					writer.write(String.format(Locale.GERMAN, "%f", energyResult.getInitialTransactionPriceOffered()) + ";");
					writer.write(String.format(Locale.GERMAN, "%f", energyResult.getInitialEnergyAmountAsked()) + ";");
					writer.write(String.format(Locale.GERMAN, "%f", energyResult.getInitialTransactionPriceAsked()) + ";");

					writer.write(String.format(Locale.GERMAN, "%f", energyResult.getDeliveredEnergyFloat()) + ";");
					writer.write(String.format(Locale.GERMAN, "%f", energyResult.getEnergyPriceMatched()) + ";");
					writer.write(String.format(Locale.GERMAN, "%f", energyResult.getOpenEnergyAmountOffer()) + ";");
					writer.write(String.format(Locale.GERMAN, "%f", energyResult.getOpenEnergyAmountAsked()) + ";");
					if (i < SOCListNew.size()) {
						writer.write(String.format(Locale.GERMAN, "%f", SOCListNew.get(i)) + ";");
					} else {
						writer.write(String.format(Locale.GERMAN, "%f", 0.0) + ";");
					}
					long endTime = System.currentTimeMillis();
					this.energyTradingAgent.getInternalDataModel().setEndTimeEnergyTrading(endTime);
					double calculationTime = endTime - this.energyTradingAgent.getInternalDataModel().getStartTimeEnergyTrading();
					this.energyTradingAgent.getInternalDataModel().addEnergyTradingTime(calculationTime);
					if (this.energyTradingAgent.getInternalDataModel().getEnergyTradingTime().containsKey(energyResult.getActualPeriod())) {
						calculationTime = this.energyTradingAgent.getInternalDataModel().getEnergyTradingTime().get(energyResult.getActualPeriod());
					} else {
						calculationTime = 0;
					}
					writer.write(String.format(Locale.GERMAN, "%.2f", energyResult.getCommuncationTimeInMs()) + ";");
					writer.write(String.format(Locale.GERMAN, "%.2f", calculationTime) + ";");
					writer.write(energyResult.isTradingFinished() + ";");
					writer.write(String.format(Locale.GERMAN, "%.4f", energyResult.getCpuLoad()) + ";");
					writer.write(String.format(Locale.GERMAN, "%.4f", energyResult.getRamLoad()) + "\n");

					if (energyResult.getEnergyPriceMatched() > 0 && energyResult.getDeliveredEnergyFloat() > 0) {
						amountTrades++;
						sumFinancialGain += energyResult.getEnergyPriceMatched() * energyResult.getDeliveredEnergyFloat();
						sumTotalEnergy += energyResult.getDeliveredEnergyFloat();
					}
					if (energyResult.getAgentOfferedEnergy().equalsIgnoreCase(this.energyTradingAgent.getLocalName())) {
						sumEnergyEarnings += energyResult.getEnergyPriceMatched() * energyResult.getDeliveredEnergyFloat();
						sumEnergySold += energyResult.getDeliveredEnergyFloat();
					} else {
						sumEnergyCosts += energyResult.getEnergyPriceMatched() * energyResult.getDeliveredEnergyFloat();
						sumEnergyBuied += energyResult.getDeliveredEnergyFloat();
					}
					if (i > 0) priceVariance += energyResult.getEnergyPriceMatched() - lastEnergyResult.getEnergyPriceMatched();
					amountEnergyTraded += energyResult.getDeliveredEnergyFloat();
					amountExternalEnergy += energyResult.getExternalEnergyAmount();

					lastEnergyResult = energyResult;
					i++;
				}
				deviation2Offer = deviation2Offer / amountTrades;
				percentageExternalEnergyNeeded = amountExternalEnergy / (amountExternalEnergy + amountEnergyTraded);

				writer.write("Deviation2Offer;");
				writer.write(deviation2Offer + "\n");
				writer.write("AmountTrades;");
				writer.write(amountTrades + "\n");
				writer.write("AverageFinancialGain;");
				writer.write(sumFinancialGain / sumTotalEnergy + "\n");
				writer.write("AverageFinancialEarnings;");
				writer.write(sumEnergyEarnings / sumEnergySold + "\n");
				writer.write("AverageFinancialCosts;");
				writer.write(sumEnergyCosts / sumEnergyBuied + "\n");
				writer.write("PriceDeviation;");
				writer.write(priceVariance + "\n");
				writer.write("PercentageExternalEnergyNeeded;");
				writer.write(percentageExternalEnergyNeeded + "\n");
				writer.write("AmountMessagesSended;");
				writer.write(this.energyTradingAgent.getInternalDataModel().getAmountMessagesSended() + "\n");

				writer.close();
				resultList.clear();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Writes the energy data for CRIEPI simulation to a CSV file.
	 */
	public void writeEnergyDataCRIEPI() {
		HashMap<Integer, Double> originalMap = new HashMap<>(this.energyTradingAgent.getInternalDataModel().getPlannedEnergyAmountMapOriginal());
		HashMap<Integer, Double> measuredMap = new HashMap<>(this.energyTradingAgent.getInternalDataModel().getMeasuredEnergyAmountMap());
		HashMap<Integer, Double> adaptedMap = new HashMap<>(this.energyTradingAgent.getInternalDataModel().getPlannedEnergyAmountMapAdaptedMeasurement());

		Set<Integer> periods = originalMap.keySet();
		periods.retainAll(measuredMap.keySet());
		periods.retainAll(adaptedMap.keySet());
		String name = this.energyTradingAgent.getLocalName();

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(metaPathResults + "resultsCRIEPI" + name + ".csv"))) {
			writer.write("Period;OriginalEnergy;MeasuredPower;AdaptedEnergy\n");

			for (Integer period : periods) {
				Double originalEnergy = originalMap.get(period);
				Double measuredEnergy = measuredMap.get(period);
				Double adaptedEnergy = adaptedMap.get(period);
				writer.write(period + ";");
				writer.write(String.format(Locale.GERMAN, "%.2f", originalEnergy) + ";");
				writer.write(String.format(Locale.GERMAN, "%.2f", measuredEnergy) + ";");
				writer.write(String.format(Locale.GERMAN, "%.2f", adaptedEnergy) + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the measurement data to a CSV file.
	 */
	public void writeIntervallMeasurementsToCSV() {
		String name = this.energyTradingAgent.getLocalName();
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(metaPathResults + "measurementCRIEPI" + name + ".csv"))) {
			writer.write("Period;Iteration;CurrentRMS; VoltageRMS; ActivePower; ActivePowerP2; ActivePowerP12; ReactivePower; ApparantPower; PowerFactor; Rho; Frequency;\n");
			HashMap<Integer, HashMap<Integer, MeasurementDataModel>> periodMeasurements = this.energyTradingAgent.getInternalDataModel().getPeriodMeasurements();

			for (Map.Entry<Integer, HashMap<Integer, MeasurementDataModel>> periodEntry : periodMeasurements.entrySet()) {
				int period = periodEntry.getKey();
				for (Map.Entry<Integer, MeasurementDataModel> iterationEntry : periodEntry.getValue().entrySet()) {
					int iteration = iterationEntry.getKey();
					MeasurementDataModel measurementDataModel = iterationEntry.getValue();
					writer.write(period + ";");
					writer.write(iteration + ";");
					writer.write(String.format(Locale.GERMAN, "%f", measurementDataModel.getCurrentRMS()) + ";");
					writer.write(String.format(Locale.GERMAN, "%f", measurementDataModel.getVoltageRMS()) + ";");
					writer.write(String.format(Locale.GERMAN, "%f", measurementDataModel.getActivePower()) + ";");
					writer.write(String.format(Locale.GERMAN, "%f", measurementDataModel.getActivePowerP2()) + ";");
					writer.write(String.format(Locale.GERMAN, "%f", measurementDataModel.getActivePowerP12()) + ";");
					writer.write(String.format(Locale.GERMAN, "%f", measurementDataModel.getReactivePower()) + ";");
					writer.write(String.format(Locale.GERMAN, "%f", measurementDataModel.getApparentPower()) + ";");
					writer.write(String.format(Locale.GERMAN, "%f", measurementDataModel.getPowerFactor()) + ";");
					writer.write(String.format(Locale.GERMAN, "%f", measurementDataModel.getPhaseAngle()) + ";");
					writer.write(String.format(Locale.GERMAN, "%f", measurementDataModel.getFrequency()) + "\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Write Computation Load to csv: 
	public void writeComputationalLoadToCsv() {
		
		
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(metaPathResults +"computationalLoad"+this.energyTradingAgent.getLocalName()+".csv"))) {
			
			HashMap<Integer, HashMap<Integer, double[]>> computationalLoadMap = this.energyTradingAgent.getInternalDataModel().getComputationalLoadMap();
            // Schreibe CSV-Header
            writer.write("Period;Iteration;CPULoad;RAMLoad\n");

            // Iteriere über die äußere HashMap (Iteration)
            for (Integer iteration : computationalLoadMap.keySet()) {
                HashMap<Integer, double[]> periodMap = computationalLoadMap.get(iteration);

                // Iteriere über die innere HashMap (Periode)
                for (Integer period : periodMap.keySet()) {
                    double[] values = periodMap.get(period);
                    double cpuLoad = values[0];
                    double ramLoad = values[1];

                    // Schreibe die Daten in CSV-Format mit 4 Nachkommastellen
                    writer.write(String.format("%d;%d;%.4f;%.4f\n", period, iteration, cpuLoad, ramLoad));
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}

	/**
	 * Writes a matrix of results to a CSV file.
	 * 
	 * @param resultTable The matrix containing the results.
	 * @param amountAsks The number of asks in the matrix.
	 * @param amountOffers The number of offers in the matrix.
	 */
	public void writeMatrix(double[][] resultTable, int amountAsks, int amountOffers) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(metaPathResults + "resultsMatrix.csv"))) {
			for (int i = 0; i < amountAsks + 1; i++) {
				for (int j = 0; j < amountOffers + 1; j++) {
					writer.write(String.format(Locale.GERMAN, "%f", resultTable[i][j]) + ";");
				}
				writer.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Placeholder for KPI calculations.
	 */
	public void doKPICalculations() {
		// Placeholder method for KPI calculations.
	}

}
