package net.peak.datamodel.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * WGS84 Coordinate
* Protege name: Location
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class Location implements Concept {

   /**
* Protege name: longitude
   */
   private float longitude;
   public void setLongitude(float value) { 
    this.longitude=value;
   }
   public float getLongitude() {
     return this.longitude;
   }

   /**
* Protege name: latitude
   */
   private float latitude;
   public void setLatitude(float value) { 
    this.latitude=value;
   }
   public float getLatitude() {
     return this.latitude;
   }

}
