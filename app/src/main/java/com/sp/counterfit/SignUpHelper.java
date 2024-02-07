package com.sp.counterfit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SignUpHelper extends SQLiteOpenHelper {
    private static final String TIPS_TABLE_NAME = "Tips";
    private static final String COLUMN_TIP_ID = "id";
    private static final String COLUMN_TIP_TEXT = "text";
    private static final String COLUMN_PROFILE_IMAGE_URI = "profileImageUri"; // New column for image URI


    private static final String DATABASE_NAME = "UserDatabase";
    private static final int DATABASE_VERSION = 8;
    public static final String TABLE_NAME = "UserDetails";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_WEIGHT_GOAL = "weightGoal";
    public static final String COLUMN_CALORIES_REMAINING = "caloriesRemaining"; // Define this column
    private static final String SESSION_TABLE_NAME = "CurrentSession";
    private static final String SESSION_COLUMN_USER_ID = "userId";
    private static final String FOOD_TABLE_NAME = "FoodItems";
    private static final String COLUMN_FOOD_ID = "id";
    private static final String COLUMN_FOOD_NAME = "name";
    private static final String COLUMN_FOOD_CALORIES = "calories";
    private static final String COLUMN_FOOD_IMAGE_URI = "imageUri";
    private static final String COLUMN_FOOD_USER_ID = "userId"; // New column for linking food to a user
    private static final String MEAL_HISTORY_TABLE_NAME = "MealHistory";
    private static final String COLUMN_MEAL_HISTORY_ID = "id";
    private static final String COLUMN_MEAL_HISTORY_FOOD_NAME = "foodName";
    private static final String COLUMN_MEAL_HISTORY_CALORIES = "calories";
    private static final String COLUMN_MEAL_HISTORY_DATE = "date";
    private static final String COLUMN_MEAL_HISTORY_USER_ID = "userId"; // To link the meal to a specific user

    private static final String CREATE_MEAL_HISTORY_TABLE = "CREATE TABLE " + MEAL_HISTORY_TABLE_NAME + "("
            + COLUMN_MEAL_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_MEAL_HISTORY_FOOD_NAME + " TEXT,"
            + COLUMN_MEAL_HISTORY_CALORIES + " INTEGER,"
            + COLUMN_MEAL_HISTORY_DATE + " TEXT," // Storing date as TEXT in SQLite (you can format it as 'YYYY-MM-DD')
            + COLUMN_MEAL_HISTORY_USER_ID + " INTEGER," // Link to user ID
            + "FOREIGN KEY(" + COLUMN_MEAL_HISTORY_USER_ID + ") REFERENCES " + TABLE_NAME + "(" + COLUMN_ID + "))";


    private static final String CREATE_TIPS_TABLE = "CREATE TABLE " + TIPS_TABLE_NAME + "("
            + COLUMN_TIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TIP_TEXT + " TEXT)";


    private static final String CREATE_SESSION_TABLE = "CREATE TABLE " + SESSION_TABLE_NAME + "("
            + SESSION_COLUMN_USER_ID + " INTEGER PRIMARY KEY)";


    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_GENDER + " TEXT,"
            + COLUMN_AGE + " INTEGER,"
            + COLUMN_WEIGHT + " REAL,"
            + COLUMN_HEIGHT + " REAL,"
            + COLUMN_EMAIL + " TEXT UNIQUE,"
            + COLUMN_PASSWORD + " TEXT,"
            + COLUMN_WEIGHT_GOAL + " TEXT," // Added column for weight goal
            + COLUMN_CALORIES_REMAINING + " INTEGER," // Added column for calories remaining
            + COLUMN_PROFILE_IMAGE_URI + " TEXT" // New column for profile image URI
            + ")";
    private static String CREATE_FOOD_TABLE = "CREATE TABLE " + FOOD_TABLE_NAME + "("
            + COLUMN_FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_FOOD_USER_ID + " INTEGER," // Link to user ID
            + COLUMN_FOOD_NAME + " TEXT,"
            + COLUMN_FOOD_CALORIES + " INTEGER,"
            + COLUMN_FOOD_IMAGE_URI + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_FOOD_USER_ID + ") REFERENCES " + TABLE_NAME + "(" + COLUMN_ID + "))"; // Add foreign key constraint




    public SignUpHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_SESSION_TABLE);
        db.execSQL(CREATE_TIPS_TABLE);
        db.execSQL(CREATE_FOOD_TABLE);
        // New table creation for meal history
        db.execSQL(CREATE_MEAL_HISTORY_TABLE);
        insertInitialTips(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Check if we are upgrading from a version prior to the introduction of the CurrentSession table
        if (oldVersion < 8) { // Assuming your previous database version was 7
            db.execSQL(CREATE_MEAL_HISTORY_TABLE);
        }
        // Add more conditions here for other database upgrades
    }
    public long insertFoodItem(int userId, String name, int calories, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOOD_USER_ID, userId);
        values.put(COLUMN_FOOD_NAME, name);
        values.put(COLUMN_FOOD_CALORIES, calories);
        values.put(COLUMN_FOOD_IMAGE_URI, imageUri);

        // insert() returns the row ID of the newly inserted row, or -1 if an error occurred
        long newRowId = db.insert(FOOD_TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    public List<FoodItem> getFoodItemsByUserId(int userId) {
        List<FoodItem> foodItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(FOOD_TABLE_NAME,
                new String[]{COLUMN_FOOD_ID, COLUMN_FOOD_NAME, COLUMN_FOOD_CALORIES, COLUMN_FOOD_IMAGE_URI},
                COLUMN_FOOD_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_NAME));
                int calories = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_CALORIES));
                String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_IMAGE_URI));
                FoodItem foodItem = new FoodItem(name, calories, imageUri);
                foodItem.setId(id);  // Set the id for the food item
                foodItems.add(foodItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return foodItems;
    }


    private void insertInitialTips(SQLiteDatabase db) {
        String[] tips = {
                "Tip: Drink plenty of water every day!",
                "Tip: Sleep is just as important as diet and exercise.",
                "Tip: Consistency is key to a healthy lifestyle.",
                "Tip: Eat balanced, exercise, sleep well, hydrate, manage stress, avoid smoking.",
                "Tip: Limit sugar, prioritize vegetables, stay active, avoid excessive alcohol, practice mindfulness.",
                "Tip: Limit caffeine, avoid excessive salt, practice moderation, foster a support system.",
                "Tip: Choose whole grains, vary your workouts, practice self-care, stay positive.",
                "Tip: Prioritize sleep, manage screen time, maintain social connections, practice gratitude.",
                "Tip: Stay hydrated, portion control, reduce processed foods, get regular check-ups."
        };

        for (String tip : tips) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TIP_TEXT, tip);
            db.insert(TIPS_TABLE_NAME, null, values);
        }
    }


    public void insertUserDetails(String gender, int age, double weight, double height,
                                  String email, String hashedPassword, String weightGoal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, hashedPassword);
        values.put(COLUMN_WEIGHT_GOAL, weightGoal); // Insert weight goal

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public UserDetails getUserDetailsByEmail(String email) {
        if (email == null) {
            Log.d("SignUpHelper", "Email is null, cannot query database.");
            return null;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        UserDetails userDetails = null;

        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID, COLUMN_GENDER, COLUMN_AGE, COLUMN_WEIGHT, COLUMN_HEIGHT, COLUMN_EMAIL, COLUMN_WEIGHT_GOAL, COLUMN_CALORIES_REMAINING},
                COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            userDetails = new UserDetails(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT_GOAL)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CALORIES_REMAINING)) // Retrieve calories remaining
            );
            cursor.close();
        }

        db.close();
        return userDetails;
    }

    public double calculateBMR(String gender, int age, double weight, double height) {
        if ("Male".equalsIgnoreCase(gender)) {
            return 10 * weight + 6.25 * height * 100 - 5 * age + 5 + 500;
        } else {
            return 10 * weight + 6.25 * height * 100 - 5 * age - 161 + 300;
        }
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_EMAIL, COLUMN_PASSWORD},
                COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(cursor.getString(0), cursor.getString(1));
            cursor.close();
            return user;
        }

        if (cursor != null) {
            cursor.close();
        }

        return null;
    }
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_EMAIL + " =?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public static class UserDetails implements Serializable {
        public int userId;
        public String gender;
        public int age;
        public double weight;
        public double height;
        public String email;
        public String weightGoal;
        public int caloriesRemaining; // Add this field

        public UserDetails(int userId, String gender, int age, double weight, double height, String email, String weightGoal, int caloriesRemaining) {
            this.userId = userId;
            this.gender = gender;
            this.age = age;
            this.weight = weight;
            this.height = height;
            this.email = email;
            this.weightGoal = weightGoal;
            this.caloriesRemaining = caloriesRemaining; // Initialize caloriesRemaining
        }
    }




    public void setCurrentSession(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SESSION_TABLE_NAME, null, null); // Clear previous session

        ContentValues values = new ContentValues();
        values.put(SESSION_COLUMN_USER_ID, userId);
        db.insert(SESSION_TABLE_NAME, null, values);
        db.close();
    }

    public int getCurrentUserId() {
        SQLiteDatabase db = this.getReadableDatabase();
        int currentUserId = -1;

        Cursor cursor = db.query(SESSION_TABLE_NAME, new String[]{SESSION_COLUMN_USER_ID},
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            currentUserId = cursor.getInt(cursor.getColumnIndexOrThrow(SESSION_COLUMN_USER_ID));
            cursor.close();
        } else {
            Log.e("SignUpHelper", "Current session not found.");
        }

        db.close();
        return currentUserId;
    }
    public String getCurrentUserEmail() {
        int currentUserId = getCurrentUserId();
        if (currentUserId == -1) {
            Log.d("SignUpHelper", "No current user ID found.");
            return null;
        }

        UserDetails userDetails = getUserDetailsById(currentUserId);
        return userDetails != null ? userDetails.email : null;
    }

    private UserDetails getUserDetailsById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        UserDetails userDetails = null;

        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID, COLUMN_GENDER, COLUMN_AGE, COLUMN_WEIGHT, COLUMN_HEIGHT, COLUMN_EMAIL, COLUMN_WEIGHT_GOAL, COLUMN_CALORIES_REMAINING},
                COLUMN_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            userDetails = new UserDetails(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT_GOAL)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CALORIES_REMAINING))
            );
            cursor.close();
        }

        db.close();
        return userDetails;
    }

    public String getRandomTip() {
        SQLiteDatabase db = this.getReadableDatabase();
        String tip = null;

        Cursor cursor = db.query(
                TIPS_TABLE_NAME,
                new String[]{COLUMN_TIP_TEXT},
                null,
                null,
                null,
                null,
                "RANDOM()",
                "1"
        );

        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(COLUMN_TIP_TEXT);
            if (index != -1) {
                tip = cursor.getString(index);
            } else {
                Log.e("SignUpHelper", "Column not found: " + COLUMN_TIP_TEXT);
            }
            cursor.close();
        } else {
            Log.e("SignUpHelper", "No tips found in the database.");
        }

        db.close();
        return tip;
    }


    public void clearCurrentSession() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SESSION_TABLE_NAME, null, null);
        db.close();
    }
    public double calculateBMRForCurrentUser() {
        String currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail != null) {
            UserDetails userDetails = getUserDetailsByEmail(currentUserEmail);
            if (userDetails != null) {
                return calculateBMR(userDetails.gender, userDetails.age, userDetails.weight, userDetails.height);
            }
        }
        return 0;
    }

    public String getWeightGoalForCurrentUser() {
        String currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail != null) {
            UserDetails userDetails = getUserDetailsByEmail(currentUserEmail);
            if (userDetails != null) {
                return userDetails.weightGoal;
            }
        }
        return "";
    }

    public void updateCaloriesRemaining(String email, int caloriesRemaining) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CALORIES_REMAINING, caloriesRemaining);

        int rowsUpdated = db.update(TABLE_NAME, values, COLUMN_EMAIL + " = ?", new String[]{email});
        db.close();

        if (rowsUpdated == 1) {
            Log.d("SignUpHelper", "Calories remaining updated successfully.");
        } else {
            Log.e("SignUpHelper", "Failed to update calories remaining.");
        }
    }
    public void updateFoodItem(int mealId, String name, int calories, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOOD_NAME, name);
        values.put(COLUMN_FOOD_CALORIES, calories);
        values.put(COLUMN_FOOD_IMAGE_URI, imageUri);

        // Update the database entry where the COLUMN_FOOD_ID matches the mealId
        int rowsAffected = db.update(FOOD_TABLE_NAME, values, COLUMN_FOOD_ID + " = ?", new String[]{String.valueOf(mealId)});

        if (rowsAffected > 0) {
            Log.d("SignUpHelper", "Food item updated successfully.");
        } else {
            Log.e("SignUpHelper", "Failed to update food item.");
        }


        db.close();
    }

    public void updateUserRemainingCalories(String userEmail, int remainingCalories) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CALORIES_REMAINING, remainingCalories);

        // Update the user's remaining calories in the database
        db.update(TABLE_NAME, values, COLUMN_EMAIL + " = ?", new String[]{userEmail});
        db.close();
    }
    public int getCaloriesRemaining(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        int caloriesRemaining = 0;

        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_CALORIES_REMAINING},
                COLUMN_EMAIL + "=?", new String[]{userEmail}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            caloriesRemaining = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CALORIES_REMAINING));
            cursor.close();
        }

        db.close();
        return caloriesRemaining;
    }
    public void updateUserEmail(String newEmail) {
        int userId = getCurrentUserId();
        if (userId != -1) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_EMAIL, newEmail);

            int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
            db.close();

            Log.d("SignUpHelper", "Rows affected: " + rowsAffected);
        } else {
            Log.d("SignUpHelper", "No user ID found for email update");
        }
    }


    public void updateProfileImageUri(String userEmail, String imageUri) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_PROFILE_IMAGE_URI, imageUri);

            db.update(TABLE_NAME, values, COLUMN_EMAIL + " = ?", new String[]{userEmail});
        } catch (Exception e) {
            Log.e("SignUpHelper", "Error updating profile image URI", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }


    public String getProfileImageUri(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        String imageUri = null;

        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_PROFILE_IMAGE_URI},
                COLUMN_EMAIL + "=?", new String[]{userEmail}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_IMAGE_URI));
            cursor.close();
        }

        db.close();
        return imageUri;
    }
    public void deleteFoodItem(int foodItemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FOOD_TABLE_NAME, COLUMN_FOOD_ID + " = ?", new String[]{String.valueOf(foodItemId)});
        db.close();
    }
    public void updateUserPassword(String userEmail, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword); // Assuming you hash the password before storing

        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_EMAIL + "=?", new String[]{userEmail});
        Log.d("SignUpHelper", "Password update affected rows: " + rowsAffected);

        db.close();
    }
    public long insertMealHistory(int userId, String foodName, int calories, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEAL_HISTORY_FOOD_NAME, foodName);
        values.put(COLUMN_MEAL_HISTORY_CALORIES, calories);
        values.put(COLUMN_MEAL_HISTORY_DATE, date);
        values.put(COLUMN_MEAL_HISTORY_USER_ID, userId);

        long newRowId = db.insert(MEAL_HISTORY_TABLE_NAME, null, values);
        db.close();

        Log.d("SignUpHelper", "Inserted meal history with row ID: " + newRowId);
        return newRowId;
    }
    public List<MealHistoryItem> getMealHistoryByUserId(int userId) {
        List<MealHistoryItem> mealHistory = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MEAL_HISTORY_TABLE_NAME,
                new String[]{COLUMN_MEAL_HISTORY_FOOD_NAME, COLUMN_MEAL_HISTORY_CALORIES, COLUMN_MEAL_HISTORY_DATE},
                COLUMN_MEAL_HISTORY_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String foodName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEAL_HISTORY_FOOD_NAME));
                int calories = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MEAL_HISTORY_CALORIES));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEAL_HISTORY_DATE));
                mealHistory.add(new MealHistoryItem(foodName, calories, date));
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.d("SignUpHelper", "Fetched " + mealHistory.size() + " meal history items for user ID: " + userId);
        return mealHistory;
    }







}







