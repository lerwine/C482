package c482lte;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Inventory;

/**
 * Application class for Inventory Management System.
 * 
 * @author Leonard T. Erwine
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
        model.Product product = new model.Product(-1, "Centipede", 25.78, 300, 50, 300);
        Inventory.addProduct(product);
        model.Part centipedeHeadPart = new model.Outsourced(-1, "OEM Centipede Head", 2.99, 300, 50, 300, "God");
        product.addAssociatedPart(centipedeHeadPart);
        product.addAssociatedPart(new model.Outsourced(-1, "Bionic Trunk Segment", 532.5, 296, 10, 100, "Austin Labs"));
        product.addAssociatedPart(new model.Outsourced(-1, "OEM Centipede Leg", 0.10, 30000, 5000, 30000, "God"));
        model.Part nerveIntegratingMandible = new model.Outsourced(-1, "Nervous System Integration Mandible", 5999.99, 500, 12, 400, "Spies R Us");
        product.addAssociatedPart(nerveIntegratingMandible);
        model.Part homeBaseCommunicationAntennae = new model.InHouse(-1, "Home Base Communication Antennae", 1199.49, 600, 300, 1400, 2);
        product.addAssociatedPart(homeBaseCommunicationAntennae);
        
        product = new model.Product(-1, "Scorpion", 50.27, 5, 12, 100);
        Inventory.addProduct(product);
        model.Part scoropionHeadPart = new model.Outsourced(-1, "OEM Scorpion Head", 16.75, 5, 12, 100, "God");
        product.addAssociatedPart(scoropionHeadPart);
        product.addAssociatedPart(new model.Outsourced(-1, "OEM Scorpion Thorax", 2.75, 5, 12, 100, "God"));
        model.Part zombificationStinger = new model.Outsourced(-1, "Zombie Virus Injection stinger", 10445.76, 12, 1, 50, "Spies R Us");
        product.addAssociatedPart(zombificationStinger);
        model.Part bionicMediumLeg = new model.Outsourced(-1, "Bionic Arthropod Leg", 2593.5, 84, 10, 50, "Austin Labs");
        product.addAssociatedPart(bionicMediumLeg);
        product.addAssociatedPart(homeBaseCommunicationAntennae);
        
        product = new model.Product(-1, "Self-driving i-spyPhone", 200000.00, 19, 15, 200);
        Inventory.addProduct(product);
        product.addAssociatedPart(nerveIntegratingMandible);
        product.addAssociatedPart(zombificationStinger);
        product.addAssociatedPart(bionicMediumLeg);
        product.addAssociatedPart(homeBaseCommunicationAntennae);
        
        launch(args);
    }
    
}
