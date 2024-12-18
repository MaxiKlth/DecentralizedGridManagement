package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PutLoadbankSetpoints
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PutLoadbankSetpoints extends PlattformToSgl{ 

   /**
* Protege name: loadbankSetpoints
   */
   private LoadbankSetpoints loadbankSetpoints;
   public void setLoadbankSetpoints(LoadbankSetpoints value) { 
    this.loadbankSetpoints=value;
   }
   public LoadbankSetpoints getLoadbankSetpoints() {
     return this.loadbankSetpoints;
   }

}
