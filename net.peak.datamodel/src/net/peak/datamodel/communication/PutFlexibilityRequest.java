package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PutFlexibilityRequest
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PutFlexibilityRequest extends PutTradingTransaction{ 

   /**
* Protege name: flexibilityRequest
   */
   private FlexibilityRequest flexibilityRequest;
   public void setFlexibilityRequest(FlexibilityRequest value) { 
    this.flexibilityRequest=value;
   }
   public FlexibilityRequest getFlexibilityRequest() {
     return this.flexibilityRequest;
   }

}
