package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: ProsumerMarketTransaction
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class ProsumerMarketTransaction extends ProsumerMarketInteraction{ 

   /**
* Protege name: energyLabel
   */
   private EnergyLabel energyLabel;
   public void setEnergyLabel(EnergyLabel value) { 
    this.energyLabel=value;
   }
   public EnergyLabel getEnergyLabel() {
     return this.energyLabel;
   }

}
