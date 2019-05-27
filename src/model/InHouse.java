package model;

import java.security.InvalidParameterException;

/**
 *Represents an part that is produced in-house.
 * @author Leonard T. Erwine
 */
public class InHouse extends Part {
    private int machineId;
    
    /**
     *Creates a new object to represent a part that is produced in-house.
     * @param id The unique identifier for the part. This can be set to a negative value if it's a new part, and a new identifier will be automatically set when it is added.
     * @param name The name of the part.
     * @param price The price of the part.
     * @param stock The number of parts currently in stock.
     * @param min The minimum number of parts to have in stock before it should be re-ordered or replaced.
     * @param max The maximum number of parts that should be in stock after inventory is re-ordered or replaced.
     * @param machineId The identifier of the machine that produced the part.
     * @throws NullPointerException Name is null.
     * @throws InvalidParameterException Name is empty; price, stock or min is less than zero; or max is not greater than min.
     */
    public InHouse(int id, String name, double price, int stock, int min, int max, int machineId) throws NullPointerException, InvalidParameterException {
        super(id, name, price, stock, min, max);
        this.machineId = machineId;
    }

    /**
     * @return The identifier of the machine that produced the part.
     */
    public int getMachineId() { return machineId; }

    /**
     * @param machineId The identifier of the machine that produced the part.
     */
    public void setMachineId(int machineId) {
        int oldMachineId = this.machineId;
        this.machineId = machineId;
        propertyChangeSupport.firePropertyChange(PROP_MACHINEID, oldMachineId, machineId);
    }
    
    public static final String PROP_MACHINEID = "machineId";
}
