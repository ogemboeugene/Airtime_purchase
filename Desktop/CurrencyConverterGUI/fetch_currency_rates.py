import requests
import json
import argparse

# Replace 'YOUR_APP_ID' with your actual Open Exchange Rates API app ID
app_id = "327ec886643c9b239a5054d0d73218da"  # Replace with your app ID

# Function to fetch historical currency conversion rates for a specific date
def fetch_historical_rates(date):
    # Use the provided date to fetch historical rates
    url = f"http://api.exchangeratesapi.io/{date}?access_key=327ec886643c9b239a5054d0d73218da"

    try:
        # Send an HTTP GET request to the specified URL
        response = requests.get(url)

        # Check for HTTP errors and raise an exception if encountered
        response.raise_for_status()

        # Parse the JSON response from the API
        new_rates_data = response.json()

        # Save the currency rates as JSON data in the file, overwriting the existing data
        with open("currency_rates.json", "w") as json_file:
            json.dump(new_rates_data, json_file)

        # Print a success message
        print(f"Currency rates for {date} successfully fetched and saved.")

    except requests.exceptions.RequestException as e:
        # Handle exceptions related to HTTP requests, such as network errors
        print(f"Failed to fetch currency rates: {e}")

    except json.JSONDecodeError as e:
        # Handle exceptions related to JSON parsing errors
        print(f"Failed to parse JSON response: {e}")

    except Exception as e:
        # Handle unexpected exceptions
        print(f"An unexpected error occurred: {e}")

if __name__ == "__main__":
    # Create an argument parser to accept the selected date
    parser = argparse.ArgumentParser(description="Fetch currency rates from your API and overwrite the JSON file.")
    parser.add_argument("selected_date", help="The selected date for currency conversion rates")

    # Parse the command-line arguments
    args = parser.parse_args()
    date = args.selected_date

    print("Selected date in Python script:", date)

    # Call the function to fetch historical rates for the selected date and update the JSON file
    fetch_historical_rates(date)
