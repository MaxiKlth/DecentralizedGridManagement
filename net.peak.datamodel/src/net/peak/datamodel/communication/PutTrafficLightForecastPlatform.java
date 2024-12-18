package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Prognostizierter Ampelstatus wird an die Plattform gesendet
* Protege name: PutTrafficLightForecastPlatform
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PutTrafficLightForecastPlatform extends GridToPlatform{ 

   /**
* Protege name: gridTrafficLightForecast
   */
   private GridTrafficLightForecast gridTrafficLightForecast;
   public void setGridTrafficLightForecast(GridTrafficLightForecast value) { 
    this.gridTrafficLightForecast=value;
   }
   public GridTrafficLightForecast getGridTrafficLightForecast() {
     return this.gridTrafficLightForecast;
   }

}
