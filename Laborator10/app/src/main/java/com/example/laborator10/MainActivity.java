package com.example.laborator10;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "";

    private static final int[] DAY_OPTIONS = {1, 5};

    private EditText editTextCity;
    private Spinner spinnerDays;
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextCity = findViewById(R.id.editTextCity);
        spinnerDays = findViewById(R.id.spinnerDays);
        textViewResult = findViewById(R.id.textViewResult);
        Button buttonSearch = findViewById(R.id.buttonSearch);

        buttonSearch.setOnClickListener(v -> {
            String city = editTextCity.getText().toString().trim();
            if (!city.isEmpty()) {
                new CitySearchTask().execute(city);
            }
        });
    }

    private int getSelectedDays() {
        return DAY_OPTIONS[spinnerDays.getSelectedItemPosition()];
    }

    private String fetchUrl(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        conn.disconnect();
        return sb.toString();
    }

    @SuppressWarnings("deprecation")
    private class CitySearchTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String encodedCity = URLEncoder.encode(params[0], "UTF-8");
                String urlStr = "https://dataservice.accuweather.com/locations/v1/cities/search"
                        + "?apikey=" + API_KEY
                        + "&q=" + encodedCity;

                String response = fetchUrl(urlStr);
                JSONArray jsonArray = new JSONArray(response);
                if (jsonArray.length() > 0) {
                    return jsonArray.getJSONObject(0).getString("Key");
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String cityKey) {
            if (cityKey == null) {
                textViewResult.setText("Orasul nu a fost gasit.");
                return;
            }
            int days = getSelectedDays();
            textViewResult.setText("Codul orasului: " + cityKey + "\nSe incarca prognoza pentru " + days + " zi(le)...");
            new WeatherForecastTask(days).execute(cityKey);
        }
    }

    @SuppressWarnings("deprecation")
    private class WeatherForecastTask extends AsyncTask<String, Void, String> {

        private final int days;

        WeatherForecastTask(int days) {
            this.days = days;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String cityKey = params[0];
                String endpoint = days + "day";
                String urlStr = "https://dataservice.accuweather.com/forecasts/v1/daily/"
                        + endpoint + "/"
                        + cityKey
                        + "?apikey=" + API_KEY
                        + "&metric=true";

                String response = fetchUrl(urlStr);
                JSONObject root = new JSONObject(response);
                JSONArray dailyForecasts = root.getJSONArray("DailyForecasts");

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < dailyForecasts.length(); i++) {
                    JSONObject forecast = dailyForecasts.getJSONObject(i);
                    String date = forecast.getString("Date").substring(0, 10);
                    JSONObject temperature = forecast.getJSONObject("Temperature");
                    double minTemp = temperature.getJSONObject("Minimum").getDouble("Value");
                    double maxTemp = temperature.getJSONObject("Maximum").getDouble("Value");

                    sb.append("Ziua ").append(i + 1).append(" (").append(date).append(")\n");
                    sb.append("  Min: ").append(minTemp).append(" °C\n");
                    sb.append("  Max: ").append(maxTemp).append(" °C\n\n");
                }
                return sb.toString().trim();
            } catch (Exception e) {
                return "Eroare la obtinerea prognozei: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            String current = textViewResult.getText().toString();
            String cityLine = current.split("\n")[0];
            textViewResult.setText(cityLine + "\n\n" + result);
        }
    }
}
