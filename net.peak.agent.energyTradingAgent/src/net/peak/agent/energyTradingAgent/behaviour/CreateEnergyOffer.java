package net.peak.agent.energyTradingAgent.behaviour;

import java.util.List;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import net.peak.agent.energyTradingAgent.EnergyTradingAgent;
import net.peak.agent.energyTradingAgent.InternalDataModel.OfferAnswerType;
import net.peak.datamodel.communication.EnergyTransaction;
import net.peak.datamodel.communication.ProsumerMarketInteraction.PriceTypeEnum;

public class CreateEnergyOffer extends OneShotBehaviour {

    private EnergyTradingAgent energyTradingAgent;
    private float energyAmountFloat;
    private EnergyTransaction energyOffer = new EnergyTransaction();
    private String offer;
    private String ask;
    private boolean topologyOptimizationOn = true;
    private boolean isFirstOffer = false;

    /**
     * Constructor to initialize CreateEnergyOffer with an existing energy transaction.
     * If the provided energy transaction is null, a new one is created.
     * 
     * @param energyTradingAgent The agent managing energy trading.
     * @param energyOffer        The energy transaction to be processed.
     */
    public CreateEnergyOffer(EnergyTradingAgent energyTradingAgent, EnergyTransaction energyOffer) {
        this.energyTradingAgent = energyTradingAgent;
        if (energyOffer == null) {
            this.energyOffer = new EnergyTransaction();
        } else {
            this.energyOffer = energyOffer;
        }
        this.energyAmountFloat = energyOffer.getEnergyAmountFloat();
    }

    /**
     * Main action method that creates an energy offer or request based on the
     * agent's state and the current energy balance. The offer is then sent to other agents.
     */
    @Override
    public void action() {
        // Initialize offer and ask strings from the internal data model.
        offer = this.energyTradingAgent.getInternalDataModel().Offer;
        ask = this.energyTradingAgent.getInternalDataModel().Ask;
        
        // Get the state of charge (SOC) of the storage.
        double storageSOC = this.energyTradingAgent.getInternalDataModel().getStorage().getStorageSOC();

        // If no energy offer exists, create a new one.
        if (this.energyOffer == null) {
            this.energyOffer = new EnergyTransaction();
        }

        double energyBalance = this.energyAmountFloat;

        // Determine whether to create an offer or a request based on the storage state of charge (SOC).
        if (!this.energyTradingAgent.getInternalDataModel().getOwnEnergyOffer().getTradeTypeString().equals(ask)) {
            if (storageSOC < 0.1 && energyBalance < 0) {
                // If SOC is low and energy balance is negative, set up an energy request.
                energyOffer = setAskingValuesAtEnergyOffer(energyOffer);
                energyOffer.setPriceType(PriceTypeEnum.HIGH);
                this.energyAmountFloat = (float) -energyBalance;
            } else if (storageSOC < 0.1 && energyBalance >= 0) {
                // If SOC is low and energy balance is positive, set up an energy offer.
                energyOffer = setOfferingValuesAtEnergyOffer(energyOffer);
                energyOffer.setPriceType(PriceTypeEnum.HIGH);
                this.energyAmountFloat = (float) energyBalance;
            } else if (storageSOC > 0.9 && energyBalance < 0) {
                // If SOC is high and energy balance is negative, set up an energy request.
                energyOffer = setAskingValuesAtEnergyOffer(energyOffer);
                energyOffer.setPriceType(PriceTypeEnum.LOW);
                this.energyAmountFloat = (float) -energyBalance;
            } else if (storageSOC > 0.9 && energyBalance >= 0) {
                // If SOC is high and energy balance is positive, set up an energy offer.
                energyOffer = setOfferingValuesAtEnergyOffer(energyOffer);
                energyOffer.setPriceType(PriceTypeEnum.LOW);
                this.energyAmountFloat = (float) energyBalance;
            }
        } else {
            // Set up an energy request if the current offer is an asking offer.
            energyOffer = setAskingValuesAtEnergyOffer(energyOffer);
            energyOffer.setPriceType(PriceTypeEnum.LOW);
        }

        // Calculate and set the transaction price based on the determined price type.
        this.energyOffer.setTransactionPrice(calculatePrice(this.energyOffer.getPriceType()));

        // Set initial values for the transaction if it's an asking offer.
        if (this.energyOffer.getTradeTypeString().equals(ask) && this.energyOffer.getInitialTransactionPriceAsked() == 0) {
            this.energyOffer.setInitialTransactionPriceAsked(this.energyOffer.getTransactionPrice());
            this.energyOffer.setInitialEnergyAmountAsked(this.energyOffer.getEnergyAmountFloat());
        }

        // Update the open energy amount in the agent's data model.
        float openEnergyAmount = this.energyTradingAgent.updateOpenEnergyAmount();
        this.energyTradingAgent.getInternalDataModel().setNumberAgentOfferedEnergy(0);

        // Get the list of open grid nodes (agents available for trading).
        List<AID> openGridNodes = this.energyTradingAgent.updateAgentAddresses();

        if (this.energyTradingAgent.getLocalName().equals("40000")) {
            int zp = 1; // This line seems to be a placeholder for debugging or further implementation.
        }

        // Send the offer to each available grid node if it's not the agent itself.
        if (this.energyOffer.getOfferingAgent() == energyTradingAgent.getAID()) {
            if (openEnergyAmount > 0 && this.energyTradingAgent.getInternalDataModel().getOwnEnergyOffer().getEnergyAmountFloat() > 0) {
                for (int i = this.energyTradingAgent.getInternalDataModel().getGridNodesCnt(); i < openGridNodes.size(); i++) {
                    if (openEnergyAmount > 0 && this.energyTradingAgent.getInternalDataModel().getOwnEnergyOffer().getEnergyAmountFloat() > 0) {
                        if (!openGridNodes.get(i).getLocalName().equals(this.energyTradingAgent.getLocalName())) {
                            if (energyOffer.getInitialEnergyAmountAsked() == 0)
                            energyOffer.setInitialEnergyAmountOffered(energyOffer.getEnergyAmountFloat());
                            energyOffer.setInitialTransactionPriceOffered(energyOffer.getTransactionPrice());

                            SendEnergyOffer seo;

                            // Store the offer in the internal data model.
                            this.energyTradingAgent.getInternalDataModel().getListOpenEnergyOffers().put(energyOffer.getLocalTransactionID(), energyOffer);
                            openEnergyAmount = this.energyTradingAgent.updateOpenEnergyAmount();

                            // Determine if this is the first offer or a reoffer.
                            if (this.energyTradingAgent.getInternalDataModel().getTradingCyclesCnt() == 0) {
                                seo = new SendEnergyOffer(this.energyTradingAgent, this.energyOffer, openGridNodes.get(i), OfferAnswerType.FIRSTOFFER);
                                this.energyTradingAgent.getInternalDataModel().increaseGridNodeCnt();
                            } else {
                                seo = new SendEnergyOffer(this.energyTradingAgent, this.energyOffer, openGridNodes.get(i), OfferAnswerType.REOFFER);
                            }

                            // Add the behavior to send the energy offer.
                            this.energyTradingAgent.addBehaviour(seo);

                            // Reset the waiting iteration counter.
                            this.energyTradingAgent.getInternalDataModel().setStartWaitingIteration(0);
                        }
                    }
                }
            }
        }
    }

