package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Prognostizierter Ampelstatus wird an den Markt gesendet
* Protege name: PutTrafficLightForecastMarket
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PutTrafficLightForecastMarket extends GridToMarket{ 

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
