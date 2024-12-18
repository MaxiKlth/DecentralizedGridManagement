package net.peak.datamodel.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PowerFactorSetpointTriPhase
* @author ontology bean generator
* @version 2022/10/18, 13:16:58
*/
public class PowerFactorSetpointTriPhase implements Concept {

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
* Protege name: value
   */
   private float value;
   public void setValue(float value) { 
    this.value=value;
   }
   public float getValue() {
     return this.value;
   }

}
