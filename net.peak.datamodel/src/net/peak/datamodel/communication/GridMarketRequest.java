package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: GridMarketRequest
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class GridMarketRequest extends GridMarketInteraction{ 

   /**
   * The maximal purchase price of the requested flexibility determined by the grid operator
* Protege name: maximumPurchasePrice
   */
   private float maximumPurchasePrice;
   public void setMaximumPurchasePrice(float value) { 
    this.maximumPurchasePrice=value;
   }
   public float getMaximumPurchasePrice() {
     return this.maximumPurchasePrice;
   }

}
