package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Senden von
* Protege name: PutInverterSetpoints
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PutInverterSetpoints extends PlattformToSgl{ 

   /**
* Protege name: inverterSetpoints
   */
   private InverterSetpoints inverterSetpoints;
   public void setInverterSetpoints(InverterSetpoints value) { 
    this.inverterSetpoints=value;
   }
   public InverterSetpoints getInverterSetpoints() {
     return this.inverterSetpoints;
   }

}
