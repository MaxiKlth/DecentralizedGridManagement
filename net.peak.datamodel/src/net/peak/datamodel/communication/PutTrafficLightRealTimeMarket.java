package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Echtzeit Ampelstatus des Netzes wird an den Markt gesendet
* Protege name: PutTrafficLightRealTimeMarket
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PutTrafficLightRealTimeMarket extends GridToMarket{ 

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
