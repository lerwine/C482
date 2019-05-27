/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Inventory;

/**
 * Controller class for the Main Screen
 * @author Leonard T. Erwine
 */
public class MainScreenController implements Initializable {
    private boolean showingProductSearchResults = false;
    private boolean showingPartSearchResults = false;
    
    /**
     * Text field for searching parts
     */
    @FXML
    private TextField partsSearchTextField;

    @FXML
    private TableView<model.Part> partsTableView;

    @FXML
    private TableColumn<model.Part, Integer> partIdTableColumn;

    @FXML
    private TableColumn<model.Part, String> partNameTableColumn;

    @FXML
    private TableColumn<model.Part, Integer> partInventoryLevelTableColumn;

    @FXML
    private TableColumn<model.Part, Double> partPriceTableColumn;

    @FXML
    private TextField productsSearchTextField;

    @FXML
    private TableView<model.Product> productsTableView;

    @FXML
    private TableColumn<?, ?> productIdTableColumn;

    @FXML
    private TableColumn<?, ?> productNameTableColumn;

    @FXML
    private TableColumn<?, ?> productInventoryLevelTableColumn;

    @FXML
    private TableColumn<?, ?> productPriceTableColumn;

    @FXML
    private Label noPartMatchesLabel;

    @FXML
    private Button partsSearchButton;

    @FXML
    private Button productsSearchButton;

    @FXML
    private Label noProductMatchesLabel;

    @FXML
    void onAddPartButtonClick(ActionEvent event) throws MalformedURLException, IOException {
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/EditPartScreen.fxml"))));
        stage.show();
    }

    @FXML
    void onAddProductButtonClick(ActionEvent event) throws MalformedURLException, IOException {
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/EditProductScreen.fxml"))));
        stage.show();
    }

