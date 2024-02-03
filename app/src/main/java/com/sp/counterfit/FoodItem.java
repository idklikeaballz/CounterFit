package com.sp.counterfit;

public class FoodItem {
    private int id;
    private String name;
    private double calories; // Change to double
    private String imageUrl;

    public FoodItem(String name, double calories, String imageUrl) {
        this.name = name;
        this.calories = calories;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getCalories() {
        return calories;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setters, if needed
    public void setName(String name) {
        this.name = name;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    @Override
    public String toString() {
        // Return a string that combines the food name and calories
        return name + " - " + calories + " calories";
    }
}
