/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Inventory;

/**
 * FXML Controller class
 *
 * @author lerwi
 */
public class EditProductController implements Initializable {
    @FXML
    private Label editProductLabel;

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
    private TextField searchTextField;

    @FXML
    private TableView<model.Part> unselectedPartsTableView;

    @FXML
    private TableColumn<model.Part, Integer> uPartIdTableColumn;

    @FXML
    private TableColumn<model.Part, String> uPartNameTableColumn;

    @FXML
    private TableColumn<model.Part, Integer> uInventoryLevelTableColumn;

    @FXML
    private TableColumn<model.Part, Double> uPricePerUnitTableColumn;

    @FXML
    private Button addPartButton;

    @FXML
    private TableView<model.Part> selectedPartsPartsTableView;

    @FXML
    private TableColumn<model.Part, Integer> sPartIdTableColumn;

    @FXML
    private TableColumn<model.Part, String> sPartNameTableColumn;

    @FXML
    private TableColumn<model.Part, Integer> sInventoryLevelTableColumn;

    @FXML
    private TableColumn<model.Part, Double> sPricePerUnitTableColumn;

    @FXML
    private Button deletePartButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    void cancelButtonClick(ActionEvent event) {
        saveChanges = false;
        ((Button)event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    void onSearchPartClick(ActionEvent event) {

    }

    @FXML
    void saveButtonClick(ActionEvent event) {
        saveChanges = true;
        ((Button)event.getSource()).getScene().getWindow().hide();
    }
    
    private boolean saveChanges = false;
    private int modelId = -1;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sInventoryLevelTableColumn.setCellValueFactory(new PropertyValueFactory<>("inv"));
        sPartIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        sPartNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        sPricePerUnitTableColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        uInventoryLevelTableColumn.setCellValueFactory(new PropertyValueFactory<>("inv"));
        uPartIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        uPartNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        uPricePerUnitTableColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    }
    
    public void applyModel(model.Product product) {
        if (product == null)
            return;
        modelId = product.getId();
        editProductLabel.setText("Modify Product");
        idTextField.setText(String.valueOf(modelId = product.getId()));
        nameTextField.setText(product.getName());
        inventoryTextField.setText(String.valueOf(product.getStock()));
        priceTextField.setText(String.valueOf(product.getPrice()));
        maxTextField.setText(String.valueOf(product.getMax()));
        minTextField.setText(String.valueOf(product.getMin()));
        ObservableList<model.Part> availableParts = FXCollections.observableArrayList();
        ObservableList<model.Part> selectedParts = FXCollections.observableArrayList();
        for (model.Part part : Inventory.getAllParts()) {
            if (product.containsAssociatedPart(part))
                selectedParts.add(part);
            else
                availableParts.add(part);
        }
        unselectedPartsTableView.setItems(availableParts);
        selectedPartsPartsTableView.setItems(selectedParts);
    }
    
    public model.Product getModel() {
        if (!saveChanges)
            return null;
        
        model.Product result = new model.Product(modelId, nameTextField.getText().trim(), Double.parseDouble(priceTextField.getText().trim()),
            Integer.parseInt(inventoryTextField.getText().trim()), Integer.parseInt(minTextField.getText().trim()),
            Integer.parseInt(maxTextField.getText().trim()));
        ObservableList<model.Part> selectedParts = selectedPartsPartsTableView.getItems();
        for (model.Part part : result.getAllAssociatedParts()) {
            if (!selectedParts.contains(part))
                result.deleteAssociatedPart(part);
        }
        for (model.Part part : selectedParts) {
            if (!result.containsAssociatedPart(part))
                result.addAssociatedPart(part);
        }
        return result;
    }
}
