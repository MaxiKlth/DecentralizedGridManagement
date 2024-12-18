package net.peak.datamodel.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: TimeSeriesValue
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class TimeSeriesValue implements Concept {

   /**
* Protege name: timeStamp
   */
   private LongValue timeStamp;
   public void setTimeStamp(LongValue value) { 
    this.timeStamp=value;
   }
   public LongValue getTimeStamp() {
     return this.timeStamp;
   }

   /**
* Protege name: unitValue
   */
   private UnitValue unitValue;
   public void setUnitValue(UnitValue value) { 
    this.unitValue=value;
   }
   public UnitValue getUnitValue() {
     return this.unitValue;
   }

}
