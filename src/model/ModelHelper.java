package model;

import java.security.InvalidKeyException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import javax.management.openmbean.KeyAlreadyExistsException;

/**
 * Utility methods and definitions.
 * 
 * @author Leonard T. Erwine
 */
public class ModelHelper {
    public static final String PROP_ID = "id";
    public static final String PROP_NAME = "name";
    public static final String PROP_PRICE = "price";
    public static final String PROP_STOCK = "stock";
    public static final String PROP_MIN = "min";
    public static final String PROP_MAX = "max";
    public static final String PROP_MACHINEID = "machineId";
    public static final String PROP_COMPANYNAME = "companyName";
    public static final String PROP_ASSOCIATEDPARTS = "associatedParts";
    
    /**
     * Searches a list for the first item matching a unique identifier.
     * 
     * @param <T> Type of item being searched.
     * @param source List to search.
     * @param id Unique identifier to search for.
     * @return The first element whose unique identifier matches the specified value or null if no match was found.
     */
    public static <T extends Part> T lookupPart(Iterable<T> source, int id) {
        for (T item : source) {
            if (item != null && item.getId() == id)
                return item;
        }
        return null;
    }
 
    /**
     * Searches a list for the first item matching a unique identifier.
     * 
     * @param source List to search.
     * @param id Unique identifier to search for.
     * @return The first element whose unique identifier matches the specified value or null if no match was found.
     */
    public static Product lookupProduct(Iterable<Product> source, int id) {
        for (Product item : source) {
            if (item != null && item.getId() == id)
                return item;
        }
        return null;
    }
 
    /**
     * Searches a list for items whose name contains the specified string.
     * .
     * @param <T> Type of item being searched.
     * @param source List to search.
     * @param name The name to search for (substring, case-insensitive).
     * @return A filtered  list of elements whose name contains the specified string.
     */
    public static <T extends Part> FilteredList<T> lookupParts(ObservableList<T> source, String name) {
        final String text;
        if (name == null || (text = name.trim().toLowerCase()).length() == 0)
            return new FilteredList(FXCollections.observableArrayList());
        return source.filtered(item -> item.getName().toLowerCase().contains(text));
    }
    
    /**
     * Searches a list for items whose name contains the specified string.
     * .
     * @param source List to search.
     * @param name The name to search for (substring, case-insensitive).
     * @return A filtered  list of elements whose name contains the specified string.
     */
    public static FilteredList<Product> lookupProducts(ObservableList<Product> source, String name) {
        final String text;
        if (name == null || (text = name.trim().toLowerCase()).length() == 0)
            return new FilteredList(FXCollections.observableArrayList());
        return source.filtered(item -> item.getName().toLowerCase().contains(text));
    }
    
    /**
     * Calculates prospective sum of all part prices in a given stream of Part objects.
     * 
     * @param partId Unique identifier of prospective associated part.
     * @param newPrice New price of part.
     * @param parts Parts that will be merged with newPrice/partId to calculate a sum of all part prices.
     * @return
     */
    public static double getPriceSum(int partId, double newPrice, Stream<Part> parts) {
        return newPrice + parts.filter((Part p) -> p.getId() != partId).mapToDouble((Part p) -> p.getPrice()).sum();
    }

    /**
     * Calculates prospective sum of all part prices in a given list of Part objects.
     * 
     * @param partId Unique identifier of prospective associated part.
     * @param newPrice New price of part.
     * @param parts Parts that will be merged with newPrice/partId to calculate a sum of all part prices.
     * @return Products where the sum of the price of its parts will be calculated.
     */
    public static double getPriceSum(int partId, double newPrice, List<Part> parts) { return getPriceSum(partId, newPrice, parts.stream()); }

    /**
     * Calculates prospective sum of all part prices in a given stream of Part objects.
     * 
     * @param parts Parts that will be merged with newPrice/partId to calculate a sum of all part prices.
     * @return
     */
    public static double getPriceSum(Stream<Part> parts) { return parts.mapToDouble((Part p) -> p.getPrice()).sum(); }

    /**
     * Calculates prospective sum of all part prices in a given list of Part objects.
     * 
     * @param parts Parts that will be used to calculate a sum price value.
     * @return
     */
    public static double getPriceSum(List<Part> parts) { return getPriceSum(parts.stream()); }

    public static Part lookupPart(Product product, int partId) {
        if (product == null || partId < 0)
            return null;
        for (Part p: product.getAllAssociatedParts()){
            if (p.getId() == partId)
                return p;
        }
        return null;
    }
    