    @FXML
    void onDeletePartButtonClick(ActionEvent event) {
        model.Part part = partsTableView.getSelectionModel().getSelectedItem();
        if (part == null) {
            model.Inventory.showWarningAlert("No selection", "You must select a part before it can be deleted");
            return;
        }
        
        FilteredList<model.Product> violations = model.Inventory.getAllProducts().filtered(p -> {
            ObservableList<model.Part> pl = p.getAllAssociatedParts();
            return pl.size() == 1 && pl.contains(part);
        });
        if (!violations.isEmpty()) {
            String message = "Removing this part would remove the last part from ";
            if (violations.size() == 1)
                message += "1 product named \"" + violations.get(0).getName() + "\"";
            else {
                message += String.valueOf(violations.size()) + " products:";
                for (model.Product p : violations)
                    message += "\n- " + p.getName();
            }
            model.Inventory.showWarningAlert("Minimum part Constraint Error", message);
            return;
        }
        int count = model.Inventory.getAllProducts().filtered(p -> p.getAllAssociatedParts().contains(part)).size();
        String contentText = "This action cannot be undone!\n\nAre you sure you want to delete this part?";
        if (count > 0)
            contentText = ((count == 1) ? "1 product references this part, and it will be deleted from that one as well" : String.valueOf(count) + " products reference this part, and it will be deleted from those as well") + ".\r\n" + contentText;
        Alert alert = new Alert(Alert.AlertType.WARNING, contentText, ButtonType.YES, ButtonType.NO);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Part \"" + part.getName() + "\"");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES)
            model.Inventory.deletePart(part);
    }

    @FXML
    void onDeleteProductButtonClick(ActionEvent event) {
        model.Product product = productsTableView.getSelectionModel().getSelectedItem();
        if (product == null) {
            model.Inventory.showWarningAlert("No selection", "You must select a product before it can be deleted");
            return;
        }
        
        ObservableList<model.Product> allProducts = model.Inventory.getAllProducts();
        ArrayList<model.Part> toBeOrphaned = new ArrayList<model.Part>();
        for (model.Part part: product.getAllAssociatedParts()) {
            if (allProducts.filtered(p -> p.getAllAssociatedParts().contains(part)).size() < 2)
                toBeOrphaned.add(part);
        }
        Alert alert = new Alert(Alert.AlertType.WARNING, "This action cannot be undone!\n\nAre you sure you want to delete this product?",
                ButtonType.YES, ButtonType.NO);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Product \"" + product.getName() + "\"");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            if (toBeOrphaned.size() > 0) {
                alert = new Alert(Alert.AlertType.CONFIRMATION, ((toBeOrphaned.size() == 1) ? "1 part" : String.valueOf(toBeOrphaned.size()) + " parts") +
                        "will not belong to any product after this product is deleted.\r\nDo you want to delete those parts as well?",
                        ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                alert.initStyle(StageStyle.UTILITY);
                alert.setTitle("Confirm Delete");
                alert.setHeaderText("Delete Product \"" + product.getName() + "\"");
                result = alert.showAndWait();
                if (!(result.isPresent() && result.get() != ButtonType.CANCEL))
                    return;
                if (result.get() == ButtonType.YES)
                    for (model.Part part: toBeOrphaned)
                        model.Inventory.deletePart(part);
            }
            model.Inventory.deleteProduct(product);
        }
    }

    @FXML
    void onExitClick(ActionEvent event) { ((Button)event.getSource()).getScene().getWindow().hide(); }

    @FXML
    void onModifyPartButtonClick(ActionEvent event) throws MalformedURLException, IOException {
        model.Part sourcePart = partsTableView.getSelectionModel().getSelectedItem();
        if (sourcePart == null) {
            model.Inventory.showWarningAlert("No selection", "You must select a part before it can be edited");
            return;
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/EditPartScreen.fxml"));
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        EditPartController controller = loader.getController();
        controller.applyModel(sourcePart);
        stage.show();
    }

    @FXML
    void onModifyProductButtonClick(ActionEvent event) throws MalformedURLException, IOException {
        model.Product sourceProduct = productsTableView.getSelectionModel().getSelectedItem();
        if (sourceProduct == null) {
            model.Inventory.showWarningAlert("No selection", "You must select a part before it can be edited");
            return;
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/EditProductScreen.fxml"));
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        EditProductController controller = loader.getController();
        controller.applyModel(sourceProduct);
        stage.show();
    }

    public static void changeToMainScreen(Scene scene, Class<?> c) throws MalformedURLException, IOException {
        Stage stage = (Stage)scene.getWindow();
        stage.setScene(new Scene(FXMLLoader.load(c.getResource("/view/MainScreen.fxml"))));
        stage.show();
    }
    
    @FXML
    void onPartsSearchButtonClick(ActionEvent event) {
        if (showingPartSearchResults) {
            partsTableView.setItems(model.Inventory.getAllParts());
            noPartMatchesLabel.setVisible(false);
            partsTableView.setVisible(true);
            partsSearchButton.setText("Search");
            showingPartSearchResults = false;
            partsSearchTextField.setDisable(false);
            partsSearchTextField.setText("");
            return;
        }
        String searchText = partsSearchTextField.getText().trim().toLowerCase();
        if (searchText.length() == 0) {
            model.Inventory.showWarningAlert("No text", "Enter a name to search for...");
            return;
        }
        ObservableList<model.Part> searchResults = FXCollections.observableArrayList();
        for (model.Part part : Inventory.getAllParts()) {
            if (part.getName().toLowerCase().contains(searchText))
                searchResults.add(part);
        }
        if (searchResults.isEmpty()) {
            noPartMatchesLabel.setVisible(true);
            partsTableView.setVisible(false);
        } else {
            noPartMatchesLabel.setVisible(false);
            partsTableView.setVisible(true);
            partsTableView.setItems(searchResults);
        }
        partsSearchTextField.setDisable(true);
        partsSearchButton.setText("Show All");
        showingPartSearchResults = true;
    }

    @FXML
    void onProductsSearchButtonClick(ActionEvent event) {
        if (showingProductSearchResults) {
            productsTableView.setItems(model.Inventory.getAllProducts());
            noProductMatchesLabel.setVisible(false);
            productsTableView.setVisible(true);
            productsSearchButton.setText("Search");
            showingProductSearchResults = false;
            productsSearchTextField.setDisable(false);
            productsSearchTextField.setText("");
            return;
        }
        String searchText = productsSearchTextField.getText().trim().toLowerCase();
        if (searchText.length() == 0) {
            model.Inventory.showWarningAlert("No text", "Enter a name to search for...");
            return;
        }
        ObservableList<model.Product> searchResults = FXCollections.observableArrayList();
        for (model.Product product : Inventory.getAllProducts()) {
            if (product.getName().toLowerCase().contains(searchText))
                searchResults.add(product);
        }
        if (searchResults.isEmpty()) {
            noProductMatchesLabel.setVisible(true);
            productsTableView.setVisible(false);
        } else {
            noProductMatchesLabel.setVisible(false);
            productsTableView.setVisible(true);
            productsTableView.setItems(searchResults);
        }
        productsSearchTextField.setDisable(true);
        productsSearchButton.setText("Show All");
        showingProductSearchResults = true;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        partIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        partInventoryLevelTableColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        partPriceTableColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        productIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productInventoryLevelTableColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        productPriceTableColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        partsTableView.setItems(model.Inventory.getAllParts());
        productsTableView.setItems(model.Inventory.getAllProducts());
    }
}
