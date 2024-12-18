package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Prognostizierter Ampelstatus zum angegebenem Timeslot
* Protege name: GridTrafficLightForecast
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class GridTrafficLightForecast extends GridPlatformInteraction{ 

   /**
* Protege name: timeSlotStart
   */
   private LongValue timeSlotStart;
   public void setTimeSlotStart(LongValue value) { 
    this.timeSlotStart=value;
   }
   public LongValue getTimeSlotStart() {
     return this.timeSlotStart;
   }

}
