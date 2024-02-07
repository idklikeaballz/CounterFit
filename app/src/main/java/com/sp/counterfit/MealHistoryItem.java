package com.sp.counterfit;

import java.io.Serializable;

public class MealHistoryItem implements Serializable {
    private String foodName;
    private int calories;
    private String date; // Assuming the date is stored as a String

    // Constructor
    public MealHistoryItem(String foodName, int calories, String date) {
        this.foodName = foodName;
        this.calories = calories;
        this.date = date;
    }

    // Getters
    public String getFoodName() {
        return foodName;
    }

    public int getCalories() {
        return calories;
    }

    public String getDate() {
        return date;
    }

    // Setters
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
