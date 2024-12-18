package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: FlexibilityResult
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class FlexibilityResult extends MarketProsumerResult{ 

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
