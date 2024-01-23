package com.sp.counterfit;

public class MealItem {
    private String mealName;
    private String calorieCount;
    private int imageResourceId;

    public MealItem(String mealName, String calorieCount, int imageResourceId) {
        this.mealName = mealName;
        this.calorieCount = calorieCount;
        this.imageResourceId = imageResourceId;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getCalorieCount() {
        return calorieCount;
    }

    public void setCalorieCount(String calorieCount) {
        this.calorieCount = calorieCount;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}
