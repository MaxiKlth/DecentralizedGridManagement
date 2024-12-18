package net.peak.datamodel.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: FlexibilityRequest
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class FlexibilityRequest implements Concept {

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
* Protege name: powerAdjustmentIndividual
   */
   private PowerAdjustment powerAdjustmentIndividual;
   public void setPowerAdjustmentIndividual(PowerAdjustment value) { 
    this.powerAdjustmentIndividual=value;
   }
   public PowerAdjustment getPowerAdjustmentIndividual() {
     return this.powerAdjustmentIndividual;
   }

}
