/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Inventory;

/**
 * Controller class for the Main Screen
 * @author Leonard T. Erwine
 */
public class MainScreenController implements Initializable {
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
    private Button modifyPartButton;

    @FXML
    private Button deletePartButton;

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
    private Button modifyProductButton;

    @FXML
    private Button deleteProductButton;

    @FXML
    void onAddPartButtonClick(ActionEvent event) throws MalformedURLException, IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/EditPartController.fxml"));
        EditPartController controller = loader.getController();
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.showAndWait();
        model.Part part = controller.getModel();
        if (part != null)
            Inventory.addPart(part);
    }

    @FXML
    void onAddProductButtonClick(ActionEvent event) throws MalformedURLException, IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/EditProductController.fxml"));
        EditProductController controller = loader.getController();
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.showAndWait();
        model.Product product = controller.getModel();
        if (product != null)
            Inventory.addProduct(product);
    }

    @FXML
    void onDeletePartButtonClick(ActionEvent event) {
        model.Part part = partsTableView.getSelectionModel().getSelectedItem();
        if (part == null) {
            Inventory.showWarningAlert("No selection", "You must select a part before it can be deleted");
            return;
        }
        int count = Inventory.getAllProducts().filtered(p -> p.getAllAssociatedParts().contains(part)).size();
        String contentText = "This action cannot be undone!\n\nAre you sure you want to delete this part?";
        if (count > 0)
            contentText = ((count == 1) ? "1 product references this part, and it will be deleted from that one as well" : String.valueOf(count) + " products reference this part, and it will be deleted from those as well") + ".\r\n" + contentText;
        Alert alert = new Alert(Alert.AlertType.ERROR, contentText, ButtonType.YES, ButtonType.NO);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Part \"" + part.getName() + "\"");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES)
            Inventory.deletePart(part);
    }

    @FXML
    void onDeleteProductButtonClick(ActionEvent event) {
        model.Product product = productsTableView.getSelectionModel().getSelectedItem();
        if (product == null) {
            Inventory.showWarningAlert("No selection", "You must select a product before it can be deleted");
            return;
        }
        
        ObservableList<model.Product> allProducts = Inventory.getAllProducts();
        ArrayList<model.Part> toBeOrphaned = new ArrayList<model.Part>();
        for (model.Part part: product.getAllAssociatedParts()) {
            if (allProducts.filtered(p -> p.getAllAssociatedParts().contains(part)).size() < 2)
                toBeOrphaned.add(part);
        }
        Alert alert = new Alert(Alert.AlertType.WARNING, "This action cannot be undone!\n\nAre you sure you want to delete this product?", ButtonType.YES, ButtonType.NO);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Product \"" + product.getName() + "\"");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            if (toBeOrphaned.size() > 0) {
                alert = new Alert(Alert.AlertType.CONFIRMATION, ((toBeOrphaned.size() == 1) ? "1 part" : String.valueOf(toBeOrphaned.size()) + " parts") + "will not belong to any product after this product is deleted.\r\nDo you want to delete those parts as well?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                alert.initStyle(StageStyle.UTILITY);
                alert.setTitle("Confirm Delete");
                alert.setHeaderText("Delete Product \"" + product.getName() + "\"");
                result = alert.showAndWait();
                if (!(result.isPresent() && result.get() != ButtonType.CANCEL))
                    return;
                if (result.get() == ButtonType.YES)
                    for (model.Part part: toBeOrphaned)
                        Inventory.deletePart(part);
            }
            Inventory.deleteProduct(product);
        }
    }

    @FXML
    void onExitClick(ActionEvent event) { ((Button)event.getSource()).getScene().getWindow().hide(); }

    @FXML
    void onModifyPartButtonClick(ActionEvent event) throws MalformedURLException, IOException {
        model.Part sourcePart = partsTableView.getSelectionModel().getSelectedItem();
        if (sourcePart == null) {
            Inventory.showWarningAlert("No selection", "You must select a part before it can be edited");
            return;
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/EditPartController.fxml"));
        EditPartController controller = loader.getController();
        controller.applyModel(sourcePart);
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.showAndWait();
        model.Part changedPart = controller.getModel();
        if (changedPart != null)
            Inventory.updatePart(Inventory.getAllParts().indexOf(sourcePart), changedPart);
    }

    @FXML
    void onModifyProductButtonClick(ActionEvent event) throws MalformedURLException, IOException {
        model.Product sourceProduct = productsTableView.getSelectionModel().getSelectedItem();
        if (sourceProduct == null) {
            Inventory.showWarningAlert("No selection", "You must select a part before it can be edited");
            return;
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/EditProductController.fxml"));
        EditProductController controller = loader.getController();
        controller.applyModel(sourceProduct);
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.showAndWait();
        model.Product changedProduct = controller.getModel();
        if (changedProduct != null)
            Inventory.updateProduct(Inventory.getAllProducts().indexOf(sourceProduct), changedProduct);
    }

    @FXML
    void onPartsSearchButtonClick(ActionEvent event) {
        String searchText = partsSearchTextField.getText().trim();
        if (searchText.length() == 0) {
            Inventory.showWarningAlert("No text", "Enter a name to search for...");
            return;
        }
        model.Part part = Inventory.lookupPart(searchText);
        if (part == null)
            Inventory.showWarningAlert("Not found", "No part with a matching name was found.");
        else
            partsTableView.getSelectionModel().select(part);
    }

    @FXML
    void onProductsSearchButtonClick(ActionEvent event) {
        String searchText = productsSearchTextField.getText().trim();
        if (searchText.length() == 0) {
            Inventory.showWarningAlert("No text", "Enter a name to search for...");
            return;
        }
        model.Product product = Inventory.lookupProduct(searchText);
        if (product == null)
            Inventory.showWarningAlert("Not found", "No product with a matching name was found.");
        else
            productsTableView.getSelectionModel().select(product);
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
    }
}
