package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: GetOrderBookRequest
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class GetOrderBookRequest extends ProsumerToMarket{ 

   /**
* Protege name: orderBookRequest
   */
   private OrderBookRequest orderBookRequest;
   public void setOrderBookRequest(OrderBookRequest value) { 
    this.orderBookRequest=value;
   }
   public OrderBookRequest getOrderBookRequest() {
     return this.orderBookRequest;
   }

}
