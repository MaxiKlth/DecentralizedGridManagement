package net.peak.datamodel.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PutEnergyTransaction
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PutEnergyTransaction extends PutTradingTransaction{ 

   /**
* Protege name: energyTransaction
   */
	private EnergyTransaction energyTransaction = new EnergyTransaction();

public EnergyTransaction getEnergyTransaction() {
	return energyTransaction;
}

public void setEnergyTransaction(EnergyTransaction energyTransaction) {
	this.energyTransaction = energyTransaction;
}
	   

}
