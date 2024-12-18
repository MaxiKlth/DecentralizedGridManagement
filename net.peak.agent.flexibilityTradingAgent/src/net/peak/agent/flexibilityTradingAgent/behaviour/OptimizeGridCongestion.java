package net.peak.agent.flexibilityTradingAgent.behaviour;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.enflexit.jade.behaviour.AbstractTimingBehaviour;
import de.enflexit.jade.behaviour.AbstractTimingBehaviour.ExecutionTiming;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import net.peak.agent.flexibilityTradingAgent.CongestionManagingAgent;
import net.peak.agent.flexibilityTradingAgent.InternalDataModel;
import net.peak.datamodel.communication.EnergyResult;
import net.peak.datamodel.communication.EnergyTransaction;
import net.peak.datamodel.communication.FlexibilityTransaction;
import net.peak.datamodel.communication.PowerFlow;
import net.peak.datamodel.communication.PutAllHardwareSetpoints;
import net.peak.datamodel.communication.PutEnergyResult;
import net.peak.datamodel.communication.PutFlexibilityTransaction;
import net.peak.datamodel.configuration.PeakConfiguration;

public class OptimizeGridCongestion extends AbstractTimingBehaviour {

	// Reference to the managing agent
	private CongestionManagingAgent congestionManagingAgent;
	
	// Parameters for the optimization process
	private double lambda = 0.005; // Penalty term
	private double x; // Power value for the electrolysis unit/agent
	private double z; // Auxiliary variable for optimization
	private double minPower; // Minimum power constraint
	private double maxPower; // Maximum power constraint
	private boolean stateProduction = true; // Flag indicating production state
	private double sumFlexibility = 0; // Sum of flexibility from all agents
	private double demandFlexibility; // Total flexibility demand
	private double flexibilityPricePerKWh; // Price per kWh of flexibility
	private double penaltyFactor = 0; // Penalty factor for constraint violation
	private boolean schedulingComplete = false; // Flag indicating if scheduling is complete
	private double epsilonProduction = 1; // Tolerance for production deviation
	private double epsilon = 0.0005; // Tolerance for optimization
	private int iteration = 0; // Current iteration count
	private static final int MAX_ITERATIONS = 25; // Maximum number of iterations
	private static final double RHO = 0.01; // Regularization parameter
	private double increment = 0.0001; // Increment step for optimization
	private double decentRate; // Decentralized rate for optimization
	private long waitTimeBase = 5000; // Base wait time between iterations
	private int waitTimeMinFactor = 1; // Minimum wait time factor
	private int waitTimeMaxFactor = 10; // Maximum wait time factor

	// Constructor for the optimization class
	public OptimizeGridCongestion(CongestionManagingAgent congestionManagingAgent, Duration interval, Duration offset,
			ExecutionTiming executionTiming) {
		super(congestionManagingAgent, calculateStartInstant(congestionManagingAgent, interval, offset), offset, executionTiming);
		this.congestionManagingAgent = congestionManagingAgent;
	}

