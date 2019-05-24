/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.beans.PropertyChangeSupport;
import java.security.InvalidParameterException;

/**
 *
 * @author lerwi
 */
public abstract class Part implements IdReferenceableObject {
    private int id;
    private String name;
    private double price;
    private int stock;
    private int min;
    private int max;

    public Part(int id, String name, double price, int stock, int min, int max) {
        this.id = id;
        this.price = price;
        this.stock = stock;
        this.min = min;
        this.max = max;
    }

    /**
     * @return the id
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    @Override
    public void setId(int id) {
        int oldId = this.id;
        Inventory.assertValidIdChange(this, id);
        this.id = id;
        propertyChangeSupport.firePropertyChange(PROP_ID, oldId, id);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        java.lang.String oldName = this.name;
        this.name = name;
        propertyChangeSupport.firePropertyChange(PROP_NAME, oldName, name);
    }

    /**
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(double price) {
        double oldPrice = this.price;
        this.price = price;
        propertyChangeSupport.firePropertyChange(PROP_PRICEVALUE, oldPrice, price);
    }

    /**
     * @return the stock
     */
    public int getStock() {
        return stock;
    }

    /**
     * @param stock the stock to set
     */
    public void setStock(int stock) {
        int oldStock = this.stock;
        this.stock = stock;
        propertyChangeSupport.firePropertyChange(PROP_STOCK, oldStock, stock);
    }

    /**
     * @return the min
     */
    public int getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(int min) {
        int oldMin = this.min;
        this.min = min;
        propertyChangeSupport.firePropertyChange(PROP_MIN, oldMin, min);
    }

    /**
     * @return the max
     */
    public int getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(int max) {
        int oldMax = this.max;
        this.max = max;
        propertyChangeSupport.firePropertyChange(PROP_MAX, oldMax, max);
    }
    
    public final transient PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
    public static final String PROP_ID = "id";
    public static final String PROP_NAME = "name";
    public static final String PROP_PRICEVALUE = "priceValue";
    public static final String PROP_STOCK = "stock";
    public static final String PROP_MIN = "min";
    public static final String PROP_MAX = "max";
}
