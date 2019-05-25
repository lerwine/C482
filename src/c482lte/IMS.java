/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c482lte;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Inventory;

/**
 *
 * @author lerwi
 */
public class IMS extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        model.Product product = new model.Product(-1, "Prod A", 0.0, 5, 5, 50);
        Inventory.addProduct(product);
        model.Part part = new model.InHouse(-1, "IH A", 0.5, 0, 12, 2000, 55);
        product.addAssociatedPart(part);
        part = new model.Outsourced(-1, "OS A", 5.5, 100, 10, 100, "Company A");
        product.addAssociatedPart(part);
        launch(args);
    }
    
}
