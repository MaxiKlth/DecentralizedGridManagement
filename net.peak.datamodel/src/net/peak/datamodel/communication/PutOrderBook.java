package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PutOrderBook
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PutOrderBook extends ProsumerToMarket{ 

   /**
* Protege name: orderBook
   */
   private OrderBook orderBook;
   public void setOrderBook(OrderBook value) { 
    this.orderBook=value;
   }
   public OrderBook getOrderBook() {
     return this.orderBook;
   }

}
