package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Senden eines Setpoints an einen Aktor im Netzengpassfall
* Protege name: PutActuatorSetpoint
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PutActuatorSetpoint extends GridToProsumer{ 

   /**
* Protege name: actuatorSetpoint
   */
   private ActuatorSetpoint actuatorSetpoint;
   public void setActuatorSetpoint(ActuatorSetpoint value) { 
    this.actuatorSetpoint=value;
   }
   public ActuatorSetpoint getActuatorSetpoint() {
     return this.actuatorSetpoint;
   }

}
