package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Additional Variable to EnergyTransaction
* Protege name: FlexibilityTransaction
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class FlexibilityTransaction extends ProsumerMarketTransaction{ 

   /**
* Protege name: powerFlow
   */
   private PowerFlow powerFlow;
   private boolean calculationComplete = false;
   
   public boolean getCalculationComplete() {
	return calculationComplete;
}
public void setCalculationComplete(boolean calculationComplete) {
	this.calculationComplete = calculationComplete;
}
public void setPowerFlow(PowerFlow value) { 
    this.powerFlow=value;
   }
   public PowerFlow getPowerFlow() {	   
     return this.powerFlow;
   }
   
   private float energyAmountFloat;

public float getEnergyAmountFloat() {
	return energyAmountFloat;
}

public void setEnergyAmountFloat(float energyAmountFloat) {
	this.energyAmountFloat = energyAmountFloat;
}

}
