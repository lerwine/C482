/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.beans.PropertyChangeSupport;

/**
 *
 * @author lerwi
 */
public class InHouse extends Part {
    private int machineId;
    
    public InHouse(int id, String name, double priceValue, int stock, int min, int max, int machineId) {
        super(id, name, priceValue, stock, min, max);
        this.machineId = machineId;
    }

    /**
     * @return the machineId
     */
    public int getMachineId() {
        return machineId;
    }

    /**
     * @param machineId the machineId to set
     */
    public void setMachineId(int machineId) {
        int oldMachineId = this.machineId;
        this.machineId = machineId;
        propertyChangeSupport.firePropertyChange(PROP_MACHINEID, oldMachineId, machineId);
    }
    public static final String PROP_MACHINEID = "machineId";
}
