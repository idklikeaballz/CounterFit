package com.sp.counterfit;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.Date;

import java.util.Locale;

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityResultLauncher<Intent> recommendActivityResultLauncher;
    private ActivityResultLauncher<Intent> foodActivityResultLauncher;

    private int caloriesRemaining;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private TextView textRemainingCalories;
    private SeekBar slider;
    private TextView textTipOfTheDay; // TextView for the tip of the day
    private ImageButton closeTipButton; // Button to close the tip
    private SignUpHelper dbHelper;

    private RelativeLayout tipContainer; // RelativeLayout for the tip of the day
    private boolean isFirstLaunch;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private String currentUserEmail; // Declare it here so it's accessible throughout the class

    private SensorEventListener stepListener;
    private int stepCount = 0;
    private int lastReportedStepCount = 0; // Add this as a member variable
    private double baseBMR;
    private TextView stepsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        isFirstLaunch = true;
        dbHelper = new SignUpHelper(this);

        // Initialize UI components
        initializeUI();
        setupStepCounter();
        retrieveAndDisplayUserBMR();
        checkAndResetCaloriesDaily();
        calculateAndSetCaloriesForNewUser();
        loadProfileImage();
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        caloriesRemaining = prefs.getInt("CaloriesRemaining", 0); // 0 is the default value

        SharedPreferences appPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String lastShownDate = appPrefs.getString("LastShownDate", "");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (!lastShownDate.equals(currentDate)) {
            displayRandomTip();
            SharedPreferences.Editor editor = appPrefs.edit();
            editor.putString("LastShownDate", currentDate);
            editor.apply();
        } else {
            checkTipVisibility();
        }

        closeTipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textTipOfTheDay.setVisibility(View.GONE);
                closeTipButton.setVisibility(View.GONE);
                tipContainer.setVisibility(View.GONE);
                saveTipVisibilityState(false);
            }
        });
        setupStepCounter();

        displayRandomTip(); // Call this method to display a random tip
        retrieveAndDisplayUserBMR();
        setupActivityResultLaunchers();
        startTracking();



    }
    private void setupActivityResultLaunchers() {
        recommendActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            int addedCalories = data.getIntExtra("addedCalories", 0);
                            updateRemainingCalories(addedCalories);
                        }
                    }
                });
        foodActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            int addedCalories = data.getIntExtra("addedCalories", 0);
                            updateRemainingCalories(addedCalories);
                        }
                    }
                }
        );
    }


    private void initializeUI() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_topview);
        NavigationView botNavigationView = findViewById(R.id.nav_bottomview);
        bottomNavigationView = findViewById(R.id.bottom_navigation_about);
        textRemainingCalories = findViewById(R.id.textRemainingCalories);
        stepsTextView = findViewById(R.id.stepsTextView);
        slider = findViewById(R.id.seekBarCalories);
        textTipOfTheDay = findViewById(R.id.tipTextView);
        closeTipButton = findViewById(R.id.closeTipButton);
        tipContainer = findViewById(R.id.tipbg);
        this.currentUserEmail = dbHelper.getCurrentUserEmail();

        ImageView recoAddImageView = findViewById(R.id.reco_add);

        View headerView = navigationView.getHeaderView(0);
        ImageView profileIcon = headerView.findViewById(R.id.profileIcon);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        navigationView.setNavigationItemSelectedListener(this);
        botNavigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        recoAddImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Main.this, Recommend.class);
            recommendActivityResultLauncher.launch(intent);
        });
        if (profileIcon != null) {
            profileIcon.setOnClickListener(v -> {
                // Handle the profile icon click, launching the profile activity
                Intent intent = new Intent(Main.this, Profile.class); // Replace with your profile activity class
                startActivity(intent);
            });
        } else {
            // Handle the case where the ImageView is not found
            Log.e("Main", "Profile icon not found");
        }
        SignUpHelper dbHelper = new SignUpHelper(this);
        String currentUserEmail = dbHelper.getCurrentUserEmail();

        if (currentUserEmail != null) {
            int maxCalories = dbHelper.getCaloriesRemaining(currentUserEmail);

            // Update the maximum value of the SeekBar with the user's calories
            slider.setMax(maxCalories);
        }

        setupBottomNavigationView();
    }

    private void updateRemainingCalories(int addedCalories) {
        caloriesRemaining -= addedCalories; // Subtract added calories
        if (caloriesRemaining < 0) {
            updateSliderProgress(caloriesRemaining);
        }
        textRemainingCalories.setText(String.format(Locale.getDefault(), "%d Remaining", caloriesRemaining));
        updateSliderProgress(caloriesRemaining);

        // Save the updated caloriesRemaining in SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("CaloriesRemaining", caloriesRemaining);
        editor.apply();

        // Save the updated calories remaining in the database
        SignUpHelper dbHelper = new SignUpHelper(this);
        String currentUserEmail = dbHelper.getCurrentUserEmail();
        if (currentUserEmail != null) {
            dbHelper.updateCaloriesRemaining(currentUserEmail, caloriesRemaining);
        }
    }

    private void retrieveAndDisplayUserBMR() {
        SignUpHelper dbHelper = new SignUpHelper(this);
        String currentUserEmail = dbHelper.getCurrentUserEmail();

        if (currentUserEmail != null) {
            SignUpHelper.UserDetails userDetails = dbHelper.getUserDetailsByEmail(currentUserEmail);
            if (userDetails != null) {
                double bmr = calculateBMR(userDetails.gender, userDetails.age, userDetails.weight, userDetails.height);
                bmr = adjustBMRBasedOnWeightGoal(bmr, userDetails.weightGoal);

                // Set maximum value of SeekBar to calculated BMR
                slider.setMax((int) bmr);
                // Set initial progress of SeekBar to calculated calories remaining
                int caloriesRemaining = dbHelper.getCaloriesRemaining(currentUserEmail);
                slider.setProgress((int) (bmr - caloriesRemaining));
                if (caloriesRemaining >= 0) {
                    slider.setProgress(0);
                    slider.setProgress(slider.getMax() - caloriesRemaining); // Display remaining calories
                    textRemainingCalories.setText(String.format(Locale.getDefault(), "%d Remaining", caloriesRemaining));
                } else {
                    slider.setProgress(slider.getMax()); // Display as full if over the goal
                    textRemainingCalories.setText(String.format(Locale.getDefault(), "%d Over", Math.abs(caloriesRemaining)));
                }

                View headerView = navigationView.getHeaderView(0);
                TextView emailTextView = headerView.findViewById(R.id.emailTextView);
                emailTextView.setText(currentUserEmail);
            } else {
                textRemainingCalories.setText("User details not found");
                slider.setProgress(0);
            }
        } else {
            Log.d("Main", "No current user email found.");
            // Optional: Redirect to login screen or show error message
        }
    }

    private void updateSliderProgress(int calories) {
        slider.setEnabled(false);

        if (calories >= 0) {
            slider.setProgress(0);
            slider.setProgress(slider.getMax() - calories); // Display remaining calories
            textRemainingCalories.setText(String.format(Locale.getDefault(), "%d Remaining", calories));
        } else {
            slider.setProgress(slider.getMax()); // Display as full if over the goal
            textRemainingCalories.setText(String.format(Locale.getDefault(), "%d Over", Math.abs(calories)));
        }
    }

    private void checkAndResetCaloriesDaily() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String lastResetDate = prefs.getString("LastResetDate", "");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Log.d("CalorieReset", "Current Date: " + currentDate + ", Last Reset Date: " + lastResetDate);

        if (!currentDate.equals(lastResetDate)) {
            baseBMR = dbHelper.calculateBMRForCurrentUser(); // Make sure this method works correctly
            baseBMR = adjustBMRBasedOnWeightGoal(baseBMR, dbHelper.getWeightGoalForCurrentUser());
            caloriesRemaining = (int) Math.round(baseBMR); // Reset caloriesRemaining
            Log.d("CalorieReset", "Calories reset to: " + caloriesRemaining);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("LastResetDate", currentDate);
            editor.putInt("CaloriesRemaining", caloriesRemaining);
            editor.apply();
        } else {
            caloriesRemaining = prefs.getInt("CaloriesRemaining", (int) Math.round(baseBMR));
            Log.d("CalorieReset", "Calories for today: " + caloriesRemaining);
        }
        updateSliderProgress(caloriesRemaining); // Make sure this updates your UI correctly
    }


    private void calculateAndSetCaloriesForNewUser() {
        SignUpHelper dbHelper = new SignUpHelper(this);
        String currentUserEmail = dbHelper.getCurrentUserEmail();
        SharedPreferences appPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        caloriesRemaining = appPrefs.getInt("CaloriesRemaining", (int) Math.round(baseBMR));

        if (currentUserEmail != null) {
            baseBMR = dbHelper.calculateBMRForCurrentUser();
            baseBMR = adjustBMRBasedOnWeightGoal(baseBMR, dbHelper.getWeightGoalForCurrentUser());

            // Check if calories_remaining is null in the database
            Integer caloriesRemainingFromDB = dbHelper.getCaloriesRemaining(currentUserEmail);

            if (caloriesRemainingFromDB == null) {
                // Set a default value for caloriesRemaining for new users
                caloriesRemaining = (int) Math.round(baseBMR);
                dbHelper.updateCaloriesRemaining(currentUserEmail, caloriesRemaining);
            } else {
                // Use the value from the database for previously logged-in users
                caloriesRemaining = caloriesRemainingFromDB;
            }

            // Save the calculated or retrieved caloriesRemaining in shared preferences
            appPrefs.edit().putInt("CaloriesRemaining", caloriesRemaining).apply();

            // Update UI
            updateSliderProgress(caloriesRemaining);
        } else {
            Log.d("Main", "No current user email found.");
        }
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            Log.d("NavigationView", "Logout clicked");
            logoutUser();

        } else if (id == R.id.nav_home) {

        } else if (id == R.id.nav_about) {
            Log.d("NavigationView", "About clicked");
            Intent intent = new Intent(Main.this, About.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_gyms) {
            Intent intent = new Intent(Main.this, Gym.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;

        } else if (id == R.id.nav_food) {
            Intent intent = new Intent(Main.this, Food.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            foodActivityResultLauncher.launch(intent);
            return true;

        }else if (id== R.id.nav_settings){
            Intent intent = new Intent(Main.this, Setting.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true; // Indicate that the item click has been handled

    }

    private void setupBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_about);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.bot_home) {
                    return true;
                } else if (id == R.id.bot_gym) {
                    Intent intent = new Intent(Main.this, Gym.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.bot_food) {
                    Intent intent = new Intent(Main.this, Food.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    foodActivityResultLauncher.launch(intent);
                    return true;

                }
                return false;
            }
        });
    }

    private void logoutUser() {
        // Clear the current session from the database
        SignUpHelper dbHelper = new SignUpHelper(this);
        dbHelper.clearCurrentSession();

        // Clear the shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to the Login Activity
        Intent intent = new Intent(this, Home.class); // Replace with actual login activity class
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void displayRandomTip() {
        SharedPreferences appPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String lastTipDate = appPrefs.getString("LastTipDate", "");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        if (!currentDate.equals(lastTipDate)) {
            // Display the tip and update the last shown date
            SignUpHelper dbHelper = new SignUpHelper(this);
            String randomTip = dbHelper.getRandomTip();
            if (randomTip != null) {
                textTipOfTheDay.setText(randomTip);
            } else {
                textTipOfTheDay.setText("No tip for today. Stay tuned!");
            }

            SharedPreferences.Editor editor = appPrefs.edit();
            editor.putString("LastTipDate", currentDate);
            editor.apply();
        }
    }

    private void saveTipVisibilityState(boolean isVisible) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("IsTipVisible", isVisible);
        editor.apply();
    }

    private void checkTipVisibility() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isTipVisible = sharedPreferences.getBoolean("IsTipVisible", true);

        if (!isTipVisible) {
            tipContainer.setVisibility(View.GONE); // Hide the tip container if it was previously closed
        } else {
            displayRandomTip(); // Display the tip if it is visible
        }
    }


    private void setupStepCounter() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        int savedInitialStepCount = getInitialStepCount();
        if (stepCounterSensor != null) {
            stepListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    int totalStepsSinceReboot = (int) event.values[0];

                    if (isFirstLaunch) {
                        isFirstLaunch = false;
                        if (savedInitialStepCount == 0) {
                            saveInitialStepCount(totalStepsSinceReboot);
                        } else {
                            lastReportedStepCount = savedInitialStepCount;
                        }
                    }

                    int stepsTakenSinceLastUpdate = totalStepsSinceReboot - lastReportedStepCount;
                    lastReportedStepCount = totalStepsSinceReboot;

                    if (stepsTakenSinceLastUpdate > 0) {
                        updateCaloriesWithSteps(stepsTakenSinceLastUpdate);
                        if (caloriesRemaining >= 0) {
                            slider.setProgress(0);
                            slider.setProgress(slider.getMax() - caloriesRemaining); // Display remaining calories
                            textRemainingCalories.setText(String.format(Locale.getDefault(), "%d Remaining", caloriesRemaining));
                        } else {
                            slider.setProgress(slider.getMax()); // Display as full if over the goal
                            textRemainingCalories.setText(String.format(Locale.getDefault(), "%d Over", Math.abs(caloriesRemaining)));
                        }
                    }

                    stepsTextView.setText(String.valueOf(totalStepsSinceReboot - getInitialStepCount()));
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };
        } else {
            Log.d("Main", "Step Counter Sensor not available on this device.");
        }
    }
    private void saveInitialStepCount(int stepCount) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("InitialStepCount", stepCount);
        editor.apply();
    }
    // Example method to call when user starts tracking their weight


    private void updateCaloriesWithSteps(int newSteps) {
        double caloriesBurned = newSteps * 0.04;
        caloriesRemaining += caloriesBurned;
        updateSliderProgress(caloriesRemaining);
        textRemainingCalories.setText(String.format(Locale.getDefault(), "%d Remaining", caloriesRemaining));
    }
    private int getInitialStepCount() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getInt("InitialStepCount", 0);
    }
    private void calculateAndDisplayUpdatedBMR() {
        double updatedBMR = baseBMR + (stepCount * 0.04);
        caloriesRemaining = (int) Math.round(updatedBMR); // Update caloriesRemaining
        updateSliderProgress(caloriesRemaining);
        textRemainingCalories.setText(String.format(Locale.getDefault(), "%d Remaining", caloriesRemaining));
    }
    private double calculateBMR(String gender, int age, double weight, double height) {
        if ("Male".equalsIgnoreCase(gender)) {
            return 10 * weight + 6.25 * height * 100 - 5 * age + 5 + 500;
        } else {
            return 10 * weight + 6.25 * height * 100 - 5 * age - 161 + 300;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAndResetCaloriesDaily();
        displayUpdatedCalories();


        if (stepCounterSensor != null) {
            sensorManager.registerListener(stepListener, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    public void startTracking() {
        int userId = dbHelper.getCurrentUserId(); // Ensure this method correctly fetches the current user's ID
        if (userId != -1) {
            String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            dbHelper.updateUserStartDate(userId, startDate);
        } else {
            Log.e("Main", "User ID not found.");
        }
    }
    private void displayUpdatedCalories() {
        // Assume getUserDetailsByEmail is a method that fetches the current user's details
        SignUpHelper.UserDetails userDetails = dbHelper.getUserDetailsByEmail(currentUserEmail);
        if (userDetails != null) {
            caloriesRemaining = userDetails.caloriesRemaining;
            textRemainingCalories.setText(String.format(Locale.getDefault(), "%d Remaining", caloriesRemaining));
            updateSliderProgress(caloriesRemaining);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        checkAndResetCaloriesDaily();
        if (stepCounterSensor != null) {
            sensorManager.unregisterListener(stepListener);
        }
    }
    private void loadProfileImage() {
        SignUpHelper dbHelper = new SignUpHelper(this);
        String currentUserEmail = dbHelper.getCurrentUserEmail();
        if (currentUserEmail != null) {
            String imageUriString = dbHelper.getProfileImageUri(currentUserEmail);
            View headerView = navigationView.getHeaderView(0);
            ImageView profileIcon = headerView.findViewById(R.id.profileIcon);

            if (profileIcon != null) {
                if (imageUriString != null && !imageUriString.isEmpty()) {
                    Uri imageUri = Uri.parse(imageUriString);

                    Glide.with(this)
                            .load(imageUri)
                            .circleCrop() // Apply circle crop transformation
                            .into(profileIcon);
                } else {

                }
            }
        }
    }



}