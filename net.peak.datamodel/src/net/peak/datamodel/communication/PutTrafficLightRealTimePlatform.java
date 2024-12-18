package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Echtzeit Ampelstatus des Netzes wird an die Plattform gesendet
* Protege name: PutTrafficLightRealTimePlatform
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PutTrafficLightRealTimePlatform extends GridToPlatform{ 

   /**
* Protege name: gridTrafficLightRealTime
   */
   private GridTrafficLightRealTime gridTrafficLightRealTime;
   public void setGridTrafficLightRealTime(GridTrafficLightRealTime value) { 
    this.gridTrafficLightRealTime=value;
   }
   public GridTrafficLightRealTime getGridTrafficLightRealTime() {
     return this.gridTrafficLightRealTime;
   }

}
