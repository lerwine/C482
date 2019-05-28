package model;

import java.beans.PropertyChangeSupport;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import javafx.collections.ObservableList;

/**
 * Base class for In-House and Out-sourced parts.
 * 
 * @author Leonard T. Erwine
 */
public abstract class Part /*implements InventoryItemModel*/ {
    private int id;
    private String name;
    private double price;
    private int stock;
    private int min;
    private int max;
    
    
    /**
     * Initializes a new object to represent a Part.
     * 
     * @param id The unique identifier for the part. This can be set to a negative value if it's a new part, and a new identifier will be automatically set when it is added.
     * @param name The name of the part.
     * @param price The price of the part.
     * @param stock The number of parts currently in stock.
     * @param min The minimum number of parts to have in stock before it should be re-ordered or replaced.
     * @param max The maximum number of parts that should be in stock after inventory is re-ordered or replaced.
     * @throws NullPointerException Name is null.
     * @throws InvalidParameterException Name is empty; price, stock or min is less than zero; or max is not greater than min.
     */
    protected Part(int id, String name, double price, int stock, int min, int max) throws NullPointerException, InvalidParameterException {
        this.id = id;
        if (name == null)
            throw new NullPointerException("Part name cannot be null.");
        if ((name = name.trim()).length() == 0)
            throw new InvalidParameterException("Part name cannot be empty.");
        this.name = name;
        if (price < 0)
            throw new InvalidParameterException("Price cannot be less than zero.");
        this.price = price;
        if (stock < 0)
            throw new InvalidParameterException("Inventory (stock) cannot be less than zero.");
        this.stock = stock;
        if (min < 0)
            throw new InvalidParameterException("Minimum inventory level cannot be less than zero.");
        this.min = 0;
        if (max < 0)
            throw new InvalidParameterException("Maximum inventory level cannot be less than zero.");
        if (max <= min)
            throw new InvalidParameterException("Maximum inventory must be greater than the minimum inventory level.");
        this.max = max;
    }

    /**
     * Gets the unique identifier for the current part.
     * 
     * @return The unique identifier value for the part.
     */
    public int getId() { return id; }

    /**
     * Sets the unique identifier for the current part.
     * 
     * @param id The new unique identifier value for the part.
     * @throws java.security.InvalidKeyException id is less than zero or another part already uses that id.
     */
    public void setId(int id) throws InvalidKeyException {
        int oldId = this.id;
        if (oldId == id)
            return;
        // Ensure that the id is unique and valid.
        ModelHelper.assertValidIdChange(this, id);
        this.id = id;
        propertyChangeSupport.firePropertyChange(ModelHelper.PROP_ID, oldId, id);
    }

    /**
     * Gets the name of the current part.
     * 
     * @return The name of the part
     */
    public String getName() { return name; }

    /**
     * Sets the name for the current part.
     * 
     * @param name The new name of the part.
     * @throws NullPointerException Name is null.
     * @throws java.security.InvalidParameterException Name is empty.
     */
    public void setName(String name) throws NullPointerException, InvalidParameterException {
        if (name == null)
            throw new NullPointerException("Part name cannot be null.");
        if ((name = name.trim()).length() == 0)
            throw new InvalidParameterException("Part name cannot be empty.");
        String oldName = this.name;
        if (name.equals(oldName))
            return;
        this.name = name;
        propertyChangeSupport.firePropertyChange(ModelHelper.PROP_NAME, oldName, name);
    }

    /**
     * Gets the price / cost of the part.
     * 
     * @return The price of the part.
     */
    public double getPrice() { return price; }

    /**
     * Sets the price / cost of the part.
     * 
     * @param price The new price of the part.
     * @throws java.security.InvalidParameterException Value is less than zero.
     */
    public void setPrice(double price) throws InvalidParameterException {
        double oldPrice = this.price;
        if (oldPrice == price)
            return;
        if (price < 0)
            throw new InvalidParameterException("Price cannot be less than zero.");
            
        this.price = price;
        propertyChangeSupport.firePropertyChange(ModelHelper.PROP_PRICE, oldPrice, price);
    }

