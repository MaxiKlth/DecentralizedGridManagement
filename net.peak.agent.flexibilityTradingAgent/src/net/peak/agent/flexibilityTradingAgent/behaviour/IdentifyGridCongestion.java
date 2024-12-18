package net.peak.agent.flexibilityTradingAgent.behaviour;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import net.peak.agent.flexibilityTradingAgent.CongestionManagingAgent;
import net.peak.datamodel.communication.FlexibilityTransaction;
import net.peak.datamodel.communication.PowerFlow;
import net.peak.datamodel.configuration.PeakConfiguration;

public class IdentifyGridCongestion extends OneShotBehaviour {

    // Fields for managing grid congestion detection
    private CongestionManagingAgent congestionManagingAgent;
    private int voltage;
    private double maxCurrent;
    private boolean congestionExists = false;
    private String OUTPUT_FILE_PATH;
    private boolean afterMatching = false; 
    private PeakConfiguration pc = new PeakConfiguration();
    private double timeDivider = pc.timeDivider;

    // Constructor for initializing with a CongestionManagingAgent
    public IdentifyGridCongestion(CongestionManagingAgent congestionManagingAgent) {
        this.congestionManagingAgent = congestionManagingAgent;
    }

    // Overloaded constructor to handle post-matching checks
    public IdentifyGridCongestion(CongestionManagingAgent congestionManagingAgent, boolean afterMatching) {
        this(congestionManagingAgent);
        this.afterMatching = afterMatching;
    }

    @Override
    public void action() {
        // Set initial values from the agent's internal data model
        voltage = this.congestionManagingAgent.getInternalDataModel().getVoltage();
        maxCurrent = this.congestionManagingAgent.getInternalDataModel().getMaxCurrent();
        OUTPUT_FILE_PATH = this.congestionManagingAgent.getInternalDataModel().getMetaFolderPath();
        int tradingPeriod = this.congestionManagingAgent.getInternalDataModel().getTradingPeriod();

        // Check if current values for this trading period are already present, or if we are in the afterMatching phase
        if (!this.congestionManagingAgent.getInternalDataModel().getCurrentValuesMap().containsKey(tradingPeriod) || this.afterMatching) {
            // Debug: Initialize flexibility transaction with default values
            FlexibilityTransaction flexTransaction = new FlexibilityTransaction();
            flexTransaction.setInitialEnergyAmountAsked(0);
            flexTransaction.setInitialEnergyAmountOffered(0);
            flexTransaction.setInitialTransactionPriceAsked(0);
            flexTransaction.setInitialTransactionPriceOffered(0);
            PowerFlow pf = new PowerFlow();
            pf.setFloatValue(0);
            flexTransaction.setPowerFlow(pf);
            flexTransaction.setTransactionPrice(0);

            // Perform grid congestion identification
            float powerFlowValue = identifyGridCongestion();
            pf.setFloatValue(powerFlowValue);
            flexTransaction.setPowerFlow(pf);
            flexTransaction.setInitialEnergyAmountAsked(powerFlowValue);
            this.congestionManagingAgent.getInternalDataModel().addTemporaryListFlexibilityTransaction(this.congestionManagingAgent.getAID(), flexTransaction);

            // Handle congestion scenarios
            if (congestionExists) {
                if (!this.congestionManagingAgent.getInternalDataModel().getPeriodCalculationDone().containsKey(tradingPeriod)) {
                    this.congestionManagingAgent.getInternalDataModel().addPeriodCalculationDone(tradingPeriod, false);
                    CalculateFlexibilityAvailable caf = new CalculateFlexibilityAvailable(congestionManagingAgent);
                    this.congestionManagingAgent.addBehaviour(caf);
                }
                this.congestionManagingAgent.getInternalDataModel().setADMMCalculating(true);
                this.congestionManagingAgent.getInternalDataModel().setRecalculateAlreadyDone(false);
            } else {
                if (!this.congestionManagingAgent.getInternalDataModel().getPeriodCalculationDone().containsKey(tradingPeriod)) {
                    this.congestionManagingAgent.getInternalDataModel().addPeriodCalculationDone(tradingPeriod, true);
                }
            }
        }
    }

