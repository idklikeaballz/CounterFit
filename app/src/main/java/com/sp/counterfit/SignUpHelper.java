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

    private static final String COLUMN_START_DATE = "startDate"; // Add this line

    private static final String DATABASE_NAME = "UserDatabase";
    private static final int DATABASE_VERSION = 9;
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
    private static final String ACTIVITIES_TABLE_NAME = "Activities";
    private static final String COLUMN_ACTIVITY_ID = "id";
    private static final String COLUMN_ACTIVITY_DATE = "date";
    private static final String COLUMN_ACTIVITY_CALORIES_BURNED = "caloriesBurned";
    private static final String COLUMN_ACTIVITY_USER_ID = "userId";

    private static final String CREATE_ACTIVITIES_TABLE = "CREATE TABLE " + ACTIVITIES_TABLE_NAME + "("
            + COLUMN_ACTIVITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_ACTIVITY_DATE + " TEXT,"
            + COLUMN_ACTIVITY_CALORIES_BURNED + " INTEGER,"
            + COLUMN_ACTIVITY_USER_ID + " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_ACTIVITY_USER_ID + ") REFERENCES " + TABLE_NAME + "(" + COLUMN_ID + "))";


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
            + COLUMN_WEIGHT_GOAL + " TEXT,"
            + COLUMN_CALORIES_REMAINING + " INTEGER,"
            + COLUMN_PROFILE_IMAGE_URI + " TEXT,"
            + COLUMN_START_DATE + " TEXT" // Add this line
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
        db.execSQL(CREATE_ACTIVITIES_TABLE);
        insertInitialTips(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Existing upgrade logic
        if (oldVersion < 9) { // Assuming the new version that includes activities table is 9
            db.execSQL(CREATE_ACTIVITIES_TABLE);
        }
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
        SQLiteDatabase db = this.getReadableDatabase(); // Correctly obtaining a reference to the database
        Cursor cursor = db.query(MEAL_HISTORY_TABLE_NAME,
                new String[]{COLUMN_MEAL_HISTORY_ID, COLUMN_MEAL_HISTORY_FOOD_NAME, COLUMN_MEAL_HISTORY_CALORIES, COLUMN_MEAL_HISTORY_DATE},
                COLUMN_MEAL_HISTORY_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MEAL_HISTORY_ID));
                String foodName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEAL_HISTORY_FOOD_NAME));
                int calories = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MEAL_HISTORY_CALORIES));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEAL_HISTORY_DATE));
                MealHistoryItem item = new MealHistoryItem(id, foodName, calories, date); // Correctly creating a MealHistoryItem instance
                mealHistory.add(item);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close(); // Close the database connection here
        return mealHistory;
    }

    public boolean removeMeal(int userId, int mealId) {
        Log.d("SignUpHelper", "Attempting to remove meal with ID: " + mealId + " for user ID: " + userId);
        SQLiteDatabase db = this.getWritableDatabase();
        int mealCalories = 0;

        Cursor calorieCursor = db.query(MEAL_HISTORY_TABLE_NAME,
                new String[]{COLUMN_MEAL_HISTORY_CALORIES},
                COLUMN_MEAL_HISTORY_ID + "=? AND " + COLUMN_MEAL_HISTORY_USER_ID + "=?",
                new String[]{String.valueOf(mealId), String.valueOf(userId)},
                null, null, null);

        if (calorieCursor.moveToFirst()) {
            mealCalories = calorieCursor.getInt(calorieCursor.getColumnIndexOrThrow(COLUMN_MEAL_HISTORY_CALORIES));
            Log.d("DebugRemoveMeal", "Meal calories for removal: " + mealCalories);
        } else {
            Log.d("DebugRemoveMeal", "Failed to find meal for ID: " + mealId);
        }

        calorieCursor.close();



        int deletedRows = db.delete(MEAL_HISTORY_TABLE_NAME, COLUMN_MEAL_HISTORY_ID + "=? AND " + COLUMN_MEAL_HISTORY_USER_ID + "=?", new String[]{String.valueOf(mealId), String.valueOf(userId)});
        Log.d("DebugRemoveMeal", "Number of rows deleted: " + deletedRows);



        if (deletedRows > 0) {
            updateUserRemainingCaloriesAfterMealRemoval(userId, mealCalories);
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }
    }






    private void updateUserRemainingCaloriesAfterMealRemoval(int userId, int mealCalories) {
        // Fetch current calories, add back the meal calories, and update
        int currentCalories = getCaloriesRemainingByUserId(userId);
        int updatedCalories = currentCalories + mealCalories;

        ContentValues values = new ContentValues();
        values.put(COLUMN_CALORIES_REMAINING, updatedCalories);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
        Log.d("DebugRemoveMeal", "Updated calories remaining for user ID: " + userId);
        db.close();
    }

    public int getCaloriesRemainingByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_CALORIES_REMAINING},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        int caloriesRemaining = 0;
        if (cursor.moveToFirst()) {
            caloriesRemaining = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return caloriesRemaining;
    }
    public double fetchUserOriginalWeight(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        double originalWeight = 0.0;

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_WEIGHT},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            originalWeight = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT));
            cursor.close();
        }
        db.close();
        return originalWeight;
    }
    public double fetchCaloriesPerWeekGoal(int userId) {
        UserDetails userDetails = getUserDetailsById(userId);
        if (userDetails == null) {
            return 0; // Return 0 if user details are not found
        }

        double bmr = calculateBMR(userDetails.gender, userDetails.age, userDetails.weight, userDetails.height);
        double caloriesPerWeekGoal = 0;

        switch (userDetails.weightGoal) {
            case "Gain 0.2 kg per week":
                caloriesPerWeekGoal = bmr + (7700 * 0.2) / 7; // 7700 calories to gain 1 kg, divided by 7 for daily surplus
                break;
            case "Gain 0.5 kg per week":
                caloriesPerWeekGoal = bmr + (7700 * 0.5) / 7;
                break;
            case "Lose 0.2 kg per week":
                caloriesPerWeekGoal = bmr - (7700 * 0.2) / 7;
                break;
            case "Lose 0.5 kg per week":
                caloriesPerWeekGoal = bmr - (7700 * 0.5) / 7;
                break;
            case "Maintain Weight":
            default:
                caloriesPerWeekGoal = bmr; // No adjustment needed
        }

        return caloriesPerWeekGoal * 7; // Return the weekly calorie goal
    }
    public double calculateWeightChangeForWeek(int userId) {

        double weeklyCalorieGoal = fetchCaloriesPerWeekGoal(userId);

        double dailyBMR = calculateBMRForCurrentUser();

        // Calculate the weekly BMR by multiplying daily BMR by 7
        double weeklyBMR = dailyBMR * 7;

        // Calculate the calorie surplus or deficit
        double calorieDifference = weeklyCalorieGoal - weeklyBMR;

        return calorieDifference / 7700;
    }

    public void updateUserStartDate(int userId, String startDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("StartDate", startDate); // Assuming you have a StartDate column in your UserDetails table
        db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public String fetchUserStartDate(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String startDate = null;

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_START_DATE},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            startDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE));
            cursor.close();
        }
        db.close();
        return startDate;
    }

    private double adjustBMRBasedOnWeightGoal(double bmr, String weightGoal) {
        switch (weightGoal) {
            case "Gain 0.2 kg per week":
                return bmr + 200;
            case "Gain 0.5 kg per week":
                return bmr + 500;
            case "Maintain Weight":
                return bmr;
            case "Lose 0.2 kg per week":
                return bmr - 200;
            case "Lose 0.5 kg per week":
                return bmr - 500;
            default:
                return bmr; // Default case if the weight goal is not recognized

        }
    }
    public void updateGoalAndCalories(String email, String newGoal) {
        UserDetails userDetails = getUserDetailsByEmail(email);
        if (userDetails != null) {
            double bmr = calculateBMR(userDetails.gender, userDetails.age, userDetails.weight, userDetails.height);
            double oldAdjustedCalories = adjustCaloriesBasedOnGoal(bmr, userDetails.weightGoal);
            double newAdjustedCalories = adjustCaloriesBasedOnGoal(bmr, newGoal);

            // Calculate the difference between the new and old daily calorie targets
            double dailyCalorieDifference = newAdjustedCalories - oldAdjustedCalories;

            // Adjust the current calories remaining with the difference
            int newCaloriesRemaining = userDetails.caloriesRemaining + (int) dailyCalorieDifference;

            // Update the database with the new goal and adjusted calories remaining
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_WEIGHT_GOAL, newGoal);
            values.put(COLUMN_CALORIES_REMAINING, newCaloriesRemaining);
            db.update(TABLE_NAME, values, COLUMN_EMAIL + "=?", new String[]{email});
            db.close();
        }
    }


    private double adjustCaloriesBasedOnGoal(double bmr, String goal) {
        switch (goal) {
            case "Gain 0.2 kg per week":
                return bmr + 200;
            case "Gain 0.5 kg per week":
                return bmr + 500;
            case "Lose 0.2 kg per week":
                return bmr - 200;
            case "Lose 0.5 kg per week":
                return bmr - 500;
            case "Maintain Weight":
            default:
                return bmr;
        }
    }


}







