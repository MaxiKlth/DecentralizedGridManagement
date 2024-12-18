package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PutFlexibilityTransaction
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PutFlexibilityTransaction extends PutTradingTransaction{ 

   /**
* Protege name: flexibilityTransaction
   */
   private FlexibilityTransaction flexibilityTransaction;
   public void setFlexibilityTransaction(FlexibilityTransaction value) { 
    this.flexibilityTransaction=value;
   }
   public FlexibilityTransaction getFlexibilityTransaction() {
     return this.flexibilityTransaction;
   }

}
