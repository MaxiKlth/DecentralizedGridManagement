package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PutLvrSetpoints
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PutLvrSetpoints extends PlattformToSgl{ 

   /**
* Protege name: lvrSetpoints
   */
   private LvrSetpoints lvrSetpoints;
   public void setLvrSetpoints(LvrSetpoints value) { 
    this.lvrSetpoints=value;
   }
   public LvrSetpoints getLvrSetpoints() {
     return this.lvrSetpoints;
   }

}
