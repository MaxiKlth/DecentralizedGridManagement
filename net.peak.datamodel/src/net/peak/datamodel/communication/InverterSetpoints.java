package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: InverterSetpoints
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class InverterSetpoints extends PlattformSglInteraction{ 

   /**
* Protege name: setpointPf123
   */
   private PowerFactorSetpointTriPhaseAcl setpointPf123;
   public void setSetpointPf123(PowerFactorSetpointTriPhaseAcl value) { 
    this.setpointPf123=value;
   }
   public PowerFactorSetpointTriPhaseAcl getSetpointPf123() {
     return this.setpointPf123;
   }

   /**
* Protege name: setpointS123
   */
   private ApparentPowerSetpointTriPhaseAcl setpointS123;
   public void setSetpointS123(ApparentPowerSetpointTriPhaseAcl value) { 
    this.setpointS123=value;
   }
   public ApparentPowerSetpointTriPhaseAcl getSetpointS123() {
     return this.setpointS123;
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
