package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Auschreibungstabelle wird bepreist und an den Markt gesendet
* Protege name: PutFlexibilityTableRequest
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PutFlexibilityTableRequest extends GridToMarket{ 

   /**
* Protege name: gridMarketRequest
   */
   private GridMarketInteraction gridMarketRequest;
   public void setGridMarketRequest(GridMarketInteraction value) { 
    this.gridMarketRequest=value;
   }
   public GridMarketInteraction getGridMarketRequest() {
     return this.gridMarketRequest;
   }

}
