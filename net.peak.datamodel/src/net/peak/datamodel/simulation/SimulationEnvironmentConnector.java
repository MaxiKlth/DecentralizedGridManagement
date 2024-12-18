package net.peak.datamodel.simulation;

import org.awb.env.networkModel.NetworkModel;

import agentgui.core.application.Application;
import agentgui.core.config.GlobalInfo.DeviceSystemExecutionMode;
import agentgui.core.config.GlobalInfo.ExecutionMode;
import agentgui.simulationService.SimulationService;
import agentgui.simulationService.SimulationServiceHelper;
import agentgui.simulationService.environment.EnvironmentModel;
import agentgui.simulationService.sensoring.ServiceSensor;
import agentgui.simulationService.sensoring.ServiceSensorInterface;
import agentgui.simulationService.time.TimeModel;
import agentgui.simulationService.time.TimeModelContinuous;
import agentgui.simulationService.time.TimeModelDiscrete;
import agentgui.simulationService.transaction.EnvironmentNotification;
import de.enflexit.ea.core.dataModel.absEnvModel.HyGridAbstractEnvironmentModel;
import de.enflexit.ea.core.dataModel.absEnvModel.SimulationStatus.STATE;
import de.enflexit.ea.core.dataModel.deployment.AgentDeploymentInformation;
import de.enflexit.ea.core.dataModel.deployment.AgentOperatingMode;
import de.enflexit.ea.core.dataModel.deployment.SetupExtension;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Location;
import jade.core.ServiceException;

