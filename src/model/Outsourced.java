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
public class Outsourced extends Part {
    private String companyName;
    
    public Outsourced(int id, String name, double priceValue, int stock, int min, int max, String companyName) {
        super(id, name, priceValue, stock, min, max);
        this.companyName = companyName;
    }

    /**
     * @return the companyName
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * @param companyName the companyName to set
     */
    public void setCompanyName(String companyName) {
        java.lang.String oldCompanyName = this.companyName;
        this.companyName = companyName;
        propertyChangeSupport.firePropertyChange(PROP_COMPANYNAME, oldCompanyName, companyName);
    }

    public static final String PROP_COMPANYNAME = "companyName";
}
