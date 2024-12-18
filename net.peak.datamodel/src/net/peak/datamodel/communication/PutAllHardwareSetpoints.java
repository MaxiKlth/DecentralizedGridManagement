package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PutAllHardwareSetpoints
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class PutAllHardwareSetpoints extends PlattformToSgl{ 

   /**
* Protege name: inverterSetpointsList
   */
   private List inverterSetpointsList = new ArrayList();
   public void addInverterSetpointsList(InverterSetpoints elem) { 
     List oldList = this.inverterSetpointsList;
     inverterSetpointsList.add(elem);
   }
   public boolean removeInverterSetpointsList(InverterSetpoints elem) {
     List oldList = this.inverterSetpointsList;
     boolean result = inverterSetpointsList.remove(elem);
     return result;
   }
   public void clearAllInverterSetpointsList() {
     List oldList = this.inverterSetpointsList;
     inverterSetpointsList.clear();
   }
   public Iterator getAllInverterSetpointsList() {return inverterSetpointsList.iterator(); }
   public List getInverterSetpointsList() {return inverterSetpointsList; }
   public void setInverterSetpointsList(List l) {inverterSetpointsList = l; }

   /**
* Protege name: loadbankSetpointsList
   */
   private List loadbankSetpointsList = new ArrayList();
   public void addLoadbankSetpointsList(LoadbankSetpoints elem) { 
     List oldList = this.loadbankSetpointsList;
     loadbankSetpointsList.add(elem);
   }
   public boolean removeLoadbankSetpointsList(LoadbankSetpoints elem) {
     List oldList = this.loadbankSetpointsList;
     boolean result = loadbankSetpointsList.remove(elem);
     return result;
   }
   public void clearAllLoadbankSetpointsList() {
     List oldList = this.loadbankSetpointsList;
     loadbankSetpointsList.clear();
   }
   public Iterator getAllLoadbankSetpointsList() {return loadbankSetpointsList.iterator(); }
   public List getLoadbankSetpointsList() {return loadbankSetpointsList; }
   public void setLoadbankSetpointsList(List l) {loadbankSetpointsList = l; }

}