    /**
     *
     * @param partId
     * @return
     */
    public static FilteredList<Product> getAssociatedProducts(int partId) {
        return Inventory.getAllProducts().filtered((Product p) -> lookupPart(p, partId) != null);
    }
    
    /**
     *
     * @param partId
     * @param newPrice
     * @return
     */
    public static FilteredList<Product> getPotentialPriceSumViolations(int partId, double newPrice) {
        return Inventory.getAllProducts().filtered((Product p) -> {
            return lookupPart(p, partId) != null && getPriceSum(partId, newPrice, p.getAllAssociatedParts()) > p.getPrice();
        });
    }
    
    /**
     *
     * @param partId
     * @return
     */
    public static FilteredList<Product> getWhereLastAssociatedProduct(int partId) {
        return Inventory.getAllProducts().filtered((Product p) -> {
            ObservableList<Part> parts = p.getAllAssociatedParts();
            return parts.size() == 1 && parts.get(0).getId() == partId;
        });
    }
    
    /**
     *
     * @return
     */
    public Stream<Pair<Integer, ObservableList<Part>>> getPartAssociations() {
        return Inventory.getAllProducts().stream().map((Product p) -> new Pair(p.getId(), p.getAllAssociatedParts()));
    }
    
    /**
     * Converts objects to string values and combines them.
     * 
     * @param <T> Type of object to convert.
     * @param source Iterable object containing values to convert.
     * @param mapper Function that converts values to strings.
     * @param separator String delimiter to use when joining strings.
     * @return String values concatenated with separator strings between.
     */
    public static <T> String joinStrings(Iterable<T> source, Function<T, String> mapper, String separator) {
        Iterator<T> iterator = source.iterator();
        if (!iterator.hasNext())
            return "";
        StringBuilder result = new StringBuilder();
        result.append(mapper.apply(iterator.next()));
        while (iterator.hasNext())
            result.append(separator).append(mapper.apply(iterator.next()));
        return result.toString();
    }

    /**
     * Joins a series of strings together, using the specified separator.
     * 
     * @param source Iterable object containing strings to join.
     * @param separator String delimiter to use when joining strings.
     * @return String values concatenated with separator strings between.
     */
    public static String joinStrings(Iterable<String> source, String separator) {
        Iterator<String> iterator = source.iterator();
        if (!iterator.hasNext())
            return "";
        StringBuilder result = new StringBuilder();
        result.append(iterator.next());
        while (iterator.hasNext())
            result.append(separator).append(iterator.next());
        return result.toString();
    }

    /**
     * Attempts to convert an object to an Integer value.
     * 
     * @param value Value to convert
     * @return An Optional with the number value set if it was able to successfully coerce a number value.
     */
    public static Optional<Integer> tryConvertToInteger(Object value) {
        if (value == null)
            return Optional.empty();
        if (value instanceof Integer)
            return Optional.of((int)value);
        if (value instanceof String) {
            String s = ((String)value).trim();
            if (s.length() > 0)
                try { return Optional.of(Integer.parseInt(s)); } catch (NumberFormatException e) { }
        } else
            try { return Optional.of((int)value); } catch (Exception e) { }
        return Optional.empty();
    }
    
    /**
     * Attempts to convert an object to a Double value.
     * 
     * @param value Value to convert
     * @return An Optional with the number value set if it was able to successfully coerce a number value.
     */
    public static Optional<Double> tryConvertToDouble(Object value) {
        if (value == null)
            return Optional.empty();
        if (value instanceof Integer)
            return Optional.of((double)value);
        if (value instanceof String) {
            String s = ((String)value).trim();
            if (s.length() > 0)
                try { return Optional.of(Double.parseDouble(s)); } catch (NumberFormatException e) { }
        } else
            try { return Optional.of((double)value); } catch (Exception e) { }
        return Optional.empty();
    }

    /**
     * Attempts to parse or convert a value to an integer.
     * 
     * @param value Value to convert
     * @return Value as integer if it was able to be converted; otherwise, the original object is returned.
     */
    public static Object tryConvertToInt(Object value) {
        if (value == null || value instanceof Integer)
            return value;

        if (value instanceof String){
            String s = ((String)value).trim();
            if (s.length() == 0)
                return s;
            try {
                int i = Integer.parseInt((String)value);
                try {
                    if (Double.parseDouble(s) != (double)i)
                        return s;
                } catch (NumberFormatException e) { }
                return i;
            } catch (NumberFormatException e) { }
            return value;
        }

        try { return (int)value; } catch (Exception e) { }
        return value;
    }

