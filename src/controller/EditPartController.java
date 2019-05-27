/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.StageStyle;
import model.Inventory;

/**
 * FXML Controller class
 *
 * @author lerwi
 */
public class EditPartController implements Initializable {
    private int modelId = -1;
    private String lastCompanyOrMachine = "";
    
    @FXML
    private Label editPartLabel;

    @FXML
    private RadioButton inHouseRadioButton;

    @FXML
    private RadioButton outsourcedRadioButton;

    @FXML
    private TextField idTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField inventoryTextField;

    @FXML
    private TextField priceTextField;

    @FXML
    private TextField maxTextField;

    @FXML
    private TextField minTextField;

    @FXML
    private Label companyOrMachineLabel;

    @FXML
    private TextField companyOrMachineTextField;

    @FXML
    void onCancelButtonClick(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "All changes will be lost. Are you sure you want to cancel?", ButtonType.YES, ButtonType.NO);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Cancel changes");
        alert.setHeaderText("Confirm Cancel");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                MainScreenController.changeToMainScreen(((Button)event.getSource()).getScene(), getClass());
            } catch (IOException ex) {
                Logger.getLogger(EditProductController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    void updateCompanyOrMachineLabel(ActionEvent event) {
        // Swap values since the validation for the input types of this field are different, and so the user gets their original values back if they clicked the radio button by accident.
        String current = companyOrMachineTextField.getText();
        companyOrMachineTextField.setText(lastCompanyOrMachine);
        lastCompanyOrMachine = current;
        String text = (this.inHouseRadioButton.isSelected()) ? LABEL_MACHINEID : LABEL_COMPANYNAME;
        companyOrMachineLabel.setText(text);
        companyOrMachineTextField.setAccessibleText(text);
    }
    
    @FXML
    void onSaveButtonClick(ActionEvent event) {
        model.ValidationInfo errors = new model.ValidationInfo();
        model.Part part = (modelId < 0) ? null : model.Inventory.lookupPart(modelId);
        if (nameTextField.getText().trim().length() == 0)
            errors.add(nameTextField.accessibleTextProperty().getValue(), "Text cannot be empty.");
        
        double price = 0.0;
        String s = priceTextField.getText().trim();
        if (s.length() == 0)
            errors.add(priceTextField.accessibleTextProperty().getValue(), "Value cannot be empty.");
        else {
            try {
                price = Double.parseDouble(s);
                if (price < 0.0)
                    errors.add(priceTextField.accessibleTextProperty().getValue(), "Value cannot be less than zero");
            } catch (NumberFormatException e) {
                errors.add(priceTextField.accessibleTextProperty().getValue(), "Invalid number value");
            }
            if (part != null && price > part.getPrice()) {
                FilteredList<model.Product> violations = model.Inventory.getAllProducts().filtered(p ->
                        p.getAllAssociatedParts().stream().anyMatch(a -> a.getId() == modelId) &&
                                p.getAllAssociatedParts().stream().mapToDouble(a -> a.getPrice()).sum() > p.getPrice());
                if (!violations.isEmpty()) {
                    String message = "Increasing the price on this part would cause the part sum price to exceed the product price for ";
                    if (violations.size() == 1)
                        message = "1 product named \"" + violations.get(0).getName() + "\".";
                    else {
                        message = String.valueOf(violations.size()) + " products:";
                        for (model.Product p : violations)
                            message += "\n- " + p.getName();
                    }
                    errors.add(priceTextField.accessibleTextProperty().getValue(), message);
                }
            }
        }
        int stock = 0;
        s = inventoryTextField.getText().trim();
        if (s.length() == 0)
            errors.add(inventoryTextField.accessibleTextProperty().getValue(), "Value cannot be empty.");
        else {
            try {
                stock = Integer.parseInt(s);
                if (stock < 0)
                    errors.add(inventoryTextField.accessibleTextProperty().getValue(), "Value cannot be less than zero");
            } catch (NumberFormatException e) {
                errors.add(inventoryTextField.accessibleTextProperty().getValue(), "Invalid number value");
            }
        }
        int min = 0;
        s = minTextField.getText().trim();
        if (s.length() == 0)
            errors.add(minTextField.accessibleTextProperty().getValue(), "Value cannot be empty.");
        else {
            try {
                min = Integer.parseInt(s);
                if (min < 0)
                    errors.add(minTextField.accessibleTextProperty().getValue(), "Value cannot be less than zero");
            } catch (NumberFormatException e) {
                errors.add(minTextField.accessibleTextProperty().getValue(), "Invalid number value");
            }
        }
        int max = 0;
        s = maxTextField.getText().trim();
        if (s.length() == 0)
            errors.add(maxTextField.accessibleTextProperty().getValue(), "Value cannot be empty.");
        else {
            try {
                max = Integer.parseInt(s);
                if (max <= min)
                    errors.add(maxTextField.accessibleTextProperty().getValue(), "Value must be greater than the minimum inventory count.");
            } catch (NumberFormatException e) {
                errors.add(maxTextField.accessibleTextProperty().getValue(), "Invalid number value");
            }
        }
        
        int machineId = 0;
        s = companyOrMachineTextField.getText().trim();
        if (inHouseRadioButton.isSelected()) {
            if (s.length() == 0)
                errors.add(companyOrMachineTextField.accessibleTextProperty().getValue(), "Value cannot be empty.");
            else {
                try { machineId = Integer.parseInt(s); }
                catch (NumberFormatException e) {
                    errors.add(companyOrMachineTextField.accessibleTextProperty().getValue(), "Invalid number value");
                }
            }
        } else if (s.length() == 0)
            errors.add(companyOrMachineTextField.accessibleTextProperty().getValue(), "Value cannot be empty.");
        
        // If this returns true, then errors were presented in a popup.
        if (errors.showWarningAlert("Invalid part properties"))
            return;
        
        if (part == null) {
            part = (inHouseRadioButton.isSelected()) ? new model.InHouse(modelId, nameTextField.getText(), price, stock, min, max, machineId) :
                    new model.Outsourced(modelId, nameTextField.getText(), price, stock, min, max, s);
            model.Inventory.addPart(part);
        } else {
            if (inHouseRadioButton.isSelected()) {
                if (part instanceof model.Outsourced) {
                    model.Inventory.updatePart(model.Inventory.getAllParts().indexOf(part),
                        new model.InHouse(modelId, nameTextField.getText(), price, stock, min, max, machineId));
                    try {
                        MainScreenController.changeToMainScreen(((Button)event.getSource()).getScene(), getClass());
                    } catch (IOException ex) {
                        Logger.getLogger(EditProductController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return;
                }
                ((model.InHouse)part).setMachineId(machineId);
            } else {
                if (part instanceof model.InHouse) {
                    model.Inventory.updatePart(model.Inventory.getAllParts().indexOf(part),
                        new model.Outsourced(modelId, nameTextField.getText(), price, stock, min, max, s));
                    try {
                        MainScreenController.changeToMainScreen(((Button)event.getSource()).getScene(), getClass());
                    } catch (IOException ex) {
                        Logger.getLogger(EditProductController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return;
                }
                ((model.Outsourced)part).setCompanyName(s);
            }
            part.setName(nameTextField.getText());
            part.setPrice(price);
            part.setStock(stock);
            part.setMinMax(min, max);
        }
        
        try {
            MainScreenController.changeToMainScreen(((Button)event.getSource()).getScene(), getClass());
        } catch (IOException ex) {
            Logger.getLogger(EditProductController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inHouseRadioButton.selectedProperty().setValue(true);
        outsourcedRadioButton.selectedProperty().setValue(false);
        companyOrMachineLabel.setText(LABEL_MACHINEID);
        companyOrMachineTextField.setAccessibleText(LABEL_MACHINEID);
    }
  
    public void applyModel(model.Part part) {
        if (part == null)
            return;
        
        editPartLabel.setText("Modify Part");
        idTextField.setText(String.valueOf(modelId = part.getId()));
        nameTextField.setText(part.getName());
        inventoryTextField.setText(String.valueOf(part.getStock()));
        priceTextField.setText(String.valueOf(part.getPrice()));
        maxTextField.setText(String.valueOf(part.getMax()));
        minTextField.setText(String.valueOf(part.getMin()));
        if (part instanceof model.InHouse) {
            inHouseRadioButton.selectedProperty().setValue(true);
            outsourcedRadioButton.selectedProperty().setValue(false);
            model.InHouse inHouse = (model.InHouse)part;
            companyOrMachineLabel.setText(LABEL_MACHINEID);
            companyOrMachineTextField.setAccessibleText(LABEL_MACHINEID);
            companyOrMachineTextField.setText(String.valueOf(inHouse.getMachineId()));
        } else {
            outsourcedRadioButton.selectedProperty().setValue(true);
            inHouseRadioButton.selectedProperty().setValue(false);
            model.Outsourced outsourced = (model.Outsourced)part;
            companyOrMachineLabel.setText(LABEL_COMPANYNAME);
            companyOrMachineTextField.setAccessibleText(LABEL_COMPANYNAME);
            companyOrMachineTextField.setText(outsourced.getCompanyName());
        }
    }
    
    private static final String LABEL_MACHINEID = "Machine ID";
    private static final String LABEL_COMPANYNAME = "Company Name";  
}
