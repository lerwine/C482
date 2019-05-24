/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author lerwi
 */
public class EditPartController implements Initializable {
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

    private boolean saveChanges = false;
    private int modelId = -1;
    
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
            model.InHouse inHouse = (model.InHouse)part;
            companyOrMachineLabel.setText("Machine ID");
            companyOrMachineTextField.setText(String.valueOf(inHouse.getMachineId()));
        } else {
            model.Outsourced outsourced = (model.Outsourced)part;
            companyOrMachineLabel.setText("Company Name");
            companyOrMachineTextField.setText(outsourced.getCompanyName());
        }
    }
    
    public model.Part getModel() {
        if (!this.saveChanges)
            return null;
        if (inHouseRadioButton.isSelected())
            return new model.InHouse(modelId, nameTextField.getText().trim(), Double.parseDouble(priceTextField.getText().trim()),
                Integer.parseInt(inventoryTextField.getText().trim()), Integer.parseInt(minTextField.getText().trim()),
                Integer.parseInt(maxTextField.getText().trim()), Integer.parseInt(companyOrMachineTextField.getText().trim()));
        return new model.Outsourced(modelId, nameTextField.getText().trim(), Double.parseDouble(priceTextField.getText().trim()),
                Integer.parseInt(inventoryTextField.getText().trim()), Integer.parseInt(minTextField.getText().trim()),
                Integer.parseInt(maxTextField.getText().trim()), companyOrMachineTextField.getText().trim());
    }
    
    @FXML
    void onCancelButtonClick(ActionEvent event) {
        saveChanges = false;
        ((Button)event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    void updateCompanyOrMachineLabel(ActionEvent event) {
        this.companyOrMachineLabel.setText((this.inHouseRadioButton.isSelected()) ? "Company Name" : "Machine ID");
    }
    
    @FXML
    void onSaveButtonClick(ActionEvent event) {
        saveChanges = true;
        ((Button)event.getSource()).getScene().getWindow().hide();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
}
