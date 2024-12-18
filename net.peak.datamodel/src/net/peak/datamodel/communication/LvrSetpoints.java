package net.peak.datamodel.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: LvrSetpoints
* @author ontology bean generator
* @version 2023/02/15, 13:50:06
*/
public class LvrSetpoints extends PlattformSglInteraction{ 

   /**
* Protege name: AllStepUp
   */
   private boolean allStepUp;
   public void setAllStepUp(boolean value) { 
    this.allStepUp=value;
   }
   public boolean getAllStepUp() {
     return this.allStepUp;
   }

   /**
   * Negative Voltage Tolerance in [%]
* Protege name: ToleranceLower
   */
   private float toleranceLower;
   public void setToleranceLower(float value) { 
    this.toleranceLower=value;
   }
   public float getToleranceLower() {
     return this.toleranceLower;
   }

   /**
* Protege name: L1StepUp
   */
   private boolean l1StepUp;
   public void setL1StepUp(boolean value) { 
    this.l1StepUp=value;
   }
   public boolean getL1StepUp() {
     return this.l1StepUp;
   }

   /**
* Protege name: L3StepUp
   */
   private boolean l3StepUp;
   public void setL3StepUp(boolean value) { 
    this.l3StepUp=value;
   }
   public boolean getL3StepUp() {
     return this.l3StepUp;
   }

   /**
* Protege name: L2StepUp
   */
   private boolean l2StepUp;
   public void setL2StepUp(boolean value) { 
    this.l2StepUp=value;
   }
   public boolean getL2StepUp() {
     return this.l2StepUp;
   }

   /**
* Protege name: AllStepDown
   */
   private boolean allStepDown;
   public void setAllStepDown(boolean value) { 
    this.allStepDown=value;
   }
   public boolean getAllStepDown() {
     return this.allStepDown;
   }

   /**
   * 0: Auto Mode
1: Manual Mode
* Protege name: Mode
   */
   private int mode;
   public void setMode(int value) { 
    this.mode=value;
   }
   public int getMode() {
     return this.mode;
   }

   /**
* Protege name: L2StepDown
   */
   private boolean l2StepDown;
   public void setL2StepDown(boolean value) { 
    this.l2StepDown=value;
   }
   public boolean getL2StepDown() {
     return this.l2StepDown;
   }

   /**
   * Positive Voltage Tolerance in [%]
* Protege name: ToleranceUpper
   */
   private float toleranceUpper;
   public void setToleranceUpper(float value) { 
    this.toleranceUpper=value;
   }
   public float getToleranceUpper() {
     return this.toleranceUpper;
   }

   /**
* Protege name: L3StepDown
   */
   private boolean l3StepDown;
   public void setL3StepDown(boolean value) { 
    this.l3StepDown=value;
   }
   public boolean getL3StepDown() {
     return this.l3StepDown;
   }

   /**
   * impedance in [Ohm]
* Protege name: Impedance
   */
   private float impedance;
   public void setImpedance(float value) { 
    this.impedance=value;
   }
   public float getImpedance() {
     return this.impedance;
   }

   /**
   * Voltage setpoint for automatic modus [V]
* Protege name: VSetpoint
   */
   private float vSetpoint;
   public void setVSetpoint(float value) { 
    this.vSetpoint=value;
   }
   public float getVSetpoint() {
     return this.vSetpoint;
   }

   /**
* Protege name: L1StepDown
   */
   private boolean l1StepDown;
   public void setL1StepDown(boolean value) { 
    this.l1StepDown=value;
   }
   public boolean getL1StepDown() {
     return this.l1StepDown;
   }

   /**
   * reaction time in [Vs]
* Protege name: ReactionTime
   */
   private float reactionTime;
   public void setReactionTime(float value) { 
    this.reactionTime=value;
   }
   public float getReactionTime() {
     return this.reactionTime;
   }

}
