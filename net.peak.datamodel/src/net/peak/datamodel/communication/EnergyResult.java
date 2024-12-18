package net.peak.datamodel.communication;

import java.util.Date;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;
import java.util.ArrayList;
import java.util.List;

/**
* Protege name: EnergyResult
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class EnergyResult extends MarketProsumerResult{ 

   /**
* Protege name: deliveredEnergy
   */
   private float deliveredEnergy;
   private double energyPriceMatched;
   private double energyAmountAsked;
   private float energyAmountFloat;
   private float initalTransactionPriceAsked;
   private int actualPeriod;
   private int actualTradingCycle;
   private TradeTypeEnum tradeType;
   private float openEnergyAmountOffer;
   private float openEnergyAmountAsked;
	private EnergyTransaction receivedEnergyOffer;
	private EnergyTransaction ownEnergyOffer;
	private double externalEnergyAmount;
	private String agentOfferedEnergy;
	private String agentAskedEnergy;
	private List<EnergyTransaction> tradeHistoryList;
	private double communcationTimeInMs;
	private boolean tradingFinished;
	private long timeStampMS;
	private double cpuLoad;
	private double ramLoad;
	
	
	
	
	
	public double getCpuLoad() {
		return cpuLoad;
	}
	public void setCpuLoad(double cpuLoad) {
		this.cpuLoad = cpuLoad;
	}
	public double getRamLoad() {
		return ramLoad;
	}
	public void setRamLoad(double ramLoad) {
		this.ramLoad = ramLoad;
	}
	public long getTimeStampMS() {
		return timeStampMS;
	}
	public void setTimeStampMS(long timeStampMS) {
		this.timeStampMS = timeStampMS;
	}
	public boolean isTradingFinished() {
		return tradingFinished;
	}
	public void setTradingFinished(boolean tradingFinished) {
		this.tradingFinished = tradingFinished;
	}
	public double getCommuncationTimeInMs() {
		return communcationTimeInMs;
	}
	public void setCommuncationTimeInMs(double communcationTimeInMs) {
		this.communcationTimeInMs = communcationTimeInMs;
	}
	public float getOpenEnergyAmountOffer() {
		return openEnergyAmountOffer;
	}
	public void setOpenEnergyAmountOffer(float openEnergyAmountOffer) {
		this.openEnergyAmountOffer = openEnergyAmountOffer;
	}
	public float getOpenEnergyAmountAsked() {
		return openEnergyAmountAsked;
	}
	public void setOpenEnergyAmountAsked(float openEnergyAmountAsked) {
		this.openEnergyAmountAsked = openEnergyAmountAsked;
	}
	public float getInitalTransactionPriceAsked() {
		return initalTransactionPriceAsked;
	}
	public void setInitalTransactionPriceAsked(float initalTransactionPriceAsked) {
		this.initalTransactionPriceAsked = initalTransactionPriceAsked;
	}
	public float getDeliveredEnergy() {
		return deliveredEnergy;
	}
	public void setDeliveredEnergy(float deliveredEnergy) {
		this.deliveredEnergy = deliveredEnergy;
	}
	public double getEnergyAmountAsked() {
		return energyAmountAsked;
	}
	public void setEnergyAmountAsked(double energyAmountAsked) {
		this.energyAmountAsked = energyAmountAsked;
	}
	public float getEnergyAmountFloat() {
		this.energyAmountFloat = deliveredEnergy;
		return energyAmountFloat;
	}
	public void setEnergyAmountFloat(float energyAmountFloat) {
		this.energyAmountFloat = energyAmountFloat;
	}
	public List<EnergyTransaction> getTradeHistoryList() {
		return tradeHistoryList;
	}
	public void setTradeHistoryList(List<EnergyTransaction> tradeHistoryList) {
		if(this.tradeHistoryList==null) {
			this.tradeHistoryList = new ArrayList<EnergyTransaction>();
		}
		this.tradeHistoryList = tradeHistoryList;
	}
	public double getEnergyPriceMatched() {
		return energyPriceMatched;
	}
	public void setEnergyPriceMatched(double energyPriceMatched) {
		this.energyPriceMatched = energyPriceMatched;
	}
	public net.peak.datamodel.communication.ProsumerMarketInteraction.TradeTypeEnum getTradeType() {
		return tradeType;
	}
	public void setTradeType(TradeTypeEnum tradeType) {
		this.tradeType = tradeType;
	}
	public EnergyTransaction getReceivedEnergyOffer() {
		return receivedEnergyOffer;
	}
	public void setReceivedEnergyOffer(EnergyTransaction receivedEnergyOffer) {
		this.receivedEnergyOffer = receivedEnergyOffer;
	}
	public EnergyTransaction getOwnEnergyOffer() {
		return ownEnergyOffer;
	}
	public void setOwnEnergyOffer(EnergyTransaction ownEnergyOffer) {
		this.ownEnergyOffer = ownEnergyOffer;
	}
	public double getExternalEnergyAmount() {
		return externalEnergyAmount;
	}
	public void setExternalEnergyAmount(double externalEnergyAmount) {
		this.externalEnergyAmount = externalEnergyAmount;
	}
	public String getAgentOfferedEnergy() {
		return agentOfferedEnergy;
	}
	public void setAgentOfferedEnergy(String agentOfferedEnergy) {
		this.agentOfferedEnergy = agentOfferedEnergy;
	}
	public String getAgentAskedEnergy() {
		return agentAskedEnergy;
	}
	public void setAgentAskedEnergy(String agentAskedEnergy) {
		this.agentAskedEnergy = agentAskedEnergy;
	}
	public int getActualTradingCycle() {
		return actualTradingCycle;
	}
	public void setActualTradingCycle(int actualTradingCycle) {
		this.actualTradingCycle = actualTradingCycle;
	}
	public int getActualPeriod() {
		return actualPeriod;
	}
	public void setActualPeriod(int actualPeriod) {
		this.actualPeriod = actualPeriod;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	private Date timeStamp;
	

   
   public void setDeliveredEnergyFloat(float energyAmountFloat) { 
    this.deliveredEnergy=energyAmountFloat;
    this.energyAmountFloat = deliveredEnergy;
   }
   public float getDeliveredEnergyFloat() {
     return this.deliveredEnergy;
   }

}
