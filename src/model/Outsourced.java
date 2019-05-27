package model;

import java.security.InvalidParameterException;

/**
 *Represents a part that is out-sourced.
 * @author Leonard T. Erwine
 */
public class Outsourced extends Part {
    private String companyName;
    
    /**
     *Creates a new object to represent a part that is out-sourced.
     * @param id The unique identifier for the part. This can be set to a negative value if it's a new part, and a new identifier will be automatically set when it is added.
     * @param name The name of the part.
     * @param price The price of the part.
     * @param stock The number of parts currently in stock.
     * @param min The minimum number of parts to have in stock before it should be re-ordered or replaced.
     * @param max The maximum number of parts that should be in stock after inventory is re-ordered or replaced.
     * @param companyName The name of the company that produced the part.
     * @throws NullPointerException name or companyName is null.
     * @throws InvalidParameterException name or companyName is empty; price, stock or min is less than zero; or max is not greater than min.
     */
    public Outsourced(int id, String name, double price, int stock, int min, int max, String companyName) throws NullPointerException, InvalidParameterException {
        super(id, name, price, stock, min, max);
        if (companyName == null)
            throw new NullPointerException("Company name cannot be null.");
        if ((companyName = companyName.trim()).length() == 0)
            throw new InvalidParameterException("Company name cannot be empty.");
        this.companyName = companyName;
    }

    /**
     * @return The name of the company that produced the part.
     */
    public String getCompanyName() { return companyName; }

    /**
     * @param companyName The new name for the company that produced the part.
     */
    public void setCompanyName(String companyName) {
        if (companyName == null)
            throw new NullPointerException("Company name cannot be null.");
        if ((companyName = companyName.trim()).length() == 0)
            throw new InvalidParameterException("Company name cannot be empty.");
        String oldCompanyName = this.companyName;
        if (companyName.equals(oldCompanyName))
            return;
        this.companyName = companyName;
        propertyChangeSupport.firePropertyChange(PROP_COMPANYNAME, oldCompanyName, companyName);
    }

    public static final String PROP_COMPANYNAME = "companyName";
}
