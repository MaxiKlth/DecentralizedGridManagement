package net.peak.agent.flexibilityTradingAgent.behaviour;

import java.util.HashMap;
import java.util.List;

import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import net.peak.agent.flexibilityTradingAgent.CongestionManagingAgent;
import net.peak.datamodel.communication.FlexibilityTransaction;
import net.peak.datamodel.communication.PowerFlow;
import net.peak.datamodel.communication.PutFlexibilityTransaction;
import net.peak.datamodel.configuration.PeakConfiguration;

public class CalculateFlexibilityAvailable extends OneShotBehaviour {

    private static final long serialVersionUID = -7273734471879622599L;
    private boolean debug = true;  // Debug mode flag for additional output and testing logic
    private CongestionManagingAgent congestionManagingAgent;  // Reference to the Congestion Managing Agent

    // Constructor to initialize the agent
    public CalculateFlexibilityAvailable(CongestionManagingAgent cma) {
        this.congestionManagingAgent = cma;
    }

    @Override
    public void action() {
        // Reset iteration count
        int iteration = this.congestionManagingAgent.getInternalDataModel().getIteration();
        this.congestionManagingAgent.getInternalDataModel().setIteration(0);
        
        // Calculate available flexibility and update internal data model
        FlexibilityTransaction ownFlexibilityTransaction = calculateFlexibilityAvailable();
        this.congestionManagingAgent.getInternalDataModel().setFlexibilityTransaction(ownFlexibilityTransaction);
        this.congestionManagingAgent.getInternalDataModel().addTemporaryListFlexibilityTransaction(this.congestionManagingAgent.getAID(), ownFlexibilityTransaction);
        
        // Prepare action message for sending the flexibility transaction
        PutFlexibilityTransaction pft = new PutFlexibilityTransaction();
        pft.setFlexibilityTransaction(ownFlexibilityTransaction);
        Action action = new Action();
        action.setActor(this.congestionManagingAgent.getAID());
        action.setAction(pft);

        // Set initial values for ADMM algorithm (x and z)
        this.congestionManagingAgent.getInternalDataModel().setX(ownFlexibilityTransaction.getPowerFlow().getFloatValue());
        this.congestionManagingAgent.getInternalDataModel().setZ(ownFlexibilityTransaction.getPowerFlow().getFloatValue());

        // Send flexibility transaction to all agents in the phone book
        List<AID> phoneBook = this.congestionManagingAgent.getInternalDataModel().getPhoneBook();
        for (AID agent : phoneBook) {
            this.congestionManagingAgent.sendACLMessage(action, agent);
        }
    }

    /**
     * Calculates the available flexibility based on current conditions and configuration.
     * 
     * @return A FlexibilityTransaction object with calculated values.
     */
    private FlexibilityTransaction calculateFlexibilityAvailable() {
        // Retrieve initial flexibility transaction
        FlexibilityTransaction oft = this.congestionManagingAgent.getInternalDataModel()
            .getTemporaryListFlexibilityTransaction()
            .get(congestionManagingAgent.getAID());

        oft.setOfferingAgent(this.congestionManagingAgent.getAID());

        // Retrieve current values and configurations
        HashMap<Integer, Double> currentValues = this.congestionManagingAgent.getInternalDataModel()
            .getCurrentValuesMap()
            .get(this.congestionManagingAgent.getInternalDataModel().getTradingPeriod());

        int maxNodes = this.congestionManagingAgent.getInternalDataModel().getMaxNodes();
        PeakConfiguration peakConfig = new PeakConfiguration();
        double timeDivider = peakConfig.timeDivider;
        int voltage = this.congestionManagingAgent.getInternalDataModel().getVoltage();
        int aidInt = Integer.parseInt(this.congestionManagingAgent.getLocalName());

        // Calculate own energy value and corresponding current
        double ownEnergyValue = this.congestionManagingAgent.getInternalDataModel()
            .getPlannedEnergyBalanceList()
            .get(this.congestionManagingAgent.getInternalDataModel().getTradingPeriod())
            .get(aidInt);
        double ownCurrent = ownEnergyValue / timeDivider / voltage;

        // Debug mode: Calculate and set power flow and transaction price
        if (debug) {
            PowerFlow powerFlow = new PowerFlow();
            if (currentValues != null && currentValues.containsKey(aidInt)) {
                double tempoCurrent = Math.random() * 0.1 + oft.getInitialEnergyAmountAsked() / maxNodes;
                powerFlow.setFloatValue((float) tempoCurrent);
            } else {
                powerFlow.setFloatValue((float) (Math.random() * 0.001 + oft.getInitialEnergyAmountAsked() / maxNodes));
            }
            oft.setPowerFlow(powerFlow);
            oft.setTransactionPrice((float) (Math.random() * 0.1));
        }

        // Set initial values for offered energy and price
        oft.setInitialEnergyAmountOffered(oft.getPowerFlow().getFloatValue());
        oft.setInitialTransactionPriceOffered(oft.getTransactionPrice());

        return oft;
    }
}