/**
 * The Class SimulationEnvironmentConnector.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class SimulationEnvironmentConnector {

	private enum WaitTask {
		SimulationManager,
		EnvironementModel,
		SimulationExecution
	}
	
	private Agent agent;
	private ServiceSensor serviceSensor;
	private boolean isExecutedSimulation;
	private AgentOperatingMode operatingMode;
	
	/**
	 * Instantiates a new simulation connector.
	 * @param agent the agent
	 */
	public SimulationEnvironmentConnector(Agent agent) {
		if (agent==null) throw new NullPointerException("The agent is not allowed to be null!");
		this.agent = agent;
		this.registerSimulationServiceSensor();
	}
	
	/**
	 * Returns the SimulationServiceHelper or <code>null</code> if the {@link SimulationService} was not started.
	 * @return the simulation service helper
	 */
	private SimulationServiceHelper getSimulationServiceHelper() {
		try {
			SimulationServiceHelper simServiceHelper = (SimulationServiceHelper) this.agent.getHelper(SimulationService.NAME);
			return simServiceHelper;
		} catch (ServiceException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Registers the local SimulationService ServiceSensor.
	 */
	private void registerSimulationServiceSensor() {
		
		if (this.isSimulationEnvironment()==false) return;
		
		try {
			this.getSimulationServiceHelper().sensorPlugIn(this.getServiceSensor(), true);
		} catch (ServiceException sEx) {
			sEx.printStackTrace();
		}
	}
	/**
	 * Unregister the local SimulationService ServiceSensor.
	 */
	private void unregisterSimulationServiceSensor() {
		
		if (this.isSimulationEnvironment()==false) return;
		
		try {
			this.getSimulationServiceHelper().sensorPlugOut(this.getServiceSensor());
			this.destroyServiceSensor();
			
		} catch (ServiceException sEx) {
			sEx.printStackTrace();
		}
	}
	/**
	 * Returns the {@link SimulationService} {@link ServiceSensor}.
	 * @return the service sensor
	 */
	private ServiceSensor getServiceSensor() {
		if (serviceSensor==null) {
			serviceSensor = new ServiceSensor(new ServiceSensorInterface() {
				@Override
				public AID getAID() {
					return SimulationEnvironmentConnector.this.agent.getAID();
				}
				@Override
				public void setEnvironmentModel(EnvironmentModel envModel, boolean aSynchron) {
					// --- Nothing to do here yet ---
					if (envModel.getAbstractEnvironment() instanceof HyGridAbstractEnvironmentModel) {
						HyGridAbstractEnvironmentModel hyGridEnvModel = (HyGridAbstractEnvironmentModel) envModel.getAbstractEnvironment();
						if (hyGridEnvModel.getSimulationStatus().getState()==STATE.B_ExecuteSimuation) {
							SimulationEnvironmentConnector.this.isExecutedSimulation=true;
							SimulationEnvironmentConnector.this.unregisterSimulationServiceSensor();
						}
					}
				}
				@Override
				public void setNotification(EnvironmentNotification notification) {
					// --- Nothing to do here yet ---
				}
				@Override
				public void setPauseSimulation(boolean isPauseSimulation) {
					// --- Nothing to do here yet ---
				}
				@Override
				public void setMigration(Location newLocation) {
					// --- Nothing to do here yet ---
				}
				@Override
				public void doDelete() {
					// --- Nothing to do here yet ---
				}
			});
		}
		return serviceSensor;
	}
	private void destroyServiceSensor() {
		serviceSensor = null;
	}
	
	/**
	 * Wait for simulation connection.
	 */
	public void waitForSimulationConnection() {

		if (this.isSimulationEnvironment()==false) return;
		
		// --- Try to get a SimulationServiceHelper ----------------- 
		SimulationServiceHelper simHelper = this.getSimulationServiceHelper();
		if (simHelper==null) return;
		
		// --- Wait for a simulation manager to be found ------------
		this.waitFor(simHelper, WaitTask.SimulationManager, 5000);
		
		// --- Wait for the environment model -----------------------
		this.waitFor(simHelper, WaitTask.EnvironementModel, 5000);
		
		// --- Wait for the execution of the simulation -------------
		this.waitFor(simHelper, WaitTask.SimulationExecution, 5000);
	}
	/**
	 * This method waits for information that are provided by the {@link SimulationService}.
	 *
	 * @param simServiceHelper the current SimulationServiceHelper
	 * @param waitTask the wait task
	 * @param timeout the time out
	 */
	private void waitFor(SimulationServiceHelper simServiceHelper, WaitTask waitTask, long timeout) {
		
		// --- Fast exit ? ------------------------------------------
		if (simServiceHelper==null) return;
		if (waitTask==null) return;
		if (timeout==0) return;
		
		// --- Set actual timeout time ------------------------------
		long timeOutTime = System.currentTimeMillis() + timeout;
		while (System.currentTimeMillis() < timeOutTime) {
			
			try {
				// --- Which waiting task to be executed? -----------
				switch (waitTask) {
				case SimulationManager:
					if (simServiceHelper.getManagerAgent()!=null) return;
					break;
				case EnvironementModel:
					if (simServiceHelper.getEnvironmentModel()!=null) return;
					break;
				case SimulationExecution:
					if (this.isExecutedSimulation==true) return;
					break;
				}
				
			} catch (ServiceException ex) {
				ex.printStackTrace();
			}
			
		} // -- end while --
		System.err.println("[" + this.getClass().getSimpleName() + "-" + this.agent.getLocalName() + "] Timeout reached for waiting task '" + waitTask.name() + "'");
	}
	
	/**
	 * Checks if the current AWB execution is for a simulation.
	 * @return true, if the current execution is a simulation
	 */
	public boolean isSimulationEnvironment() {
		// --- Check application settings -----------------
		if (Application.getGlobalInfo().getExecutionMode()==ExecutionMode.DEVICE_SYSTEM) {
			if (Application.getGlobalInfo().getDeviceServiceExecutionMode()==DeviceSystemExecutionMode.AGENT) {
				return false;
			}
		}
		if (this.getSimulationServiceHelper()==null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the EnvironmentModel by using the SimulationService.
	 * @return the environment model
	 */
	public EnvironmentModel getEnvironmentModel() {
		
		if (this.isSimulationEnvironment()==false) return null;
		
		// --- Try to get a SimulationServiceHelper ----------------- 
		SimulationServiceHelper simHelper = this.getSimulationServiceHelper();
		if (simHelper==null) return null;
		
		EnvironmentModel envModel = null;
		try {
			envModel = simHelper.getEnvironmentModel();
			if (envModel==null) {
				envModel = simHelper.getEnvironmentModelFromSetup();
			}
			
		} catch (ServiceException ex) {
			ex.printStackTrace();
		}
		return envModel;
	}
	
	/**
	 * Returns the current time in Milliseconds.
	 * @return the time in Milliseconds
	 */
	public Long getTimeMillis() {
		
		EnvironmentModel envModel = this.getEnvironmentModel();
		if (envModel!=null) {
			TimeModel tm = envModel.getTimeModel();
			if (tm instanceof TimeModelContinuous) {
				TimeModelContinuous tmc = (TimeModelContinuous) tm;
				return tmc.getTime();
			} else if (tm instanceof TimeModelDiscrete) {
				TimeModelDiscrete tmd = (TimeModelDiscrete) tm;
				return tmd.getTime();
			}
			return null;
			
		} else {
			return System.currentTimeMillis();
		}
	}
	/**
	 * Returns the current NetworkModel.
	 * @return the network model
	 */
	public NetworkModel getNetworkModel() {
		
		EnvironmentModel envModel = this.getEnvironmentModel();
		if (envModel!=null) {
			return (NetworkModel) envModel.getDisplayEnvironment();
		}
		return null;
	}
	/**
	 * Returns the HyGridAbstractEnvironmentModel.
	 * @return the HyGridAbstractEnvironmentModel 
	 */
	public HyGridAbstractEnvironmentModel getHyGridAbstractEnvironmentModel() {
		
		EnvironmentModel envModel = this.getEnvironmentModel();
		if (envModel!=null) {
			return (HyGridAbstractEnvironmentModel) envModel.getAbstractEnvironment();
		}
		return null;
	}
	
	/**
	 * Gets the current {@link AgentOperatingMode}.
	 * @return the operating mode
	 */
	public AgentOperatingMode getAgentOperatingMode() {
		if (operatingMode==null) {
			if (this.isSimulationEnvironment()==true) {
				// --- The simulation case ----------------------------------------------
				operatingMode = AgentOperatingMode.Simulation;
			} else {
				// --- Possible other cases - check the SetupExtension ------------------
				SetupExtension setEx = this.getHyGridAbstractEnvironmentModel().getSetupExtension();
				if (setEx!=null) {
					AgentDeploymentInformation agentInfo = setEx.getDeploymentGroupsHelper().getAgentDeploymentInformation(this.agent.getLocalName());
					operatingMode = agentInfo.getAgentOperatingMode();
				}
			}
			// --- Use 'real' as backup solution ----------------------------------------
			if (operatingMode==null) {
				System.out.println("[" + this.getClass().getSimpleName() + " + " + this.agent.getLocalName() + "] Use backup operating mode 'RealSystem'.");
				operatingMode = AgentOperatingMode.RealSystem;
			}
		}
		return operatingMode;
	}
	
	
}
