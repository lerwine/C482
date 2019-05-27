package model;

import java.beans.PropertyChangeSupport;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *Class which represents a product.
 * @author Leonard T. Erwine
 */
public class Product implements IdReferenceableObject {
    private final ObservableList<Part> associatedParts = FXCollections.observableArrayList();
    private int id;
    private String name;
    private double price;
    private int stock;
    private int min;
    private int max;
    
    /**
     *Creates a new object to represent a Product.
     * @param id The unique identifier for the product. This can be set to a negative value if it's a new product, and a new identifier will be automatically set when it is added.
     * @param name The name of the product.
     * @param price The price of the product.
     * @param stock The number of products currently in stock.
     * @param min The minimum number of products to have in stock before it should be replenished.
     * @param max The maximum number of products that should be in stock after inventory is replenished.
     * @throws NullPointerException Name is null.
     * @throws InvalidParameterException Name is empty; price, stock or min is less than zero; or max is not greater than min.
     */
    public Product(int id, String name, double price, int stock, int min, int max) throws NullPointerException, InvalidParameterException {
        this.id = id;
        if (name == null)
            throw new NullPointerException("Product name cannot be null.");
        if ((name = name.trim()).length() == 0)
            throw new InvalidParameterException("Product name cannot be empty.");
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

    public static final String PROP_ID = "id";
    
    /**
     * @return The unique identifier value for the part.
     */
    @Override
    public int getId() { return id; }

    /**
     * @param id The new unique identifier value for the part.
     * @throws java.security.InvalidKeyException id is less than zero or another part already uses that id.
     */
    @Override
    public void setId(int id) throws InvalidKeyException {
        int oldId = this.id;
        // Ensure that the id is unique and valid.
        Inventory.assertValidIdChange(this, id);
        this.id = id;
        propertyChangeSupport.firePropertyChange(PROP_ID, oldId, id);
    }

    public static final String PROP_NAME = "name";
    
    /**
     * @return The name of the product
     */
    public String getName() { return name; }

    /**
     * @param name The new name of the product.
     * @throws NullPointerException Name is null.
     * @throws java.security.InvalidParameterException Name is empty.
     */
    public void setName(String name) {
        if (name == null)
            throw new NullPointerException("Product name cannot be null.");
        if ((name = name.trim()).length() == 0)
            throw new InvalidParameterException("Product name cannot be empty.");
        String oldName = this.name;
        if (name.equals(oldName))
            return;
        this.name = name;
        propertyChangeSupport.firePropertyChange(PROP_NAME, oldName, name);
    }

    public static final String PROP_PRICE = "price";
    
    /**
     * @return The price of the product.
     */
    public double getPrice() { return price; }

    /**
     * @param price The new price of the product.
     * @throws java.security.InvalidParameterException Value is less than zero.
     */
    public void setPrice(double price) throws InvalidParameterException {
        double oldPrice = this.price;
        if (oldPrice == price)
            return;
        if (price < 0)
            throw new InvalidParameterException("Price cannot be less than zero.");
            
        this.price = price;
        propertyChangeSupport.firePropertyChange(PROP_PRICE, oldPrice, price);
    }

    public static final String PROP_STOCK = "stock";
    
    /**
     * @return The number of products currently in stock.
     */
    public int getStock() { return stock; }

    /**
     * @param stock The new number of products currently in stock.
     * @throws java.security.InvalidParameterException Value is less than zero.
     */
    public void setStock(int stock) {
        int oldStock = this.stock;
        this.stock = stock;
        propertyChangeSupport.firePropertyChange(PROP_STOCK, oldStock, stock);
    }

    public static final String PROP_MIN = "min";
    
    /**
     * @return The minimum number of product in stock before the items need to be replenished.
     */
    public int getMin() { return min; }

    /**
     * @param min The minimum number of product in stock before the items need to be replenished.
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
        propertyChangeSupport.firePropertyChange(PROP_MIN, oldMin, min);
    }

    public static final String PROP_MAX = "max";
    
    /**
     * @return The maximum number of products that should be in stock after replacement inventory is replenished.
     */
    public int getMax() { return max; }

    /**
     * @param max The new maximum number of products that should be in stock after replacement inventory is replenished.
     * @throws java.security.InvalidParameterException Value is is not greater than getMin().
     */
    public void setMax(int max) {
        int oldMax = this.max;
        if (oldMax == max)
            return;
        if (max <= this.min)
            throw new InvalidParameterException("Maximum stock level must be greater than the maximum stock level.");
        this.max = max;
        propertyChangeSupport.firePropertyChange(PROP_MAX, oldMax, max);
    }
    
    /**
     * @param min The minimum number of products in stock before the inventory needs to be replenished.
     * @param max The maximum number of products that should be in stock after replacement inventory is replenished.
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
            propertyChangeSupport.firePropertyChange(PROP_MIN, oldMin, min);
            if (oldMax == max)
                return;
        }
        this.max = max;
        propertyChangeSupport.firePropertyChange(PROP_MAX, oldMax, max);
    }
    
    /**
     * @param part Part to associate with the current Product.
     */
    public void addAssociatedPart(Part part) {
        if (part == null)
            throw new NullPointerException();
        if (associatedParts.contains(part))
            return;
        // Make sure it exists in the 'all parts' list beforehand.
        if (!Inventory.containsPart(part))
            Inventory.addPart(part);
        associatedParts.add(part);
    }

    /**
     * @param part Part to disassociated from the current Product.
     */
    public void deleteAssociatedPart(Part part) {
        if (part != null && associatedParts.contains(part))
            associatedParts.remove(part);
    }
    
    /**
     * @param part Part to look for.
     * @return true if the Part is associated with the current product; otherwise, false.
     */
    public boolean containsAssociatedPart(Part part) { return part != null && associatedParts.contains(part); }

    /**
     * @return The list of parts associated with the current Product.
     */
    public ObservableList<Part> getAllAssociatedParts() { return associatedParts; }

    /**
     *Re-populates the associated Part objects.
     * @param parts The Parts to be associated with the current Product.
     */
    public void setAllAssociatedParts(Iterable<Part> parts) {
        associatedParts.clear();
        for (Part p : parts)
            addAssociatedPart(p);
    }
    /**
     *Ensures product has a valid unique identifier before it's added to the allProducts list.
     */
    public final void ensureId() {
        if (Inventory.containsProduct(this))
            return;
        int oldId = getId();
        if (oldId < 0 || Inventory.lookupProduct(oldId) != null) {
            ObservableList<Product> allProducts = Inventory.getAllProducts();
            int nextId = allProducts.size();
            if (Inventory.lookupProduct(nextId) != null)
            {
                for (int i = 0; i < nextId; i++) {
                    if (Inventory.lookupProduct(i) != null) {
                        id = i;
                        propertyChangeSupport.firePropertyChange(PROP_ID, oldId, i);
                        return;
                    }
                }
                // this would probably never happen, but just in case...
                do { nextId++; } while (Inventory.lookupProduct(nextId) != null);
            }
            id = nextId;
            propertyChangeSupport.firePropertyChange(PROP_ID, oldId, nextId);
        }
    }
    
    public final transient PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
}
