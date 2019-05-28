package model;

import java.security.InvalidParameterException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Maintains the in-memory inventory lists.
 * 
 * @author Leonard T. Erwine
 */

public class Inventory {
    private static final ObservableList<Part> allParts = FXCollections.observableArrayList();
    private static final ObservableList<Product> allProducts = FXCollections.observableArrayList();
    
    /**
     * Adds a part to the in-memory inventory list.
     * 
     * @param part The new part to be added.
     * @throws NullPointerException part is null.
     */
    public static void addPart(Part part) throws NullPointerException {
        if (part == null)
            throw new NullPointerException();
        if (allParts.contains(part))
            return;
        // Make sure part has a valid unique id
        part.ensureId();
        allParts.add(part);
    }
    
    /**
     * Adds a product to the in-memory inventory list.
     * 
     * @param product The new product to be added.
     * @throws NullPointerException product is null.
     */
    public static void addProduct(Product product) throws NullPointerException {
        if (product == null)
            throw new NullPointerException();
        if (allProducts.contains(product))
            return;
        // Make sure product has a valid unique id
        product.ensureId();
        allProducts.add(product);
    }
    
    /**
     * Gets the Part whose unique identifier matches a specified value.
     * 
     * @param partId The unique identifier to search for.
     * @return The Part where getId() matches the specified value or null if no matches were found.
     */
    public static Part lookupPart(int partId) { return (Part)ModelHelper.lookupPart(allParts, partId); }
    
    /**
     * Gets the Product whose unique identifier matches a specified value.
     * 
     * @param productId The unique identifier to search for.
     * @return The Product where getId() matches the specified value or null if no matches were found.
     */
    public static Product lookupProduct(int productId) { return (Product)ModelHelper.lookupProduct(allProducts, productId); }
    
    /**
     * Gets the Part whose name matches a specified value (case-insensitive).
     * 
     * @param partName The part name to search for.
     * @return The Part where getName() matches the specified string or null if no matches were found.
     */
    public static Part lookupPart(String partName) {
        if (partName == null || (partName = partName.trim()).length() == 0)
            return null;
        for (Part part : allParts) {
            if (part.getName().equalsIgnoreCase(partName))
                return part;
        }
        return null;
    }
    
    /**
     * Gets the Product whose name matches a specified value (case-insensitive).
     * 
     * @param productName The product name to search for.
     * @return The Product where getName() matches the specified string or null if no matches were found.
     */
    public static Product lookupProduct(String productName) {
        if (productName == null || (productName = productName.trim()).length() == 0)
            return null;
        for (Product product : allProducts) {
            if (product.getName().equalsIgnoreCase(productName))
                return product;
        }
        return null;
    }
    
    /**
     * Updates a part at a specific index, with the values of a Part object.
     * 
     * @param index - The index of the part to update.
     * @param part - Contains the values to apply to the part.
     */
    public static void updatePart(int index, Part part) throws NullPointerException, InvalidParameterException {
        if (part == null)
            throw new NullPointerException();
        int currentIndex = allParts.indexOf(part);
        if (currentIndex == index)
            return;
        Part existing = allParts.get(index);
        int id = existing.getId();
        if (id != part.getId())
            throw new InvalidParameterException("Unique identifier of part does not match the unique identifier at the specified index.");
        
        if (part instanceof InHouse) {
            if (existing instanceof Outsourced) {
                if (currentIndex > -1)
                    part = new InHouse(id, part.getName(), part.getPrice(), part.getStock(), part.getMin(), part.getMax(), ((InHouse)part).getMachineId());
                allParts.set(index, part);
                return;
            }
            ((InHouse)existing).setMachineId(((InHouse)part).getMachineId());
        } else if (existing instanceof InHouse) {
            if (currentIndex > -1)
                part = new Outsourced(id, part.getName(), part.getPrice(), part.getStock(), part.getMin(), part.getMax(), ((Outsourced)part).getCompanyName());
            allParts.set(index, part);
            return;
        } else
            ((Outsourced)existing).setCompanyName(((Outsourced)part).getCompanyName());
        existing.setName(part.getName());
        existing.setPrice(part.getPrice());
        existing.setStock(part.getStock());
        existing.setMinMax(part.getMin(), part.getMax());
    }
    
    /**
     *
     * @param index
     * @param product
     */
    public static void updateProduct(int index, Product product) {
        if (product == null)
            throw new NullPointerException();
        int currentIndex = allProducts.indexOf(product);
        if (currentIndex == index)
            return;
        Product existing = allProducts.get(index);
        if (existing.getId() != product.getId())
            throw new InvalidParameterException("Unique identifier of product does not match the unique identifier at the specified index.");
        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setStock(product.getStock());
        existing.setMinMax(product.getMin(), product.getMax());
        existing.setAllAssociatedParts(product.getAllAssociatedParts());
    }
    
    /**
     *
     * @param part
     */
    public static void deletePart(Part part) {
        if (part == null || !allParts.contains(part))
            return;
        allProducts.forEach((Product p) -> p.deleteAssociatedPart(part));
        allParts.remove(part);
    }
    
    /**
     *
     * @param product
     */
    public static void deleteProduct(Product product) {
        if (product != null && allProducts.contains(product))
            allProducts.remove(product);
    }
    
    /**
     *
     * @return
     */
    public static ObservableList<Part> getAllParts() { return allParts; }
    
    /**
     *
     * @return
     */
    public static ObservableList<Product> getAllProducts() { return allProducts; }
}
