package net.peak.datamodel.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: ForecastSchedule
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class ForecastSchedule implements Concept {

   /**
* Protege name: timeSeries
   */
   private TimeSeries timeSeries;
   public void setTimeSeries(TimeSeries value) { 
    this.timeSeries=value;
   }
   public TimeSeries getTimeSeries() {
     return this.timeSeries;
   }

}
