package net.peak.agent.ConnetionCRIEPILab;

import jade.core.behaviours.OneShotBehaviour;
import net.peak.agent.energyTradingAgent.EnergyTradingAgent;

public class GetMeasurementBehaviour extends OneShotBehaviour {

    private EnergyTradingAgent energyTradingAgent;

    /**
     * Constructor to initialize GetMeasurementBehaviour with the energy trading agent.
     *
     * @param energyTradingAgent The energy trading agent responsible for handling measurement data.
     */
    public GetMeasurementBehaviour(EnergyTradingAgent energyTradingAgent) {
        this.energyTradingAgent = energyTradingAgent;
    }

    /**
     * The action method that triggers the collection of measurement data from the CRIEPI lab.
     * It collects the measurements and stores them in the internal data model of the agent.
     */
    @Override
    public void action() {
        // Create an instance to receive measurement data
        ReceiveMeasureDataOfCRIEPILab rmd = new ReceiveMeasureDataOfCRIEPILab(this.energyTradingAgent);
        
        // Collect the measurements
        MeasurementDataModel measurementDataModel = rmd.collectMeasurements();
        
        // Add the collected measurements to the agent's internal data model
        this.energyTradingAgent.getInternalDataModel().addPeriodMeasurementInIntervall(measurementDataModel);
    }
}
