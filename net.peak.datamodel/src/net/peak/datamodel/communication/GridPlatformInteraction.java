package net.peak.datamodel.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: GridPlatformInteraction
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class GridPlatformInteraction implements Concept {

   /**
* Protege name: trafficLight
   */
   private TrafficLight trafficLight;
   public void setTrafficLight(TrafficLight value) { 
    this.trafficLight=value;
   }
   public TrafficLight getTrafficLight() {
     return this.trafficLight;
   }

}
