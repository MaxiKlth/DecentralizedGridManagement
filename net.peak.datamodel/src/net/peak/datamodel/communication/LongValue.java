package net.peak.datamodel.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: LongValue
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class LongValue implements Concept {

//////////////////////////// User code
/**
	 * Instantiates a new long value.
	 */
	public LongValue() {
		
	}
	
	/**
	 * Instantiates a new long value.
	 * @param value the value
	 */
	public LongValue(long value) {
		this.setLongValue(value);
	}

/**
 * Gets the long value.
 * @return the long value
 */
   public long getLongValue() {
	   return Long.parseLong(this.getLongAsString());
   }
   /**
    * Sets the long value.
    * @param longValue the new long value
    */
   public void setLongValue(long longValue) {
	   this.longAsString = "" + longValue;
   }
   /**
* Protege name: longAsString
   */
   private String longAsString;
   public void setLongAsString(String value) { 
    this.longAsString=value;
   }
   public String getLongAsString() {
     return this.longAsString;
   }

}
