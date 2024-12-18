package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: EnergyTransaction
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class EnergyTransaction extends ProsumerMarketTransaction{ 

   /**
* Protege name: energyAmount
   */
   private float energyAmountFloat;
   private double communicationTime;
   

public double getCommunicationTime() {
	return communicationTime;
}

public void setCommunicationTime(double communicationTime) {
	this.communicationTime = communicationTime;
}

public float getEnergyAmountFloat() {
	return energyAmountFloat;
}

public void setEnergyAmountFloat(float energyAmountFloat) {
	this.energyAmountFloat = energyAmountFloat;
}

}
