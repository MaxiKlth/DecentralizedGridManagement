package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: GridMarketResult
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class GridMarketResult extends GridMarketInteraction{ 

   /**
* Protege name: purchasePrice
   */
   private float purchasePrice;
   public void setPurchasePrice(float value) { 
    this.purchasePrice=value;
   }
   public float getPurchasePrice() {
     return this.purchasePrice;
   }

}
