package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PeakMemberRegistration
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PeakMemberRegistration extends PeakMemberInteraction{ 

   /**
* Protege name: peakMember
   */
   private PeakMember peakMember;
   public void setPeakMember(PeakMember value) { 
    this.peakMember=value;
   }
   public PeakMember getPeakMember() {
     return this.peakMember;
   }

}
