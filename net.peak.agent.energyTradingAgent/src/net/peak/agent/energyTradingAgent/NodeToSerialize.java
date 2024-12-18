package net.peak.agent.energyTradingAgent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import net.peak.topology.Node;

@Getter
@Setter
public class NodeToSerialize implements Serializable {

    private static final long serialVersionUID = 1L;

    // Node attributes to be serialized
    private String name;                        // Node name
    private Double energySourceCapacity = 0.0;  // Capacity of energy source
    private Double energyStorageCapacity = 0.0; // Capacity of energy storage
    private Double energyConsumption = 0.0;     // Energy consumption value
    private double EVBoolean = 0;               // Indicator for Electric Vehicle (EV) presence
    private double HeatPumpBoolean = 0;         // Indicator for Heat Pump presence
    private boolean newToSystem = false;        // Flag to indicate if the node is new to the system

    // Map to store adjacent nodes and their respective distances
    private Map<String, Double> adjacentNodes = new HashMap<>();

    /**
     * Fills this object's fields with data from the provided Node object.
     * 
     * @param node The Node object whose data is to be copied.
     */
    public void fillData(Node node) {
        this.name = node.getName();
        this.energySourceCapacity = node.getEnergySourceCapacity();
        this.energyStorageCapacity = node.getEnergyStorageCapacity();
        this.energyConsumption = node.getEnergyConsumption();
        this.EVBoolean = node.getEVBoolean();
        this.HeatPumpBoolean = node.getHeatPumpBoolean();

        // Copy all adjacent nodes into the HashMap with their respective distances
        for (Map.Entry<Node, Double> entry : node.getAdjacentNodes().entrySet()) {
            this.adjacentNodes.put(entry.getKey().getName(), entry.getValue());
        }
    }
}
