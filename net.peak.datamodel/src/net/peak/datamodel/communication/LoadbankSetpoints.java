package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: LoadbankSetpoints
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class LoadbankSetpoints extends PlattformSglInteraction{ 

   /**
* Protege name: setpointP2
   */
   private ActivePowerSetpointSinglePhaseAcl setpointP2;
   public void setSetpointP2(ActivePowerSetpointSinglePhaseAcl value) { 
    this.setpointP2=value;
   }
   public ActivePowerSetpointSinglePhaseAcl getSetpointP2() {
     return this.setpointP2;
   }

   /**
* Protege name: setpointP3
   */
   private ActivePowerSetpointSinglePhaseAcl setpointP3;
   public void setSetpointP3(ActivePowerSetpointSinglePhaseAcl value) { 
    this.setpointP3=value;
   }
   public ActivePowerSetpointSinglePhaseAcl getSetpointP3() {
     return this.setpointP3;
   }

   /**
* Protege name: setpointP1
   */
   private ActivePowerSetpointSinglePhaseAcl setpointP1;
   public void setSetpointP1(ActivePowerSetpointSinglePhaseAcl value) { 
    this.setpointP1=value;
   }
   public ActivePowerSetpointSinglePhaseAcl getSetpointP1() {
     return this.setpointP1;
   }

   /**
* Protege name: uuid
   */
   private float uuid;
   public void setUuid(float value) { 
    this.uuid=value;
   }
   public float getUuid() {
     return this.uuid;
   }

}
