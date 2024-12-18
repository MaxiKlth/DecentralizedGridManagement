package net.peak.datamodel.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: TimeSeries
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class TimeSeries implements Concept {

   /**
* Protege name: timeSeriesValueList
   */
   private List timeSeriesValueList = new ArrayList();
   public void addTimeSeriesValueList(TimeSeriesValue elem) { 
     List oldList = this.timeSeriesValueList;
     timeSeriesValueList.add(elem);
   }
   public boolean removeTimeSeriesValueList(TimeSeriesValue elem) {
     List oldList = this.timeSeriesValueList;
     boolean result = timeSeriesValueList.remove(elem);
     return result;
   }
   public void clearAllTimeSeriesValueList() {
     List oldList = this.timeSeriesValueList;
     timeSeriesValueList.clear();
   }
   public Iterator getAllTimeSeriesValueList() {return timeSeriesValueList.iterator(); }
   public List getTimeSeriesValueList() {return timeSeriesValueList; }
   public void setTimeSeriesValueList(List l) {timeSeriesValueList = l; }

}