    /**
     *Asserts that a candidate unique identifier (getId()) is not negative, and that no other Part currently contained in the allParts list have that unique identifier.
     * @param part The target Part that is presumably about to have the id changed.
     * @param newId The new unique identifier value.
     * @throws java.security.InvalidKeyException newId is less than zero or another Part already uses that Id;
     */
    public static void assertValidIdChange(Part part, int newId) throws InvalidKeyException {
        if (part == null)
            throw new NullPointerException();
        if (newId < 0)
            throw new InvalidKeyException();
        if (part.getId() == newId)
            return;
        ObservableList<Part> allParts = Inventory.getAllParts();
        int index = allParts.indexOf(part);
        if (index < 0)
            return;
        for (int i = 0; i < allParts.size(); i++) {
            if (i != index && allParts.get(i).getId() == newId)
                throw new KeyAlreadyExistsException();
        }
    }
    
    /**
     *Asserts that a candidate unique identifier (getId()) is not negative, and that no other Part currently contained in the allParts list have that unique identifier.
     * @param product The target Product that is presumably about to have the id changed.
     * @param newId The new unique identifier value.
     * @throws java.security.InvalidKeyException newId is less than zero or another Product already uses that Id;
     */
    public static void assertValidIdChange(Product product, int newId) throws InvalidKeyException {
        if (product == null)
            throw new NullPointerException();
        if (newId < 0)
            throw new InvalidKeyException();
        if (product.getId() == newId)
            return;
        ObservableList<Product> allProducts = Inventory.getAllProducts();
        int index = allProducts.indexOf(product);
        if (index < 0)
            return;
        for (int i = 0; i < allProducts.size(); i++) {
            if (i != index && allProducts.get(i).getId() == newId)
                throw new KeyAlreadyExistsException();
        }
    }

    /**
     * Determines whether a part has been associated with a specific product.
     * 
     * @param part Part to test.
     * @param product Product to search.
     * @return True if part is associated with product; otherwise, false.
     */
    public static boolean isAssociatedPartAdded(Part part, Product product) { return part != null && product != null && product.getAllAssociatedParts().contains(part); }
    
    /**
     * Determines whether a part has been added to the inventory list.
     * 
     * @param part Part to test.
     * @return True if part exists in inventory list; otherwise, false.
     */
    public static boolean isPartAdded(Part part) { return part != null && Inventory.getAllParts().contains(part); }
    
    /**
     * Determines whether a product has been added to the inventory list.
     * 
     * @param product Product to test.
     * @return True if product exists in inventory list; otherwise, false.
     */
    public static boolean isProductAdded(Product product) { return product != null && Inventory.getAllProducts().contains(product); }
    
    /**
     * Displays a notificaton dialog box.
     * @param title Title of dialog box.
     * @param headerText Header text for dialog box.
     * @param contentText Content of dialog box.
     * @param type Type of dialog box to display.
     */
    public static void showNotificationDialog(String title, String headerText, String contentText, Alert.AlertType type) {
        Alert alert = new Alert(type, contentText, ButtonType.OK);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }
    
    /**
     * Displays a confirmation dialog box.
     * @param title Title of dialog box.
     * @param headerText Header text for dialog box.
     * @param contentText Content of dialog box.
     * @param type Type of dialog box to display.
     * @param showCancel Whether to show the 'Cancel' button as well as the 'Yes' and 'No' buttons.
     * @return Optional&lt;ButtonType&gt; using ButtonType.YES, ButtonType.NO or ButtonType.CANCEL.
     */
    public static Optional<ButtonType> showConfirmationDialog(String title, String headerText, String contentText, Alert.AlertType type, boolean showCancel) {
        Alert alert = (showCancel) ? new Alert(type, contentText, ButtonType.YES, ButtonType.NO, ButtonType.CANCEL) : 
                new Alert(type, contentText, ButtonType.YES, ButtonType.NO);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        return alert.showAndWait();
    }
    
    /**
     * Displays a confirmation dialog box.
     * @param title Title of dialog box.
     * @param headerText Header text for dialog box.
     * @param contentText Content of dialog box.
     * @param type Type of dialog box to display.
     * @return Optional&lt;ButtonType&gt; using ButtonType.YES or ButtonType.NO.
     */
    public static Optional<ButtonType> showConfirmationDialog(String title, String headerText, String contentText, Alert.AlertType type) {
        return showConfirmationDialog(title, headerText, contentText, type, false);
    }
}