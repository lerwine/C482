package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import model.ModelHelper;

/**
 * FXML Controller class for adding and modifying Parts.
 *
 * @author Leonard T. Erwine
 */
public class EditPartController implements Initializable {
    private int currentPartId = -1;
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
        Optional<ButtonType> result = ModelHelper.showConfirmationDialog("Cancel Changes", "All changes will be lost",
                "Are you sure you want to cancel?", Alert.AlertType.CONFIRMATION);
        if (result.isPresent() && result.get() == ButtonType.YES)
            MainScreenController.changeScene((Node)event.getSource(), MainScreenController.VIEW_PATH_MAINSCREEN);
    }

    @FXML
    void updateCompanyOrMachineLabel(ActionEvent event) {
        // Swap values since the validation for the input types of this field are different, and so the user gets their original values back if they clicked the radio button by accident.
        String current = companyOrMachineTextField.getText();
        companyOrMachineTextField.setText(lastCompanyOrMachine);
        lastCompanyOrMachine = current;
        String text = (this.inHouseRadioButton.isSelected()) ? LABELTEXT_MACHINEID : LABELTEXT_COMPANYNAME;
        companyOrMachineLabel.setText(text);
        companyOrMachineTextField.setAccessibleText(text);
    }
    
    @FXML
    void onSaveButtonClick(ActionEvent event) {
        // Create list to store validation errors.
        ArrayList<Pair<String, String>> errors = new ArrayList<>();
        
        // This gets the existing (unmodified) part being edited, or null if a new part is being added.
        model.Part currentPart = (currentPartId < 0) ? null : model.Inventory.lookupPart(currentPartId);
        
        // Validate that part name is not empty and trim extraneous white space.
        if (nameTextField.getText().trim().length() == 0)
            errors.add(new Pair(nameTextField.getAccessibleText(), ": Text cannot be empty."));
        
        // Attempt to parse appropriate number values for other fields.
        Optional<Double> price = ModelHelper.tryConvertToDouble(priceTextField.getText());
        Optional<Integer> min = ModelHelper.tryConvertToInteger(minTextField.getText());
        Optional<Integer> max = ModelHelper.tryConvertToInteger(maxTextField.getText());
        Optional<Integer> stock = ModelHelper.tryConvertToInteger(inventoryTextField.getText());
        
        // Validate that Min field contains an integer value that is greater than or equal to zero.
        if (min.isPresent()) {
            if (min.get() < 0)
                errors.add(new Pair(minTextField.getAccessibleText(), "Value cannot be less than zero."));
        } else if (minTextField.getText().trim().length() == 0)
            errors.add(new Pair(minTextField.getAccessibleText(), "Value cannot be empty."));
        else
            errors.add(new Pair(minTextField.getAccessibleText(), "Invalid number value."));
        
        // Validate that Max field contains an integer value that is greater than or equal to the Min field.
        if (max.isPresent()) {
            if (max.get() < 0)
                errors.add(new Pair(maxTextField.getAccessibleText(), "Value cannot be less than zero."));
            else if (min.isPresent() && min.get() > max.get())
                errors.add(new Pair(maxTextField.getAccessibleText(), "Value cannot be greater than " + minTextField.getAccessibleText() + "."));
        } else if (maxTextField.getText().trim().length() == 0)
            errors.add(new Pair(maxTextField.getAccessibleText(), "Value cannot be empty."));
        else
            errors.add(new Pair(maxTextField.getAccessibleText(), "Invalid number value."));
        
        // Validate that the Inv field contains an integer value that is not less than the Min field and not greater than the Max field.
        if (stock.isPresent()) {
            if (stock.get() < 0)
                errors.add(new Pair(inventoryTextField.getAccessibleText(), "Value cannot be less than zero."));
            else if (min.isPresent() && (!max.isPresent() || max.get() >= min.get()) && stock.get() < min.get())
                errors.add(new Pair(inventoryTextField.getAccessibleText(), "Value cannot be less than " + minTextField.getAccessibleText() + "."));
            else if (max.isPresent() && stock.get() > max.get())
                errors.add(new Pair(inventoryTextField.getAccessibleText(), "Value cannot be greater than " + maxTextField.getAccessibleText() + "."));
        } else if (inventoryTextField.getText().trim().length() == 0)
            errors.add(new Pair(inventoryTextField.getAccessibleText(), "Value cannot be empty."));
        else
            errors.add(new Pair(inventoryTextField.getAccessibleText(), "Invalid number value."));
        
        // Validate that the Price field contains a floating-point number that is not less than zero.
        if (price.isPresent()) {
            if (price.get() < 0.0)
                errors.add(new Pair(priceTextField.getAccessibleText(), "Price cannot be less than zero."));
            else if (currentPart != null && currentPartId >= 0) {
                FilteredList<model.Product> potentialPriceViolations = ModelHelper.getPotentialPriceSumViolations(currentPartId, price.get());
                if (!potentialPriceViolations.isEmpty()) {
                    if (potentialPriceViolations.size() == 1)
                        errors.add(new Pair(priceTextField.getAccessibleText(),
                                "Changing the price/cost of the current part will cause the sum of the proce/cost of associated parts for 1 product (" +
                                potentialPriceViolations.get(0).getName() + ") to exceed the price of the product itself."));
                    else
                        errors.add(new Pair(priceTextField.getAccessibleText(), 
                                "Changing the price/cost of the current part will cause the sum of the proce/cost of associated parts for " +
                                String.valueOf(potentialPriceViolations.size()) + " products (" + ModelHelper.joinStrings(potentialPriceViolations,
                                        (model.Product p) -> p.getName(), "; ") + ") to exceed the price of those products."));
                }
            }
        } else if (priceTextField.getText().trim().length() == 0)
            errors.add(new Pair(priceTextField.getAccessibleText(), "Value cannot be empty."));
        else
            errors.add(new Pair(priceTextField.getAccessibleText(), "Invalid number value."));
        
        String companyName = companyOrMachineTextField.getText();
        Optional<Integer> machineId;
        if (inHouseRadioButton.isSelected()) {
            // Validate that the Machine ID field contains a number value that is not less than zero.
            machineId = ModelHelper.tryConvertToInteger(companyName);
            if (machineId.isPresent()) {
                if (machineId.get() < 0)
                    errors.add(new Pair(companyOrMachineTextField.getAccessibleText(), "Value cannot be less than zero."));
            } else if (companyOrMachineTextField.getText().trim().length() == 0)
                errors.add(new Pair(companyOrMachineTextField.getAccessibleText(), "Value cannot be empty."));
            else
                errors.add(new Pair(companyOrMachineTextField.getAccessibleText(), "Invalid number value."));
        } else {
            machineId = Optional.empty();
            // Validate that the Company Name field is nto empty.
            if ((companyName = companyName.trim()).length() == 0)
                errors.add(new Pair(companyOrMachineTextField.getAccessibleText(), "Text cannot be empty."));
        }
        
        // If we have errors, show alert dialog and return without saving or moving away from the current screen.
        if (!errors.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initStyle(StageStyle.UTILITY);
            if (errors.size() == 1) {
                alert.setTitle("Field Value Error");
                alert.setHeaderText("Error in \"" + errors.get(0).getKey() + "\"");
                alert.setContentText(errors.get(0).getValue());
            } else {
                alert.setTitle(String.valueOf("Errors in " + errors.size()) + " Fields");
                alert.setHeaderText("Error in \"" + errors.get(0).getKey() + "\"");
                alert.setContentText(ModelHelper.joinStrings(errors, (Pair<String, String> p) -> p.getKey() + ": " + p.getValue(), "\n"));
            }
            alert.showAndWait();
            return;
        }
        
        if (currentPart == null) {
            // Create new part object and add it to the inventory.
            currentPart = (inHouseRadioButton.isSelected()) ?
                    new model.InHouse(currentPartId, nameTextField.getText(), price.get(), stock.get(), min.get(), max.get(), machineId.get()) :
                    new model.Outsourced(currentPartId, nameTextField.getText(), price.get(), stock.get(), min.get(), max.get(), companyName);
            model.Inventory.addPart(currentPart);
        } else {
            // currentPart contains the object from the inventory that has not yet been modified.
            if (inHouseRadioButton.isSelected()) {
                // If the current part from inventory is not a InHouse object, then we'll need to replace it. Otherwise, we can directly update it.
                if (currentPart instanceof model.Outsourced) {
                    model.Inventory.updatePart(model.Inventory.getAllParts().indexOf(currentPart),
                        new model.InHouse(currentPartId, nameTextField.getText(), price.get(), stock.get(), min.get(), max.get(), machineId.get()));
                    MainScreenController.changeScene((Node)event.getSource(), MainScreenController.VIEW_PATH_MAINSCREEN);
                    return;
                }
                // Update property that only applies to Inhouse type.
                ((model.InHouse)currentPart).setMachineId(machineId.get());
            } else {
                // If the current part from inventory is not an Outsourced object, then we'll need to replace it. Otherwise, we can directly update
                if (currentPart instanceof model.InHouse) {
                    model.Inventory.updatePart(model.Inventory.getAllParts().indexOf(currentPart),
                        new model.Outsourced(currentPartId, nameTextField.getText(), price.get(), stock.get(), min.get(), max.get(), companyName));
                    MainScreenController.changeScene((Node)event.getSource(), MainScreenController.VIEW_PATH_MAINSCREEN);
                    return;
                }
                // Update property that only applies to Outsourced type.
                ((model.Outsourced)currentPart).setCompanyName(companyName);
            }
            // Update remainder of properties.
            currentPart.setName(nameTextField.getText());
            currentPart.setPrice(price.get());
            currentPart.setStock(stock.get());
            currentPart.setMinMax(min.get(), max.get());
        }
        
        // Change back to the main screen.
        MainScreenController.changeScene((Node)event.getSource(), MainScreenController.VIEW_PATH_MAINSCREEN);
    }

    /**
     * Initializes the controller class.
     * 
     * @param url The location used to resolve relative paths for the root object or <tt>null</tt> if the location is not known.
     * @param rb The resources used to localize the root object, or <tt>null</tt> if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inHouseRadioButton.selectedProperty().setValue(true);
        outsourcedRadioButton.selectedProperty().setValue(false);
        companyOrMachineLabel.setText(LABELTEXT_MACHINEID);
        companyOrMachineTextField.setAccessibleText(LABELTEXT_MACHINEID);
    }
  
    /**
     * Prepares controller for editing an existing part.
     * 
     * @param part The part to be edited.
     */
    public void applyModel(model.Part part) {
        if (part == null)
            return;
        // Save the unique identifier of the part being edited. This can be -1 if it is a new part.
        currentPartId = part.getId();
        
        editPartLabel.setText("Modify Part");
        idTextField.setText(String.valueOf(currentPartId));
        nameTextField.setText(part.getName());
        priceTextField.setText(String.valueOf(part.getPrice()));
        maxTextField.setText(String.valueOf(part.getMax()));
        minTextField.setText(String.valueOf(part.getMin()));
        inventoryTextField.setText(String.valueOf(part.getStock()));
        
        if (part instanceof model.InHouse) {
            inHouseRadioButton.selectedProperty().setValue(true);
            outsourcedRadioButton.selectedProperty().setValue(false);
            model.InHouse inHouse = (model.InHouse)part;
            companyOrMachineLabel.setText(LABELTEXT_MACHINEID);
            companyOrMachineTextField.setAccessibleText(LABELTEXT_MACHINEID);
            companyOrMachineTextField.setText(String.valueOf(inHouse.getMachineId()));
        } else {
            outsourcedRadioButton.selectedProperty().setValue(true);
            inHouseRadioButton.selectedProperty().setValue(false);
            model.Outsourced outsourced = (model.Outsourced)part;
            companyOrMachineLabel.setText(LABELTEXT_COMPANYNAME);
            companyOrMachineTextField.setAccessibleText(LABELTEXT_COMPANYNAME);
            companyOrMachineTextField.setText(outsourced.getCompanyName());
        }
    }
    
    private static final String LABELTEXT_MACHINEID = "Machine ID";
    private static final String LABELTEXT_COMPANYNAME = "Company Name";
}
