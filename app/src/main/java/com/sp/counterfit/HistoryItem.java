package com.sp.counterfit;

public class HistoryItem {

    public static class WeightEntry {
        private long dateInMillis;
        private double weight;

        public WeightEntry(long dateInMillis, double weight) {
            this.dateInMillis = dateInMillis;
            this.weight = weight;
        }

        public long getDateInMillis() {
            return dateInMillis;
        }

        public void setDateInMillis(long dateInMillis) {
            this.dateInMillis = dateInMillis;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }
    }

    public static class MealEntry {
        private String name;
        private int calories;
        private String date;

        public MealEntry(String name, int calories, String date) {
            this.name = name;
            this.calories = calories;
            this.date = date;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCalories() {
            return calories;
        }

        public void setCalories(int calories) {
            this.calories = calories;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