    /**
     * Identifies grid congestion by calculating power flow and checking against the maximum allowed current.
     * 
     * @return The residual current that indicates the extent of congestion.
     */
    private float identifyGridCongestion() {
        // Initialize grid matrix and impedance matrix based on the number of nodes
        IEEEBusMatrix gridMatrix = new IEEEBusMatrix();
        double maxCurrentValue = 0;
        int amountNodes = this.congestionManagingAgent.getInternalDataModel().getMaxNodes();
        int[][] matrix = null;
        double[][] impedanceMatrix = null;

        // Select appropriate matrix based on the number of nodes in the grid
        switch (amountNodes) {
            case 5:
                matrix = gridMatrix.get5Matrix();
                impedanceMatrix = gridMatrix.impedanceMatrix5;
                break;
            case 33:
                matrix = gridMatrix.get33Matrix();
                impedanceMatrix = gridMatrix.impedanceMatrix33;
                break;
            case 119:
                matrix = gridMatrix.get119Matrix();
                impedanceMatrix = gridMatrix.impedanceMatrix119;
                break;
            case 10:
                matrix = gridMatrix.get10Matrix();
                impedanceMatrix = gridMatrix.impedanceMatrix10;
                break;
            case 3:
                matrix = gridMatrix.get3Matrix();
                impedanceMatrix = gridMatrix.impedanceMatrix3;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + amountNodes);
        }

        // Identify line end, side, and start nodes in the grid
        List<Integer> lineEndNodes = identifyLineEndNodes(matrix);
        List<Integer> lineSideNodes = identifyLineSideNodes(matrix);
        List<Integer> lineStartNodes = identifyLineStartNodes(matrix, lineSideNodes);

        // Initialize current and voltage values
        HashMap<Integer, Double> currentValues = initializeCurrentValues(matrix.length - 1);
        HashMap<Integer, Double> energyValues = getEnergyValues();
        HashMap<Integer, Double> voltageValues = initializeVoltageValues(matrix.length - 1, voltage);

        // Calculate power flow and adjust voltages if energy values are present
        if (energyValues != null) {
            maxCurrentValue = calculateCurrentValues(matrix, lineEndNodes, lineStartNodes, currentValues, energyValues, voltageValues);

            // Perform forward sweep for voltage adjustment
            calculateVoltages(matrix, impedanceMatrix, lineStartNodes, lineEndNodes, lineSideNodes, currentValues, voltageValues);

            // Write current values to an Excel file
            writeCurrentValues2Excel(currentValues);

            // Identify and report grid congestion
            float residualCurrent = (float) (maxCurrentValue - maxCurrent);
            if (residualCurrent > 0 && !afterMatching) {
                congestionExists = true;
                return residualCurrent;
            }
        }

