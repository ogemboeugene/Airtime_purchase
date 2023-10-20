import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public class CurrencyConverterGUI extends Application {
    private JsonObject ratesObject;
    private CustomComboBox sourceCurrencyComboBox;
    private CustomComboBox targetCurrencyComboBox;
    private DatePicker datePicker;
    private TextField amountTextField;
    private Label resultLabel;
    private String selectedDate; // Declare selectedDate at a higher scope
    // Add a boolean flag to track editability
    private boolean isEditable = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Currency Converter");

        // Load the initial JSON data
        loadInitialRatesData();


        BorderPane borderPane = new BorderPane();
        sourceCurrencyComboBox = new CustomComboBox();
        targetCurrencyComboBox = new CustomComboBox();
        amountTextField = new TextField();
        resultLabel = new Label();

        datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());

        // Move the selectedDate declaration here
        selectedDate = LocalDate.now().toString();

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            selectedDate = newValue.toString();

            // When the date changes, update rates and the JSON file
            updateRatesFromJSON(selectedDate);
            // Update the result label here if you want
            resultLabel.setText(""); // Clear the result label
            // updateResultLabel(selectedDate);
        });

        Set<Map.Entry<String, JsonElement>> entries = ratesObject.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            sourceCurrencyComboBox.getItems().add(entry.getKey());
            targetCurrencyComboBox.getItems().add(entry.getKey());
        }

        Button toggleEditButton = new Button("Toggle Edit");
        toggleEditButton.setOnAction(e -> {
            isEditable = !isEditable; // Toggle the editability flag
            sourceCurrencyComboBox.setEditable(isEditable);
            targetCurrencyComboBox.setEditable(isEditable);
        });

        Button convertButton = new Button("Convert");
        convertButton.setOnAction(e -> {
            String sourceCurrency = sourceCurrencyComboBox.getValue();
            String targetCurrency = targetCurrencyComboBox.getValue();
            double amount;

            try {
                amount = Double.parseDouble(amountTextField.getText());
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter a valid numeric amount.");
                return;
            }

            // Temporarily set the combo boxes to non-editable during the conversion
            sourceCurrencyComboBox.setEditable(false);
            targetCurrencyComboBox.setEditable(false);

            // Run the Python script and update the currency rates
            runPythonScript(selectedDate);

            if (ratesObject.has(sourceCurrency) && ratesObject.has(targetCurrency)) {
                 // Perform the currency conversion and update the result label
                double sourceToUSD = ratesObject.get(sourceCurrency).getAsDouble();
                double targetToUSD = ratesObject.get(targetCurrency).getAsDouble();
                double conversionRate = targetToUSD / sourceToUSD;

                double convertedAmount = amount * conversionRate;
                resultLabel.setText(amount + " " + sourceCurrency + " is equal to " + convertedAmount + " " + targetCurrency);
            } else {
                showAlert("Invalid Currency Codes", "Selected currency codes or conversion rates not found.");
            }

             // Set the combo boxes back to editable
            sourceCurrencyComboBox.setEditable(true);
            targetCurrencyComboBox.setEditable(true);
        });

        

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(event -> {
            sourceCurrencyComboBox.setValue(null);
            targetCurrencyComboBox.setValue(null);
            amountTextField.setText("");
            resultLabel.setText("");
        });

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(
            new Label("Source Currency:"),
            sourceCurrencyComboBox,
            new Label("Target Currency:"),
            targetCurrencyComboBox,
            new Label("Amount:"),
            amountTextField,
            new Label("Date:"),
            datePicker,
            convertButton,
            clearButton,
            resultLabel
        );

        borderPane.setCenter(vbox);

        Scene scene = new Scene(borderPane, 500, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadInitialRatesData() {
        // Load the initial JSON data when the application starts
        try (FileReader reader = new FileReader("currency_rates.json")) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            if (jsonObject.has("rates")) {
                ratesObject = jsonObject.getAsJsonObject("rates");
            } else {
                showAlert("Invalid JSON format", "'rates' field not found in JSON.");
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to read currency rates file: " + e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void updateRatesFromJSON(String newDate) {
        // Code to update rates from the JSON file based on the new date
        // You may need to modify this based on your JSON file structure.
        // This is a simplified example.
        try (FileReader reader = new FileReader("currency_rates.json")) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            if (jsonObject.has("rates")) {
                ratesObject = jsonObject.getAsJsonObject("rates");
            } else {
                showAlert("Invalid JSON format", "'rates' field not found in JSON.");
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to read currency rates file: " + e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void runPythonScript(String selectedDate) {
        try {
            String pythonScript = "python";
            String scriptPath = "fetch_currency_rates.py";

            ProcessBuilder processBuilder = new ProcessBuilder(pythonScript, scriptPath, selectedDate);

            Process process = processBuilder.start();

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Python script executed successfully.");
            } else {
                System.err.println("Error running Python script. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class CustomComboBox extends ComboBox<String> {
        private final TextField editor;
        private ObservableList<String> originalItems;
        private ObservableList<String> filteredItems;

        public CustomComboBox() {
            setEditable(true);
            editor = getEditor();

            editor.textProperty().addListener((observable, oldValue, newValue) -> {
                if (originalItems == null) {
                    originalItems = FXCollections.observableArrayList(getItems());
                }

                if (newValue.isEmpty()) {
                    filteredItems = originalItems;
                } else {
                    filteredItems = originalItems.filtered(item ->
                        item.toLowerCase().contains(newValue.toLowerCase()));
                }
                setItems(filteredItems);

                if (!filteredItems.isEmpty()) {
                    show();
                } else {
                    hide();
                }
            });

            setOnHidden(e -> {
                setItems(originalItems);
                filteredItems = null;
            });

            setOnAction(e -> {
                String selected = getSelectionModel().getSelectedItem();
                if (selected != null) {
                    editor.setText(selected); // Set the text when a selection is made
                } else {
                    editor.setText(""); // Clear the text when no selection is made
                }
                hide();
            });
        }

        // Add this method to toggle editability
        public void setComboBoxEditable(boolean isEditable) {
        editor.setEditable(isEditable);
    }
    }
}
