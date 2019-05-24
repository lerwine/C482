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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private TableView<?> unselectedPartsTableView;

    @FXML
    private TableColumn<?, ?> uPartIdTableColumn;

    @FXML
    private TableColumn<?, ?> uPartNameTableColumn;

    @FXML
    private TableColumn<?, ?> uInventoryLevelTableColumn;

    @FXML
    private TableColumn<?, ?> uPricePerUnitTableColumn;

    @FXML
    private Button addPartButton;

    @FXML
    private TableView<?> selectedPartsPartsTableView;

    @FXML
    private TableColumn<?, ?> sPartIdTableColumn;

    @FXML
    private TableColumn<?, ?> sPartNameTableColumn;

    @FXML
    private TableColumn<?, ?> sInventoryLevelTableColumn;

    @FXML
    private TableColumn<?, ?> sPricePerUnitTableColumn;

    @FXML
    private Button deletePartButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    void cancelButtonClick(ActionEvent event) {

    }

    @FXML
    void onSearchPartClick(ActionEvent event) {

    }

    @FXML
    void saveButtonClick(ActionEvent event) {

    }
    
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
        
    }
    
    public model.Product getModel() {
        return null;
    }
}