	@Override
	public void performAction() {
	
		// Check if calculation for the current period is already done
		if (this.congestionManagingAgent.getInternalDataModel().getPeriodCalculationDone().containsKey(this.congestionManagingAgent.getInternalDataModel().getTradingPeriod())) {
			if (this.congestionManagingAgent.getInternalDataModel().getPeriodCalculationDone().get(this.congestionManagingAgent.getInternalDataModel().getTradingPeriod()) == false) {	
			
				// Initialize timing on first iteration
				if (this.iteration == 0) {
					long startTime = System.currentTimeMillis();
					this.congestionManagingAgent.getInternalDataModel().setStartTimeCalculation(startTime);
				}
				
				// Update optimization parameters
				decentRate = this.congestionManagingAgent.getInternalDataModel().getDecentRate();
				int tradingPeriod = this.congestionManagingAgent.getInternalDataModel().getTradingPeriod();
				lambda = this.congestionManagingAgent.getInternalDataModel().getPreviousLambda();
				iteration = this.congestionManagingAgent.getInternalDataModel().getIteration();
				
				// Retrieve flexibility transactions
				HashMap<Integer, HashMap<AID, FlexibilityTransaction>> overAllList = this.congestionManagingAgent.getInternalDataModel().getOverallFlexibilityTransaction().get(tradingPeriod);
				HashMap<AID, FlexibilityTransaction> listFlexibilityTransactions = new HashMap<>();
				
				// Handle first iteration case
				if (overAllList != null) {
					if (iteration == 0) {
						listFlexibilityTransactions = overAllList.get(iteration);
						x = this.congestionManagingAgent.getInternalDataModel().getX();
						z = this.congestionManagingAgent.getInternalDataModel().getZ();
					} else {
						listFlexibilityTransactions = overAllList.get(iteration - 1);
						x = this.congestionManagingAgent.getInternalDataModel().getPreviousX();
						z = this.congestionManagingAgent.getInternalDataModel().getPreviousZ();
					}
				}
				
				FlexibilityTransaction ownFlexibilityTransaction = new FlexibilityTransaction();
				
				// Handle the current agent's flexibility transaction
				if (listFlexibilityTransactions != null && listFlexibilityTransactions.containsKey(congestionManagingAgent.getAID())) {
					ownFlexibilityTransaction = listFlexibilityTransactions.get(this.congestionManagingAgent.getAID());
					maxPower = ownFlexibilityTransaction.getInitialEnergyAmountOffered();
					
					if (maxPower == 0) {
						System.out.println("Agent: " + this.congestionManagingAgent.getLocalName() + " No flexible power available");
					}
					
					demandFlexibility = ownFlexibilityTransaction.getInitialEnergyAmountAsked(); 
					flexibilityPricePerKWh = ownFlexibilityTransaction.getTransactionPrice(); 
					
					if (flexibilityPricePerKWh == 0) {
						flexibilityPricePerKWh = Math.random() * 0.5 + 0.1;
					}
				}
				
				// Set power constraints and calculate total flexibility
				minPower = 0;
				sumFlexibility = 0;
				
				if (listFlexibilityTransactions != null) {
					for (Map.Entry<AID, FlexibilityTransaction> entry : listFlexibilityTransactions.entrySet()) {
						AID key = entry.getKey();
						FlexibilityTransaction flexVal = entry.getValue();
						if (flexVal != null && !key.equals(this.congestionManagingAgent.getAID())) {
							sumFlexibility += flexVal.getPowerFlow().getFloatValue();
						}
					}
				}
				
				double totalSumFlexibility = sumFlexibility + ownFlexibilityTransaction.getInitialEnergyAmountOffered();
				
				// Perform optimization if demand is less than total flexibility
				if (demandFlexibility < totalSumFlexibility) {
					this.congestionManagingAgent.getInternalDataModel().setPreviousX(x);
					this.congestionManagingAgent.getInternalDataModel().setPreviousZ(z);
					
					x = minimizeLx();
					z = minimizeLz();
					
					DualUpdate(); // Update dual variables
					
					ownFlexibilityTransaction.getPowerFlow().setFloatValue((float) z);
					this.congestionManagingAgent.getInternalDataModel().setFlexibilityTransaction(ownFlexibilityTransaction);
					listFlexibilityTransactions.put(this.congestionManagingAgent.getAID(), ownFlexibilityTransaction);
					this.congestionManagingAgent.getInternalDataModel().addTemporaryListFlexibilityTransaction(this.congestionManagingAgent.getAID(), ownFlexibilityTransaction);
					this.congestionManagingAgent.getInternalDataModel().increaseIteration();
				}
				
				// Check if further iterations are needed
				if (iteration < MAX_ITERATIONS && Math.abs(demandDeviation()) > epsilon) {
					this.congestionManagingAgent.getInternalDataModel().setPreviousX(x);
					this.congestionManagingAgent.getInternalDataModel().setPreviousZ(z);
					this.congestionManagingAgent.getInternalDataModel().setPreviousLambda(lambda);
					
					PutFlexibilityTransaction pft = new PutFlexibilityTransaction();
					ownFlexibilityTransaction.setIteration(iteration);
					pft.setFlexibilityTransaction(ownFlexibilityTransaction);
					Action action = new Action();
					action.setActor(this.congestionManagingAgent.getAID());
					action.setAction(pft);
					
					// Send the updated flexibility transaction to all agents
					List<AID> phoneBook = this.congestionManagingAgent.getInternalDataModel().getPhoneBook();
					for (int i = 0; i < phoneBook.size(); i++) {
						this.congestionManagingAgent.sendACLMessage(action, phoneBook.get(i));
					}
				} else {
					// Finalize the transaction and send the results
					PutFlexibilityTransaction pft = new PutFlexibilityTransaction();
					ownFlexibilityTransaction.setIteration(iteration);
					ownFlexibilityTransaction.setCalculationComplete(true);
					pft.setFlexibilityTransaction(ownFlexibilityTransaction);
					
					Action action = new Action();
					action.setActor(this.congestionManagingAgent.getAID());
					action.setAction(pft);
					
					// Send the final results to all agents
					List<AID> phoneBook = this.congestionManagingAgent.getInternalDataModel().getPhoneBook();
					for (int i = 0; i < phoneBook.size(); i++) {
						this.congestionManagingAgent.sendACLMessage(action, phoneBook.get(i));
					}
					
					 this.congestionManagingAgent.getInternalDataModel().setEndIteration(this.congestionManagingAgent.getInternalDataModel().getIteration());
					 SendCMResults scmr = new SendCMResults(this.congestionManagingAgent);
					 this.congestionManagingAgent.addBehaviour(scmr);
				}
			}
		}
	}

