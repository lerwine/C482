/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author lerwi
 */
public class ValidationInfo {
    private ObservableList<String> fieldNames = FXCollections.observableArrayList();
    private ObservableList<String> messages = FXCollections.observableArrayList();
    
    public int size() { return fieldNames.size(); }
    
    public void add(String fieldName, String message) {
        fieldNames.add(fieldName);
        messages.add(message);
    }
    
    public boolean showWarningAlert(String headingText) {
        if (fieldNames.isEmpty())
            return false;
        
        String message = fieldNames.get(0) + ": " + messages.get(0);
        for (int i = 0; i < fieldNames.size(); i++)
            message = message + "\n" + fieldNames.get(i) + ": " + messages.get(i);
        Inventory.showWarningAlert(headingText, message);
        return true;
    }
}
