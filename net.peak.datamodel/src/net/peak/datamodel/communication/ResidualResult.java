package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: ResidualResult
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class ResidualResult extends MarketProsumerResult{ 

   /**
* Protege name: deliveredEnergy
   */
   private EnergyAmount deliveredEnergy;
   public void setDeliveredEnergy(EnergyAmount value) { 
    this.deliveredEnergy=value;
   }
   public EnergyAmount getDeliveredEnergy() {
     return this.deliveredEnergy;
   }

}
