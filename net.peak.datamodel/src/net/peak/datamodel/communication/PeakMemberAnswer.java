package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PeakMemberAnswer
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PeakMemberAnswer extends PeakMemberInteraction{ 

   /**
* Protege name: forecastSchedule
   */
   private ForecastSchedule forecastSchedule;
   public void setForecastSchedule(ForecastSchedule value) { 
    this.forecastSchedule=value;
   }
   public ForecastSchedule getForecastSchedule() {
     return this.forecastSchedule;
   }

}
