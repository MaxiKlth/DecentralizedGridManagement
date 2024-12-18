package net.peak.datamodel.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: ActuatorSetpoint
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class ActuatorSetpoint implements Concept {

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
* Protege name: setpoint
   */
   private PowerFlow setpoint;
   public void setSetpoint(PowerFlow value) { 
    this.setpoint=value;
   }
   public PowerFlow getSetpoint() {
     return this.setpoint;
   }

}
