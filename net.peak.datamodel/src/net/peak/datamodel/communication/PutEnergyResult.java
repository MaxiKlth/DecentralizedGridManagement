package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PutEnergyResult
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PutEnergyResult extends PutTradingResult{ 

   /**
* Protege name: energyResult
   */
   private EnergyResult energyResult;
   public void setEnergyResult(EnergyResult value) { 
    this.energyResult=value;
   }
   public EnergyResult getEnergyResult() {
     return this.energyResult;
   }

}
