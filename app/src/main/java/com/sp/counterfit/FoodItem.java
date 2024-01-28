package com.sp.counterfit;

public class FoodItem {
    private int id;
    private String name;
    private int calories;
    private String imageUrl;
    public FoodItem(String name, int calories, String imageUrl) {
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

    public int getCalories() {
        return calories;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setters, if needed
    public void setName(String name) {
        this.name = name;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
