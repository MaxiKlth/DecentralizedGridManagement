package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PutResidualProcurementResult
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PutResidualProcurementResult extends PutTradingResult{ 

   /**
* Protege name: residualResult
   */
   private ResidualResult residualResult;
   public void setResidualResult(ResidualResult value) { 
    this.residualResult=value;
   }
   public ResidualResult getResidualResult() {
     return this.residualResult;
   }

}
