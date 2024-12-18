package net.peak.datamodel.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: GridMarketInteraction
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class GridMarketInteraction implements Concept {

   /**
* Protege name: timeSlotStart
   */
   private LongValue timeSlotStart;
   public void setTimeSlotStart(LongValue value) { 
    this.timeSlotStart=value;
   }
   public LongValue getTimeSlotStart() {
     return this.timeSlotStart;
   }

   /**
* Protege name: powerAdjustment
   */
   private List powerAdjustment = new ArrayList();
   public void addPowerAdjustment(PowerAdjustment elem) { 
     List oldList = this.powerAdjustment;
     powerAdjustment.add(elem);
   }
   public boolean removePowerAdjustment(PowerAdjustment elem) {
     List oldList = this.powerAdjustment;
     boolean result = powerAdjustment.remove(elem);
     return result;
   }
   public void clearAllPowerAdjustment() {
     List oldList = this.powerAdjustment;
     powerAdjustment.clear();
   }
   public Iterator getAllPowerAdjustment() {return powerAdjustment.iterator(); }
   public List getPowerAdjustment() {return powerAdjustment; }
   public void setPowerAdjustment(List l) {powerAdjustment = l; }

}
