/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.StageStyle;
import javax.management.openmbean.KeyAlreadyExistsException;

/**
 *
 * @author lerwi
 */

public class Inventory {
    private static final ObservableList<Part> allParts = FXCollections.observableArrayList();
    private static final ObservableList<Product> allProducts = FXCollections.observableArrayList();
    
    public static void addPart(Part part) {
        if (part == null)
            throw new NullPointerException();
        if (allParts.contains(part))
            return;
        if (part.getId() < 0 || lookupPart(part.getId()) != null) {
            int id = allParts.size();
            int alt = id;
            boolean useId = true;
            for (Part item : allParts) {
                int i = item.getId();
                if (i >= alt)
                    alt = i + 1;
                if (useId && i == id)
                    useId = false;
            }
            
            part.setId((useId) ? id : alt);
        }
        allParts.add(part);
    }
    
    public static void addProduct(Product product) {
        if (product == null)
            throw new NullPointerException();
        if (allProducts.contains(product))
            return;
        if (product.getId() < 0 || lookupPart(product.getId()) != null) {
            int id = allProducts.size();
            int alt = id;
            boolean useId = true;
            for (Product item : allProducts) {
                int i = item.getId();
                if (i >= alt)
                    alt = i + 1;
                if (useId && i == id)
                    useId = false;
            }
            
            product.setId((useId) ? id : alt);
        }
        allProducts.add(product);
    }
    
    public static IdReferenceableObject lookup(ObservableList<? extends IdReferenceableObject> source, int id) {
        for (IdReferenceableObject item : source) {
            if (item.getId() == id)
                return item;
        }
        return null;
    }
    
    public static Part lookupPart(int partId) { return (Part)lookup(allParts, partId); }
    
    public static Product lookupProduct(int productId) { return (Product)lookup(allProducts, productId); }
    
    public static Part lookupPart(String partName) {
        for (Part part : allParts) {
            if (part.getName().equalsIgnoreCase(partName))
                return part;
        }
        return null;
    }
    
    public static Product lookupProduct(String productName) {
        for (Product product : allProducts) {
            if (product.getName().equalsIgnoreCase(productName))
                return product;
        }
        return null;
    }
    
    public static void updatePart(int index, Part part) {
        
    }
    
    public static void updateProduct(int index, Product product) {
        
    }
    
    public static void deletePart(Part part) {
        
    }
    
    public static void deleteProduct(Product part) {
        
    }
    
    public static ObservableList<Part> getAllParts() { return FXCollections.unmodifiableObservableList(allParts); }
    
    public static ObservableList<Product> getAllProducts() { return FXCollections.unmodifiableObservableList(allProducts); }
    
    public static int indexOfId(ObservableList<? extends IdReferenceableObject> source, int id) {
        for (int i = 0; i < source.size(); i++) {
            if (source.get(i).getId() == id)
                return i;
        }
        return -1;
    }
    
    public static void assertValidIdChange(Part part, int newId) {
        if (part == null || !allParts.contains(part))
            return;
        for (Part item : allParts) {
            if (item.getId() == newId)
                throw new KeyAlreadyExistsException();
        }
    }
    
    public static void assertValidIdChange(Product product, int newId) {
        if (product == null || !allProducts.contains(product))
            return;
        for (Product item : allProducts) {
            if (item.getId() == newId)
                throw new KeyAlreadyExistsException();
        }
    }
    
    public static boolean containsPart(Part part) { return allParts.contains(part); }
    
    public static boolean containsProduct(Product product) { return allProducts.contains(product); }
    
    public static void showWarningAlert(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Warning");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