	// Minimizes the function with respect to x
	public double minimizeLx() {
	    double min_x_value = this.congestionManagingAgent.getInternalDataModel().getPreviousX();
	    double min_flexibilityPrice = Double.POSITIVE_INFINITY;

	    if (stateProduction) {
	        double rho_term = RHO * (sumFlexibility + z - demandFlexibility);

	        for (double x_i = minPower; x_i <= maxPower; x_i += this.increment) {
	            double flexibilityPrice = x_i * (flexibilityPricePerKWh + RHO) + rho_term;
	            
	            if (flexibilityPrice < min_flexibilityPrice) {
	                min_flexibilityPrice = flexibilityPrice;
	                min_x_value = x + (x_i - x) * decentRate;
	                if (min_x_value < 0) {
	                    min_x_value = 0;
	                }
	            }
	        }
	    } else {
	        min_x_value = 0.0;
	    }

	    // Adjust state based on minimum power condition
	    if (Math.abs(min_x_value - minPower) < 0.01) {
	        stateProduction = true;
	    }

	    this.congestionManagingAgent.getInternalDataModel().setPreviousDiff2ZeroX(min_flexibilityPrice);
	    this.congestionManagingAgent.getInternalDataModel().setX(min_x_value);
	    return min_x_value;
	}

	// Minimizes the function with respect to z
	public double minimizeLz() {
	    double minDiffToZero = Double.POSITIVE_INFINITY;
	    double minZ = this.congestionManagingAgent.getInternalDataModel().getPreviousZ();
	    
	    InternalDataModel model = this.congestionManagingAgent.getInternalDataModel();
	    int iteration = model.getIteration();
	    int tradingPeriod = model.getTradingPeriod();
	    
	    HashMap<Integer, HashMap<AID, FlexibilityTransaction>> overAllList = model.getOverallFlexibilityTransaction().get(tradingPeriod);
	    if (overAllList != null && iteration > 0) {
			overAllList.get(iteration - 1);
		}
	    
	    if (stateProduction) {
	        for (double z_i = minPower; z_i <= maxPower; z_i += increment) {
	            double dzProduction = Math.abs(sumFlexibility + z_i - demandFlexibility);
	            if (dzProduction < minDiffToZero) {
	                minDiffToZero = dzProduction;
	                minZ = z + Math.signum(z_i - z) * (Math.abs(z_i - z) * decentRate);
	                minZ = Math.max(minZ, 0);
	            }
	        }
	        model.setPreviousDiff2ZeroZ(minDiffToZero);

	    } else {
	        minZ = 0;
	    }
	    
	    model.setZ(minZ);
	    return minZ;
	}
	
	// Updates dual variables (lambda) based on the current state
	public void DualUpdate() {
		x = this.congestionManagingAgent.getInternalDataModel().getPreviousX();
		z = this.congestionManagingAgent.getInternalDataModel().getPreviousZ();

		double demandDeviation = demandDeviation();
		double demandPercentage = Math.abs(demandDeviation / demandFlexibility) * 100;
		if (this.congestionManagingAgent.getLocalName().equals("1")) {
			System.out.println("DemandDeviation: " + demandDeviation + " Period: " + this.congestionManagingAgent.getInternalDataModel().getTradingPeriod() + " Iteration: " + this.iteration);
		}

		penaltyFactor = 0.5;
		lambda = lambda + (penaltyFactor * calculateGradient(this.congestionManagingAgent.getInternalDataModel().getPreviousZ()) / demandPercentage) * (x - z);
	}

	// Calculates the gradient of the flexibility price with respect to lambda
	public double calculateGradient(double x) {
		return flexibilityPricePerKWh + lambda;
	}

	// Checks if the scheduling process is complete
	public boolean schedulingComplete() {
		return Math.abs(demandDeviation()) < epsilonProduction;
	}
	
	// Calculates the deviation from the demand
	public double demandDeviation() {
		z = this.congestionManagingAgent.getInternalDataModel().getZ();
		return z + sumFlexibility - demandFlexibility;
	}
	
	// Calculates the start time for the optimization process
	private static Instant calculateStartInstant(CongestionManagingAgent congestionManagingAgent, Duration interval,
			Duration offset) {
		long simulationTime = congestionManagingAgent.getTimeMillis();
		ZonedDateTime startAt = Instant.ofEpochMilli(simulationTime).atZone(ZoneId.systemDefault());
		PeakConfiguration peakConfig = new PeakConfiguration();
		
		if (interval == null) {
			interval = Duration.ofMillis(peakConfig.getTradingIntervallInSeconds());
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

	// Getters and setters for wait time parameters
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
