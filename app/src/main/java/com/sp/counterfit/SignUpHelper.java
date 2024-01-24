package com.sp.counterfit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Serializable;

public class SignUpHelper extends SQLiteOpenHelper {
    private static final String TIPS_TABLE_NAME = "Tips";
    private static final String COLUMN_TIP_ID = "id";
    private static final String COLUMN_TIP_TEXT = "text";

    private static final String DATABASE_NAME = "UserDatabase";
    private static final int DATABASE_VERSION = 5;
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
            + COLUMN_CALORIES_REMAINING + " INTEGER" // Added column for calories remaining
            + ")";


    public SignUpHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_SESSION_TABLE);
        db.execSQL(CREATE_TIPS_TABLE); // Create the tips table
        insertInitialTips(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Check if we are upgrading from a version prior to the introduction of the CurrentSession table
        if (oldVersion < 4) {
            // Only create the CurrentSession table if it doesn't exist
            db.execSQL(CREATE_SESSION_TABLE);

        }
        // Add more conditions here for other database upgrades
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

    // Class to hold user details
    // Class to hold user details
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



}







