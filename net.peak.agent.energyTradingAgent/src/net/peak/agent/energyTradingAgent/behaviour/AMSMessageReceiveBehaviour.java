package net.peak.agent.energyTradingAgent.behaviour;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import net.peak.agent.energyTradingAgent.EnergyAMS;
import jade.core.behaviours.ParallelBehaviour;

public class AMSMessageReceiveBehaviour extends ParallelBehaviour {
    
    EnergyAMS energyAMS;
    
    public AMSMessageReceiveBehaviour(EnergyAMS energyAMS) {
        this.energyAMS = energyAMS;
        // Hier wird der TickerBehaviour anstelle des CyclicBehaviour verwendet
        addSubBehaviour(new HandleMessagesBehaviour(energyAMS, 1000)); // Intervall in Millisekunden, z.B. 1000 ms = 1 Sekunde
    }
    
    private class HandleMessagesBehaviour extends TickerBehaviour {
        
        public HandleMessagesBehaviour(EnergyAMS energyAMS, long period) {
            super(energyAMS, period);
        }
        
        @Override
        protected void onTick() {
            ACLMessage msg = energyAMS.receive();
            List<AID> openGridNodesEDA = energyAMS.getOpenGridNodesEDA();
            List<AID> openGridNodesCMA = energyAMS.getOpenGridNodesCMA();
            
            if (msg != null) {
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                int aidMsg = Integer.parseInt(msg.getSender().getLocalName());
                if (aidMsg / 1000 >= 1) {
                    if (!openGridNodesEDA.contains(msg.getSender())) {
                        openGridNodesEDA.add(msg.getSender());
                    }
                    System.out.println("Agent: " + msg.getSender() + " gets: " + openGridNodesEDA.size() + " Nodes");
                    try {
                        reply.setContentObject((Serializable) openGridNodesEDA);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (!openGridNodesCMA.contains(msg.getSender())) {
                        openGridNodesCMA.add(msg.getSender());
                    }
                    System.out.println("Agent: " + msg.getSender() + " gets: " + openGridNodesCMA.size() + " Nodes");
                    try {
                        reply.setContentObject((Serializable) openGridNodesCMA);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                energyAMS.send(reply);
            } else {
                block();
            }
        }
    }
}
