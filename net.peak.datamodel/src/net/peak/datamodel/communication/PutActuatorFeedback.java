package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Nach dem Empfangen eines Setpoints versucht der Aktor diesen umzusetzten und sendet anschliessend ein Feedback zurueck an das Netzautomatisierungssystem
* Protege name: PutActuatorFeedback
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PutActuatorFeedback extends GridToProsumer{ 

   /**
* Protege name: actuatorFeedback
   */
   private ActuatorFeedback actuatorFeedback;
   public void setActuatorFeedback(ActuatorFeedback value) { 
    this.actuatorFeedback=value;
   }
   public ActuatorFeedback getActuatorFeedback() {
     return this.actuatorFeedback;
   }

}
