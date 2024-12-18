package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PutFlexibilityResult
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PutFlexibilityResult extends PutTradingResult{ 

   /**
* Protege name: flexibilityResult
   */
   private FlexibilityResult flexibilityResult;
   public void setFlexibilityResult(FlexibilityResult value) { 
    this.flexibilityResult=value;
   }
   public FlexibilityResult getFlexibilityResult() {
     return this.flexibilityResult;
   }

}
