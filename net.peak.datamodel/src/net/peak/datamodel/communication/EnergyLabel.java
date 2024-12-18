package net.peak.datamodel.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: EnergyLabel
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class EnergyLabel implements Concept {

//////////////////////////// User code
public static String NUCLEAR = "NuclearPower";
public static String WIND= "WindEnergy";
public static String PHOTOVOLTAIC = "PhotovoltaicEnergy";
public static String BATTERY = "Battery";
   /**
* Protege name: label
   */
   private String label;
   public void setLabel(String value) { 
    this.label=value;
   }
   public String getLabel() {
     return this.label;
   }

}
