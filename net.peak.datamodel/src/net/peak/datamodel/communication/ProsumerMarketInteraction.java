package net.peak.datamodel.communication;



import jade.util.leap.*;
import jade.content.*;
import jade.core.*;

import jade.content.Concept;
import jade.core.AID;

/**
* Protege name: ProsumerMarketInteraction
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class ProsumerMarketInteraction implements Concept {

   /**
* Protege name: timeSlotStart
   */

	private double initialEnergyAmountOffered;
	private double initialTransactionPriceOffered;
	private PriceTypeEnum priceType;
	private String tradeTypeString;
	private float energyPriceThreshold = (float) 0.01;
	private AID askingAgent;
	private AID offeringAgent;
	private List energyTransaction = new ArrayList();
	private double initialEnergyAmountAsked;
	private double initialTransactionPriceAsked;
	private int iteration;
	
	public int getIteration() {
		return iteration;
	}
	public void setIteration(int iteration) {
		this.iteration = iteration;
	}
	//	private int tradingPeriod = 0;
//	
//	
//	
//	
//	public int getTradingPeriod() {
//		return tradingPeriod;
//	}
//	public void setTradingPeriod(int tradingPeriod) {
//		this.tradingPeriod = tradingPeriod;
//	}
	public double getInitialEnergyAmountOffered() {
		return initialEnergyAmountOffered;
	}
	public void setInitialEnergyAmountOffered(double initialEnergyAmountOffered) {
		this.initialEnergyAmountOffered = initialEnergyAmountOffered;
	}
	public double getInitialTransactionPriceOffered() {
		return initialTransactionPriceOffered;
	}
	public void setInitialTransactionPriceOffered(double initialTransactionPriceOffered) {
		this.initialTransactionPriceOffered = initialTransactionPriceOffered;
	}
	public double getInitialEnergyAmountAsked() {
		return initialEnergyAmountAsked;
	}
	public void setInitialEnergyAmountAsked(double initialEnergyAmountAsked) {
		this.initialEnergyAmountAsked = initialEnergyAmountAsked;
	}
	public double getInitialTransactionPriceAsked() {
		return initialTransactionPriceAsked;
	}
	public void setInitialTransactionPriceAsked(double initialTransactionPriceAsked) {
		this.initialTransactionPriceAsked = initialTransactionPriceAsked;
	}
	public Iterator getAllEnergyTransaction() {return energyTransaction.iterator();}  
	public List getEnergyTransaction() {
	   if(energyTransaction == null) {
		   energyTransaction = new ArrayList();
	   }
	return energyTransaction;
	}
	public void setEnergyTransaction(List listHistoricalTrades) {
		this.energyTransaction = listHistoricalTrades;
	}
	public void addEnergyTransaction(EnergyTransaction eT2bAdded) {
		if (energyTransaction == null) {
			energyTransaction = new ArrayList();
		}
		energyTransaction.add(eT2bAdded);
	}
	public boolean removeEnergyTransaction(EnergyTransaction energyTransaction) {
		boolean result = this.energyTransaction.remove(energyTransaction);
		return result;
	}
	
	public void clearAllEnergyTransaction() {
		this.energyTransaction.clear();
	}
		
	
	public AID getAskingAgent() {
		return askingAgent;
	}

	public void setAskingAgent(AID askingAgent) {
		this.askingAgent = askingAgent;
	}

	public AID getOfferingAgent() {
		return offeringAgent;
	}

	public void setOfferingAgent(AID offeringAgent) {
		this.offeringAgent = offeringAgent;
	}

	public float getEnergyPriceThreshold() {
		return energyPriceThreshold;
	}

	public void setEnergyPriceThreshold(float energyPriceThreshold) {
		this.energyPriceThreshold = energyPriceThreshold;
	}

	public PriceTypeEnum getPriceType() {
		return priceType;
	}

	public void setPriceType(PriceTypeEnum priceType) {
		this.priceType = priceType;
	}
	
	public String getTradeTypeString() {
		return tradeTypeString;
	}

	
	public void setTradeTypeString (String s) {
		this.tradeTypeString=s;
	}


	
   private LongValue timeSlotStart;
   public void setTimeSlotStart(LongValue value) { 
    this.timeSlotStart=value;
   }
   public LongValue getTimeSlotStart() {
     return this.timeSlotStart;
   }

   /**
* Protege name: localTransactionID
   */
   private String localTransactionID;
   public void setLocalTransactionID(String value) { 
    this.localTransactionID=value;
   }
   public String getLocalTransactionID() {
     return this.localTransactionID;
   }

   /**
* Protege name: peakMemberID
   */
   private int peakMemberID;
   public void setPeakMemberID(int value) { 
    this.peakMemberID=value;
   }
   public int getPeakMemberID() {
     return this.peakMemberID;
   }

   /**
* Protege name: transactionPrice
   */
   private float transactionPrice;
   public void setTransactionPrice(float value) { 
    this.transactionPrice=value;
   }
   public float getTransactionPrice() {
     return this.transactionPrice;
   }
   
	public enum TradeTypeEnum {
	    ASK("Ask"),
	    
	    OFFER("Offer");

	    public String value;

	    TradeTypeEnum(String value) {
	      this.value = value;
	    }
	}
	
	public enum PriceTypeEnum {
	    HIGH("High"),
	    
	    NORMAL("Normal"),
		
		LOW("Low");

	    private String value;

	    PriceTypeEnum(String value) {
	    	this.value = value;
		}
	}

}
