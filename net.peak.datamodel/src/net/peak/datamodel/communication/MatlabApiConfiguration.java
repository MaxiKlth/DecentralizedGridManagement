package net.peak.datamodel.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: MatlabApiConfiguration
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class MatlabApiConfiguration implements Concept {

   /**
* Protege name: gridStateForecastServerURL
   */
   private String gridStateForecastServerURL;
   public void setGridStateForecastServerURL(String value) { 
    this.gridStateForecastServerURL=value;
   }
   public String getGridStateForecastServerURL() {
     return this.gridStateForecastServerURL;
   }

   /**
* Protege name: updatePowerForecastServerURL
   */
   private String updatePowerForecastServerURL;
   public void setUpdatePowerForecastServerURL(String value) { 
    this.updatePowerForecastServerURL=value;
   }
   public String getUpdatePowerForecastServerURL() {
     return this.updatePowerForecastServerURL;
   }

}
