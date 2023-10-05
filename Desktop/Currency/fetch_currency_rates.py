import requests
import json

# Replace 'YOUR_APP_ID' with your actual Open Exchange Rates API app ID
app_id = "81516390be5d475b99cab506d22b6388"

# URL to fetch currency conversion rates
url = f"https://openexchangerates.org/api/latest.json?app_id=81516390be5d475b99cab506d22b6388"

try:
    response = requests.get(url)
    response.raise_for_status()  # Raise an exception for HTTP errors

    rates_data = response.json()
    with open("currency_rates.json", "w") as json_file:
        json.dump(rates_data, json_file)
    print("Currency rates successfully fetched and saved.")
except requests.exceptions.RequestException as e:
    print(f"Failed to fetch currency rates: {e}")
except json.JSONDecodeError as e:
    print(f"Failed to parse JSON response: {e}")
except Exception as e:
    print(f"An unexpected error occurred: {e}")