    /**
     * Calculates the transaction price based on the given price type.
     * 
     * @param priceType The type of price (HIGH, NORMAL, LOW).
     * @return The calculated price as a float.
     */
    private float calculatePrice(PriceTypeEnum priceType) {
        float price = 0;
        //Default Value
        if (priceType == null) priceType = PriceTypeEnum.HIGH;
        switch (priceType) {
            case HIGH:
                price = (float) (Math.random() * this.energyTradingAgent.getInternalDataModel().getHighPriceOffset() + this.energyTradingAgent.getInternalDataModel().getFeedInTariff());
                break;
            case NORMAL:
                price = (float) (Math.random() * this.energyTradingAgent.getInternalDataModel().getHighPriceOffset() + this.energyTradingAgent.getInternalDataModel().getFeedInTariff());
                break;
            case LOW:
                price = (float) (Math.random() * this.energyTradingAgent.getInternalDataModel().getLowPriceOffset() + this.energyTradingAgent.getInternalDataModel().getFeedInTariff());
                break;
        }
        return price;
    }

    /**
     * Sets the values for an energy offering transaction.
     * 
     * @param energyOffer The energy transaction to be modified.
     * @return The modified energy transaction.
     */
    private EnergyTransaction setOfferingValuesAtEnergyOffer(EnergyTransaction energyOffer) {
        energyOffer.setOfferingAgent(this.energyTradingAgent.getAID());
        this.energyTradingAgent.getInternalDataModel().getOwnEnergyOffer().setTradeTypeString(offer);
        energyOffer.setTradeTypeString(offer);

        return energyOffer;
    }

    /**
     * Sets the values for an energy asking transaction.
     * 
     * @param energyOffer The energy transaction to be modified.
     * @return The modified energy transaction.
     */
    private EnergyTransaction setAskingValuesAtEnergyOffer(EnergyTransaction energyOffer) {
        energyOffer.setAskingAgent(this.energyTradingAgent.getAID());
        this.energyTradingAgent.getInternalDataModel().getOwnEnergyOffer().setTradeTypeString(ask);
        energyOffer.setTradeTypeString(ask);
        return energyOffer;
    }

    /**
     * Finds an AID by its name.
     * 
     * @param s The name of the agent.
     * @return The AID of the agent.
     */
    private AID findAIDByName(String s) {
        AID resultingAID = new AID(s, AID.ISLOCALNAME);
        return resultingAID;
    }
}
