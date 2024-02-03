package com.sp.counterfit;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EdamamService {

    private static final String API_URL = "https://api.edamam.com/api/food-database/v2/parser";
    private static final String APP_ID = "2f2207be";
    private static final String API_KEY = "9aedb2d76e1c44274afd08fe513c1538";

    public interface Callback {
        void onResponse(List<FoodItem> foodItems); // Change to use a list of custom FoodItem objects
        void onError(String error);
    }

    public static void searchFood(String query, Callback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(API_URL + "?app_id=" + APP_ID + "&app_key=" + API_KEY + "&ingr=" + query);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                StringBuilder response = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray hints = jsonResponse.getJSONArray("hints");
                List<FoodItem> foodItems = new ArrayList<>();
                for (int i = 0; i < hints.length(); i++) {
                    JSONObject food = hints.getJSONObject(i).getJSONObject("food");
                    String label = food.getString("label");
                    JSONObject nutrients = food.getJSONObject("nutrients");
                    double calories = nutrients.optDouble("ENERC_KCAL", 0);
                    // Cast calories to int
                    foodItems.add(new FoodItem(label, (int) calories, "")); // Assuming an empty string for imageURL for now
                }
                callback.onResponse(foodItems);
            } catch (Exception e) {
                Log.e("EdamamService", "Error searching for food", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }
}