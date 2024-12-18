package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Liste von Leistungsprognosen aller PEAK-Teilnehmer wird an den Netzagent gesendet
* Protege name: PutForecastSchedule
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PutForecastSchedule extends GridToPlatform{ 

   /**
* Protege name: forecastScheduleTable
   */
   private List forecastScheduleTable = new ArrayList();
   public void addForecastScheduleTable(ForecastSchedule elem) { 
     List oldList = this.forecastScheduleTable;
     forecastScheduleTable.add(elem);
   }
   public boolean removeForecastScheduleTable(ForecastSchedule elem) {
     List oldList = this.forecastScheduleTable;
     boolean result = forecastScheduleTable.remove(elem);
     return result;
   }
   public void clearAllForecastScheduleTable() {
     List oldList = this.forecastScheduleTable;
     forecastScheduleTable.clear();
   }
   public Iterator getAllForecastScheduleTable() {return forecastScheduleTable.iterator(); }
   public List getForecastScheduleTable() {return forecastScheduleTable; }
   public void setForecastScheduleTable(List l) {forecastScheduleTable = l; }

}