    /**
     * Gets the number of parts currently in stock.
     * 
     * @return The number of parts currently in stock.
     */
    public int getStock() { return stock; }

    /**
     * Sets the number of parts currently in stock.
     * 
     * @param stock The new number of parts currently in stock.
     * @throws java.security.InvalidParameterException Value is less than zero.
     */
    public void setStock(int stock) {
        int oldStock = this.stock;
        this.stock = stock;
        propertyChangeSupport.firePropertyChange(ModelHelper.PROP_STOCK, oldStock, stock);
    }

    /**
     * Gets the minimum number of parts that can be in stock.
     * 
     * @return The minimum number of parts that can be in stock.
     */
    public int getMin() { return min; }

    /**
     * Sets the minimum number of parts that can be in stock.
     * 
     * @param min The new minimum number of parts that can be in stock.
     * @throws java.security.InvalidParameterException Value is less than zero or is not less than getMax().
     */
    public void setMin(int min) {
        if (min < 0)
            throw new InvalidParameterException("Minimum stock cannot be less than zero.");
        int oldMin = this.min;
        if (oldMin == min)
            return;
        if (min >= this.max)
            throw new InvalidParameterException("Minimum stock level must be less than the maximum stock level.");
        this.min = min;
        propertyChangeSupport.firePropertyChange(ModelHelper.PROP_MIN, oldMin, min);
    }

    /**
     * Gets the maximum number of parts that can be in stock.
     * 
     * @return The maximum number of parts that can be in stock.
     */
    public int getMax() { return max; }

    /**
     * Set the maximum number of parts that can be in stock.
     * 
     * @param max The new maximum number of parts that can be in stock.
     * @throws java.security.InvalidParameterException Value is is not greater than getMin().
     */
    public void setMax(int max) {
        int oldMax = this.max;
        if (oldMax == max)
            return;
        if (max <= this.min)
            throw new InvalidParameterException("Maximum stock level must be greater than the maximum stock level.");
        this.max = max;
        propertyChangeSupport.firePropertyChange(ModelHelper.PROP_MAX, oldMax, max);
    }
    
    /**
     * Sets both the minimum and maximum number of parts that can be in stock, validating the new range simultaneously.
     * 
     * @param min The minimum number of parts that can be in stock.
     * @param max The maximum number of parts that can be in stock.
     * @throws java.security.InvalidParameterException min is less than zero or max is is not greater than min.
     */
    public void setMinMax(int min, int max) {
        if (min < 0)
            throw new InvalidParameterException("Minimum stock cannot be less than zero.");
        if (min >= max)
            throw new InvalidParameterException("Minimum stock level must be less than the maximum stock level.");
        int oldMin = this.min;
        int oldMax = this.max;
        if (oldMin == min) {
            if (oldMax == max)
                return;
        } else {
            this.min = min;
            propertyChangeSupport.firePropertyChange(ModelHelper.PROP_MIN, oldMin, min);
            if (oldMax == max)
                return;
        }
        this.max = max;
        propertyChangeSupport.firePropertyChange(ModelHelper.PROP_MAX, oldMax, max);
    }
    
    /**
     * Ensures product has a valid unique identifier before it's added to the allParts list.
     */
    public final void ensureId() {
        if (ModelHelper.isPartAdded(this))
            return;
        int oldId = getId();
        if (oldId < 0 || Inventory.lookupPart(oldId) != null) {
            ObservableList<Part> allParts = Inventory.getAllParts();
            int nextId = allParts.size();
            if (Inventory.lookupPart(nextId) != null)
            {
                for (int i = 0; i < nextId; i++) {
                    if (Inventory.lookupPart(i) != null) {
                        id = i;
                        propertyChangeSupport.firePropertyChange(ModelHelper.PROP_ID, oldId, i);
                        return;
                    }
                }
                // this would probably never happen, but just in case...
                do { nextId++; } while (Inventory.lookupPart(nextId) != null);
            }
            id = nextId;
            propertyChangeSupport.firePropertyChange(ModelHelper.PROP_ID, oldId, nextId);
        }
    }
    
    /**
     * Allows for more efficient detection of property value changes.
     */
    protected final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
}