        return 0;
    }

    /**
     * Initialize voltage values to the specified voltage level.
     */
    private HashMap<Integer, Double> initializeVoltageValues(int length, double voltage) {
        HashMap<Integer, Double> voltageValues = new HashMap<>();
        for (int i = 0; i <= length; i++) {
            voltageValues.put(i, voltage);
        }
        return voltageValues;
    }

    /**
     * Identify nodes that are at the end of lines in the grid matrix.
     */
    private List<Integer> identifyLineEndNodes(int[][] matrix) {
        List<Integer> lineEndNodes = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            int temporarySumStartConnection = 0;
            for (int j = 0; j < matrix[i].length; j++) {
                temporarySumStartConnection += matrix[i][j];
            }
            if (temporarySumStartConnection == 0) {
                lineEndNodes.add(i + 1);
            }
        }
        return lineEndNodes;
    }

    /**
     * Identify nodes that are at the start of lines in the grid matrix.
     */
    private List<Integer> identifyLineStartNodes(int[][] matrix, List<Integer> lineSideNodes) {
        List<Integer> lineStartNodes = new ArrayList<>();
        lineStartNodes.add(1); // Always add the first node as a start node
        for (int i = 1; i < matrix.length; i++) {
            boolean isSideNode = false;
            if (matrix[i - 1][i] == 0) {
                for (int sideNode : lineSideNodes) {
                    if (matrix[sideNode][i] == 1) {
                        isSideNode = true;
                        break;
                    }
                }
                if (isSideNode) {
                    lineStartNodes.add(i);
                }
            }
        }
        return lineStartNodes;
    }

    /**
     * Identify nodes that are on the side of lines in the grid matrix.
     */
    private List<Integer> identifyLineSideNodes(int[][] matrix) {
        List<Integer> lineSideNodes = new ArrayList<>();
        lineSideNodes.add(1); // Always add the first node as a side node
        for (int i = 1; i < matrix.length; i++) {
            int sumNodes = 0;
            for (int j = 1; j < matrix.length; j++) {
                sumNodes += matrix[i][j];
            }
            if (sumNodes > 1) {
                for (int k = 0; k < sumNodes - 1; k++) {
                    lineSideNodes.add(i);
                }
            }
        }
        return lineSideNodes;
    }

    /**
     * Initialize current values for all nodes.
     */
    private HashMap<Integer, Double> initializeCurrentValues(int length) {
        HashMap<Integer, Double> currentValues = new HashMap<>();
        for (int i = 1; i <= length; i++) {
            currentValues.put(i, 0.0);
        }
        return currentValues;
    }

    /**
     * Get energy values for the current trading period.
     * If after matching, adjust the energy values accordingly.
     */
    private HashMap<Integer, Double> getEnergyValues() {
        HashMap<Integer, Double> energyValues = this.congestionManagingAgent.getInternalDataModel().getPlannedEnergyBalanceList().get(this.congestionManagingAgent.getInternalDataModel().getTradingPeriod());
        if (this.afterMatching) {
            HashMap<Integer, Double> energyAdaptedValues = this.congestionManagingAgent.getInternalDataModel().getAdaptedTemporaryEnergyBalanceList().get(this.congestionManagingAgent.getInternalDataModel().getTradingPeriod());
            for (HashMap.Entry<Integer, Double> energyTempoValues : energyValues.entrySet()) {
                Integer intAid = energyTempoValues.getKey();
                Double energyFloatValue = energyTempoValues.getValue();
                if (energyAdaptedValues.containsKey(intAid)) {
                    energyFloatValue -= energyAdaptedValues.get(intAid);
                    energyValues.replace(intAid, energyFloatValue);
                }
            }
        }
        return energyValues;
    }

    /**
     * Calculate current values for each node based on the energy values and voltages.
     */
    private double calculateCurrentValues(int[][] matrix, List<Integer> lineEndNodes, List<Integer> lineStartNodes, HashMap<Integer, Double> currentValues, HashMap<Integer, Double> energyValues, HashMap<Integer, Double> voltageValues) {
        double maxCurrentValue = 0;

        for (int endNode : lineEndNodes) {
            int actualNode = endNode - 1;
            double temporaryCurrentValue = 0.0;
            boolean alreadyFinished = false;
            boolean isSideLine = false;
            double temporarySideLineValue = 0.0;

            do {
                for (int j = actualNode; j > 0; j--) {
                    if (matrix[j - 1][actualNode] == 1) {
                        if (energyValues.containsKey(actualNode)) {
                            if (j == actualNode && !isSideLine) {
                                temporaryCurrentValue += energyValues.get(actualNode) / timeDivider / voltageValues.get(actualNode);
                            } else if (j != actualNode && !isSideLine) {
                                isSideLine = true;
                                temporarySideLineValue = temporaryCurrentValue + energyValues.get(actualNode) / timeDivider / voltageValues.get(actualNode);
                            } else if (isSideLine) {
                                temporaryCurrentValue = currentValues.get(actualNode) + temporarySideLineValue;
                            }
                            currentValues.put(actualNode, temporaryCurrentValue);

                            if (temporaryCurrentValue > maxCurrentValue) {
                                maxCurrentValue = temporaryCurrentValue;
                            }

                            actualNode = j - 1;
                            if (j == lineStartNodes.get(0)) {
                                alreadyFinished = true;
                                break;
                            }
                        }
                    }
                }
            } while (!alreadyFinished);
        }
        return maxCurrentValue;
    }

    /**
     * Perform a forward sweep to calculate and adjust voltages based on current values.
     */
    private void calculateVoltages(int[][] matrix, double[][] impedanceMatrix, List<Integer> lineStartNodes, List<Integer> lineEndNodes, List<Integer> lineSideNodes, HashMap<Integer, Double> currentValues, HashMap<Integer, Double> voltageValues) {
        HashMap<Integer, Double> voltageDropValues = initializeVoltageValues(matrix.length - 1, 0);
        for (int k = 0; k < lineStartNodes.size(); k++) {
            int startNode = lineStartNodes.get(k);
            int actualNode = startNode;
            double voltageDrop = 0.0;
            do {
                if (matrix[actualNode - 1][actualNode] == 1 || matrix[lineSideNodes.get(k)][actualNode] == 1) { // If there's a connection
                    int tempoStartNode = matrix[actualNode - 1][actualNode] == 1 ? actualNode - 1 : lineSideNodes.get(k);
                    double current = currentValues.getOrDefault(actualNode, 0.0);
                    voltageDrop += calculateVoltageDrop(current, matrix, impedanceMatrix, tempoStartNode, actualNode); // Calculate voltage drop in the line

                    double newVoltage = (startNode != actualNode) ? voltageValues.get(actualNode) - voltageDrop 
                                                                 : voltageValues.get(actualNode) - voltageDrop - voltageDropValues.get(lineSideNodes.get(k));

                    voltageDropValues.put(actualNode, voltageDrop);
                    voltageValues.put(actualNode, newVoltage);
                }
                actualNode++;
            } while (actualNode < lineEndNodes.get(k));
        }
    }

    /**
     * Calculate the voltage drop across a line segment given the current and impedance.
     */
    private double calculateVoltageDrop(double current, int[][] matrix, double[][] impedanceMatrix, int startNode, int endNode) {
        double impedance = impedanceMatrix[startNode][endNode]; // Get impedance value for the line segment
        return current * impedance;
    }

    /**
     * Write the current values for each node to an Excel (CSV) file.
     */
    private void writeCurrentValues2Excel(HashMap<Integer, Double> currentValues) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH + "currentValues" + this.congestionManagingAgent.getLocalName() + ".csv"))) {
            int tradingPeriod = this.congestionManagingAgent.getInternalDataModel().getTradingPeriod();

            // Store current values in the internal data model
            if (!afterMatching) {
                this.congestionManagingAgent.getInternalDataModel().getCurrentValuesMap().put(tradingPeriod, currentValues);
            } else {
                this.congestionManagingAgent.getInternalDataModel().addCurrentValueAfterMatching(currentValues);
            }

            HashMap<Integer, HashMap<Integer, Double>> currentValuesAfterMatching = this.congestionManagingAgent.getInternalDataModel().getCurrentValuesAfterMatching();

            // Write header
            writer.write("TradingPeriod;AgentName;CurrentValue;AfterMatching");
            writer.write("\n");

            // Write current values for each agent
            for (HashMap.Entry<Integer, HashMap<Integer, Double>> currentValuesTotalMap : this.congestionManagingAgent.getInternalDataModel().getCurrentValuesMap().entrySet()) {
                Integer temporaryPeriod = currentValuesTotalMap.getKey();
                for (HashMap.Entry<Integer, Double> currentValuesMap : currentValuesTotalMap.getValue().entrySet()) {
                    Integer agentName = currentValuesMap.getKey();
                    Double current = currentValuesMap.getValue();
                    writer.write(temporaryPeriod + ";");
                    writer.write(agentName + ";");
                    writer.write(String.format(Locale.GERMAN, "%.2f", current) + ";");
                    if (currentValuesAfterMatching.containsKey(temporaryPeriod)) {
                        HashMap<Integer, Double> currentValuesAfterMatchingPerPeriod = currentValuesAfterMatching.get(temporaryPeriod);
                        if (currentValuesAfterMatchingPerPeriod.containsKey(agentName)) {
                            writer.write(String.format(Locale.GERMAN, "%.2f", currentValuesAfterMatchingPerPeriod.get(agentName)) + ";");
                        }
                    } else {
                        writer.write(String.format(Locale.GERMAN, "%.2f", current) + ";");
                    }
                    writer.write("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
