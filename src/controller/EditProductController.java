package controller;

import java.awt.event.InputMethodEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author lerwi
 */
public class EditProductController implements Initializable {
    private int modelId = -1;
    private ObservableList<model.Part> availableParts = FXCollections.observableArrayList();
    private boolean showingSearchResults = false;
    
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
    private Button partsSearchButton;
    
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
    private Label noMatchesLabel;
    
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
    void cancelButtonClick(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "All changes will be lost. Are you sure you want to cancel?", ButtonType.YES, ButtonType.NO);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Cancel changes");
        alert.setHeaderText("Confirm Cancel");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES)
        {
            try {
                MainScreenController.changeToMainScreen(((Button)event.getSource()).getScene(), getClass());
            } catch (IOException ex) {
                Logger.getLogger(EditProductController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    void onSearchPartClick(ActionEvent event) {
        if (showingSearchResults) {
            unselectedPartsTableView.setItems(availableParts);
            showingSearchResults = false;
            unselectedPartsTableView.setVisible(true);
            noMatchesLabel.setVisible(false);
            searchTextField.setDisable(false);
            partsSearchButton.setText("Search");
            return;
        }
        String searchText = searchTextField.getText().trim().toLowerCase();
        if (searchText.length() == 0) {
            model.Inventory.showWarningAlert("No text", "Enter a name to search for...");
            return;
        }
        ObservableList<model.Part> searchResults = FXCollections.observableArrayList();
        for (model.Part part : availableParts) {
            if (part.getName().toLowerCase().contains(searchText))
                searchResults.add(part);
        }
        if (searchResults.isEmpty()) {
            noMatchesLabel.setVisible(true);
            unselectedPartsTableView.setVisible(false);
        } else {
            noMatchesLabel.setVisible(false);
            unselectedPartsTableView.setVisible(true);
            unselectedPartsTableView.setItems(searchResults);
        }
        searchTextField.setDisable(true);
        showingSearchResults = true;
        partsSearchButton.setText("Show All");
    }

    @FXML
    void saveButtonClick(ActionEvent event) {
        model.ValidationInfo errors = new model.ValidationInfo();
        
        ObservableList<model.Part> selectedParts = selectedPartsPartsTableView.getItems();
        if (selectedParts.isEmpty())
            errors.add("Selected Parts", "Products must have at least one part.");
        
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
                else {
                    double partSum = selectedParts.stream().mapToDouble(p -> p.getPrice()).sum();
                    if (partSum < price)
                        errors.add(priceTextField.accessibleTextProperty().getValue(), "Value cannot be less than the total price of all parts (" +
                                String.valueOf(partSum));
                }
            } catch (NumberFormatException e) {
                errors.add(priceTextField.accessibleTextProperty().getValue(), "Invalid number value");
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
        
        // If this returns true, then errors were presented in a popup.
        if (errors.showWarningAlert("Invalid product properties"))
            return;
        
        model.Product product = (modelId < 0) ? null : model.Inventory.lookupProduct(modelId);
        if (product == null) {
            product = new model.Product(modelId, nameTextField.getText(), price, stock, min, max);
            for (model.Part part : selectedParts)
                product.addAssociatedPart(part);
            model.Inventory.addProduct(product);
        } else {
            product.setName(nameTextField.getText());
            product.setPrice(price);
            product.setStock(stock);
            product.setMinMax(min, max);
            product.setAllAssociatedParts(selectedParts);
        }
        
        try {
            MainScreenController.changeToMainScreen(((Button)event.getSource()).getScene(), getClass());
        } catch (IOException ex) {
            Logger.getLogger(EditProductController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void addPartButtonClick(ActionEvent event) {
        model.Part part = unselectedPartsTableView.getSelectionModel().getSelectedItem();
        if (part == null) {
            model.Inventory.showWarningAlert("No selection", "You must select a part before it can be added.");
            return;
        }
        ObservableList<model.Part> items = unselectedPartsTableView.getItems();
        items.remove(part);
        selectedPartsPartsTableView.getItems().add(part);
        if (showingSearchResults)
            availableParts.remove(part);
    }
    
    @FXML
    void deletePartButtonClick(ActionEvent event) {
        model.Part part = selectedPartsPartsTableView.getSelectionModel().getSelectedItem();
        if (part == null) {
            model.Inventory.showWarningAlert("No selection", "You must select a part before it can be deleted.");
            return;
        }
        ObservableList<model.Part> items = selectedPartsPartsTableView.getItems();
        if (items.size() == 1)
        if (part == null) {
            model.Inventory.showWarningAlert("Minimum part constraint", "Products must have at least one part.");
            return;
        }
        items.remove(part);
        unselectedPartsTableView.getItems().add(part);
        if (showingSearchResults)
            availableParts.add(part);
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sInventoryLevelTableColumn.setCellValueFactory(new PropertyValueFactory<>(model.Part.PROP_STOCK));
        sPartIdTableColumn.setCellValueFactory(new PropertyValueFactory<>(model.Part.PROP_ID));
        sPartNameTableColumn.setCellValueFactory(new PropertyValueFactory<>(model.Part.PROP_NAME));
        sPricePerUnitTableColumn.setCellValueFactory(new PropertyValueFactory<>(model.Part.PROP_PRICE));
        uInventoryLevelTableColumn.setCellValueFactory(new PropertyValueFactory<>(model.Part.PROP_STOCK));
        uPartIdTableColumn.setCellValueFactory(new PropertyValueFactory<>(model.Part.PROP_ID));
        uPartNameTableColumn.setCellValueFactory(new PropertyValueFactory<>(model.Part.PROP_NAME));
        uPricePerUnitTableColumn.setCellValueFactory(new PropertyValueFactory<>(model.Part.PROP_PRICE));
        for (model.Part part : model.Inventory.getAllParts())
            availableParts.add(part);
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
        availableParts.clear();
        ObservableList<model.Part> selectedParts = FXCollections.observableArrayList();
        for (model.Part part : model.Inventory.getAllParts()) {
            if (product.containsAssociatedPart(part))
                selectedParts.add(part);
            else
                availableParts.add(part);
        }
        unselectedPartsTableView.setItems(availableParts);
        selectedPartsPartsTableView.setItems(selectedParts);
    }
}
