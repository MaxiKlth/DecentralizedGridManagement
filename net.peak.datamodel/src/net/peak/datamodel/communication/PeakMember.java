package net.peak.datamodel.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PeakMember
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PeakMember implements Concept {

   /**
* Protege name: location
   */
   private Location location;
   public void setLocation(Location value) { 
    this.location=value;
   }
   public Location getLocation() {
     return this.location;
   }

   /**
* Protege name: peakAgentID
   */
   private AID peakAgentID;
   public void setPeakAgentID(AID value) { 
    this.peakAgentID=value;
   }
   public AID getPeakAgentID() {
     return this.peakAgentID;
   }

   /**
* Protege name: peakMemberID
   */
   private int peakMemberID;
   public void setPeakMemberID(int value) { 
    this.peakMemberID=value;
   }
   public int getPeakMemberID() {
     return this.peakMemberID;
   }

   /**
* Protege name: nodeID
   */
   private String nodeID;
   public void setNodeID(String value) { 
    this.nodeID=value;
   }
   public String getNodeID() {
     return this.nodeID;
   }

   /**
* Protege name: peakUserID
   */
   private String peakUserID;
   public void setPeakUserID(String value) { 
    this.peakUserID=value;
   }
   public String getPeakUserID() {
     return this.peakUserID;
   }

   /**
* Protege name: gridID
   */
   private String gridID;
   public void setGridID(String value) { 
    this.gridID=value;
   }
   public String getGridID() {
     return this.gridID;
   }

}
