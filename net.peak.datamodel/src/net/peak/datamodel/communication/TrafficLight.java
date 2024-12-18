package net.peak.datamodel.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: TrafficLight
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class TrafficLight implements Concept {

//////////////////////////// User code
public static String GREEN = "TrafficLightGreen";
public static String YELLOW = "TrafficLightYellow";
public static String RED = "TrafficLightRed";
   /**
* Protege name: color
   */
   private String color;
   public void setColor(String value) { 
    this.color=value;
   }
   public String getColor() {
     return this.color;
   }

}
