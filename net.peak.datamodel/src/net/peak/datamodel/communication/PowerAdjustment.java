package net.peak.datamodel.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PowerAdjustment
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PowerAdjustment implements Concept {

   /**
* Protege name: nodeID
   */
   private String nodeID;
   public void setNodeID(String value) { 
    this.nodeID=value;
   }
   public String getNodeID() {
     return this.nodeID;
   }

   /**
* Protege name: adjustment
   */
   private PowerFlow adjustment;
   public void setAdjustment(PowerFlow value) { 
    this.adjustment=value;
   }
   public PowerFlow getAdjustment() {
     return this.adjustment;
   }

}
