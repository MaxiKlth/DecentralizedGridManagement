package net.peak.datamodel.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: ActuatorFeedback
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class ActuatorFeedback implements Concept {

   /**
* Protege name: maxActivePowerFeedIn
   */
   private float maxActivePowerFeedIn;
   public void setMaxActivePowerFeedIn(float value) { 
    this.maxActivePowerFeedIn=value;
   }
   public float getMaxActivePowerFeedIn() {
     return this.maxActivePowerFeedIn;
   }

   /**
* Protege name: timeStamp
   */
   private LongValue timeStamp;
   public void setTimeStamp(LongValue value) { 
    this.timeStamp=value;
   }
   public LongValue getTimeStamp() {
     return this.timeStamp;
   }

   /**
* Protege name: maxActivePowerDemand
   */
   private float maxActivePowerDemand;
   public void setMaxActivePowerDemand(float value) { 
    this.maxActivePowerDemand=value;
   }
   public float getMaxActivePowerDemand() {
     return this.maxActivePowerDemand;
   }

   /**
* Protege name: reportedActivePower
   */
   private float reportedActivePower;
   public void setReportedActivePower(float value) { 
    this.reportedActivePower=value;
   }
   public float getReportedActivePower() {
     return this.reportedActivePower;
   }

   /**
* Protege name: reportedSetpoint
   */
   private float reportedSetpoint;
   public void setReportedSetpoint(float value) { 
    this.reportedSetpoint=value;
   }
   public float getReportedSetpoint() {
     return this.reportedSetpoint;
   }

}
