package net.peak.datamodel.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: OrderBook
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class OrderBook implements Concept {

   /**
* Protege name: minBidPrice
   */
   private float minBidPrice;
   public void setMinBidPrice(float value) { 
    this.minBidPrice=value;
   }
   public float getMinBidPrice() {
     return this.minBidPrice;
   }

   /**
* Protege name: counterAsk
   */
   private int counterAsk;
   public void setCounterAsk(int value) { 
    this.counterAsk=value;
   }
   public int getCounterAsk() {
     return this.counterAsk;
   }

   /**
* Protege name: sumBidAmount
   */
   private float sumBidAmount;
   public void setSumBidAmount(float value) { 
    this.sumBidAmount=value;
   }
   public float getSumBidAmount() {
     return this.sumBidAmount;
   }

   /**
* Protege name: avgBidPrice
   */
   private float avgBidPrice;
   public void setAvgBidPrice(float value) { 
    this.avgBidPrice=value;
   }
   public float getAvgBidPrice() {
     return this.avgBidPrice;
   }

   /**
* Protege name: counterBid
   */
   private int counterBid;
   public void setCounterBid(int value) { 
    this.counterBid=value;
   }
   public int getCounterBid() {
     return this.counterBid;
   }

   /**
* Protege name: quarterHourStatus
   */
   private String quarterHourStatus;
   public void setQuarterHourStatus(String value) { 
    this.quarterHourStatus=value;
   }
   public String getQuarterHourStatus() {
     return this.quarterHourStatus;
   }

   /**
* Protege name: avgAskPrice
   */
   private float avgAskPrice;
   public void setAvgAskPrice(float value) { 
    this.avgAskPrice=value;
   }
   public float getAvgAskPrice() {
     return this.avgAskPrice;
   }

   /**
* Protege name: maxAskPrice
   */
   private float maxAskPrice;
   public void setMaxAskPrice(float value) { 
    this.maxAskPrice=value;
   }
   public float getMaxAskPrice() {
     return this.maxAskPrice;
   }

   /**
* Protege name: createdDate
   */
   private LongValue createdDate;
   public void setCreatedDate(LongValue value) { 
    this.createdDate=value;
   }
   public LongValue getCreatedDate() {
     return this.createdDate;
   }

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

   /**
* Protege name: changedDate
   */
   private String changedDate;
   public void setChangedDate(String value) { 
    this.changedDate=value;
   }
   public String getChangedDate() {
     return this.changedDate;
   }

   /**
* Protege name: sumAskAmount
   */
   private float sumAskAmount;
   public void setSumAskAmount(float value) { 
    this.sumAskAmount=value;
   }
   public float getSumAskAmount() {
     return this.sumAskAmount;
   }

   /**
* Protege name: minAskPrice
   */
   private float minAskPrice;
   public void setMinAskPrice(float value) { 
    this.minAskPrice=value;
   }
   public float getMinAskPrice() {
     return this.minAskPrice;
   }

}
