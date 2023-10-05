import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import java.util.Map;

public class CurrencyConverterJava {
    public static void main(String[] args) {
        String jsonFilePath = "C:\\Users\\ADMIN\\Desktop\\Currency\\currency_rates.json";

        try (FileReader reader = new FileReader(jsonFilePath)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            
            if (jsonObject.has("rates")) {
                JsonObject ratesObject = jsonObject.getAsJsonObject("rates");

                // Print the first 5 currency exchange rates
                System.out.println("First 5 currency exchange rates:");
                printFirst5Rates(ratesObject);

                Scanner scanner = new Scanner(System.in);

                while (true) {
                    System.out.println("\nCurrency Conversion Menu:");
                    System.out.println("1. Convert from one currency to another");
                    System.out.println("2. Exit");
                    System.out.print("Enter your choice: ");

                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline character

                    switch (choice) {
                        case 1:
                            convertCurrency(ratesObject, scanner);
                            break;
                        case 2:
                            System.out.println("Goodbye!");
                            System.exit(0);
                        default:
                            System.out.println("Invalid choice. Please select 1 or 2.");
                    }
                }
            } else {
                System.err.println("Invalid JSON format: 'rates' field not found.");
            }
        } catch (IOException e) {
            System.err.println("Failed to read currency rates file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void printFirst5Rates(JsonObject ratesObject) {
        Set<Map.Entry<String, JsonElement>> entries = ratesObject.entrySet();
        int count = 0;
        for (Map.Entry<String, JsonElement> entry : entries) {
            if (count >= 5) {
                break;
            }
            System.out.println(entry.getKey() + ": " + entry.getValue().getAsDouble());
            count++;
        }
    }

    private static void convertCurrency(JsonObject ratesObject, Scanner scanner) {
        System.out.print("Enter the source currency code (e.g., USD, EUR): ");
        String sourceCurrency = scanner.nextLine().toUpperCase(); // Convert to uppercase

        System.out.print("Enter the target currency code (e.g., EUR, JPY): ");
        String targetCurrency = scanner.nextLine().toUpperCase(); // Convert to uppercase

        if (ratesObject.has(sourceCurrency) && ratesObject.has(targetCurrency)) {
            double sourceToUSD = ratesObject.get(sourceCurrency).getAsDouble();
            double targetToUSD = ratesObject.get(targetCurrency).getAsDouble();
            
            // Calculate the conversion rate
            double conversionRate = targetToUSD / sourceToUSD;

            System.out.print("Enter the amount to convert: ");
            double amount = scanner.nextDouble();
            scanner.nextLine(); // Consume newline character

            double convertedAmount = amount * conversionRate;
            System.out.println(amount + " " + sourceCurrency + " is equal to " + convertedAmount + " " + targetCurrency);
        } else {
            System.err.println("Invalid currency codes or conversion rates not found.");
        }
    }
}
