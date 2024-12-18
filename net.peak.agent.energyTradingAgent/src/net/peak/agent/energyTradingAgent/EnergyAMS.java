package net.peak.agent.energyTradingAgent;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import net.peak.agent.energyTradingAgent.behaviour.AMSMessageReceiveBehaviour;

/**
 * The EnergyAMS class represents an agent in the energy management system.
 * It handles communication with the AMS and manages a list of open grid nodes for both EDA and CMA.
 */
public class EnergyAMS extends Agent {
    
    /**
     * Setup method is called when the agent is started.
     * It registers the agent with the Directory Facilitator (DF) and adds the AMSMessageReceiveBehaviour.
     */
    protected void setup() {
        AMSMessageReceiveBehaviour amse = new AMSMessageReceiveBehaviour(this);
        this.addBehaviour(amse);
    }
    
    private List<AID> openGridNodesEDA;

    /**
     * Retrieves the list of open grid nodes for EDA. If the list is null, it initializes a new list.
     * @return The list of open grid nodes for EDA.
     */
    public List<AID> getOpenGridNodesEDA() {
        if (openGridNodesEDA == null) {
            openGridNodesEDA = new ArrayList<AID>();
        }
        return openGridNodesEDA;
    }

    /**
     * Sets the list of open grid nodes for EDA.
     * @param openGridNodes The list of open grid nodes to be set.
     */
    public void setOpenGridNodesEDA(List<AID> openGridNodes) {
        this.openGridNodesEDA = openGridNodes;
    }
    
    /**
     * Adds a new AID to the list of open grid nodes for EDA.
     * @param aid The AID to be added.
     */
    public void addOpenGridNodesEDA(AID aid) {
        this.openGridNodesEDA = this.getOpenGridNodesEDA();
        this.openGridNodesEDA.add(aid);
    }
    
    private List<AID> openGridNodesCMA;

    /**
     * Retrieves the list of open grid nodes for CMA. If the list is null, it initializes a new list.
     * @return The list of open grid nodes for CMA.
     */
    public List<AID> getOpenGridNodesCMA() {
        if (openGridNodesCMA == null) {
            openGridNodesCMA = new ArrayList<AID>();
        }
        return openGridNodesCMA;
    }

    /**
     * Sets the list of open grid nodes for CMA.
     * @param openGridNodes The list of open grid nodes to be set.
     */
    public void setOpenGridNodesCMA(List<AID> openGridNodes) {
        this.openGridNodesCMA = openGridNodes;
    }
    
    /**
     * Adds a new AID to the list of open grid nodes for CMA.
     * @param aid The AID to be added.
     */
    public void addOpenGridNodesCMA(AID aid) {
        this.openGridNodesCMA = this.getOpenGridNodesCMA();
        this.openGridNodesCMA.add(aid);
    }
}
