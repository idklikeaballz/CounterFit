package com.sp.counterfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Date;

import java.util.Locale;

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private TextView textRemainingCalories;
    private SeekBar slider;
    private TextView textTipOfTheDay; // TextView for the tip of the day
    private ImageButton closeTipButton; // Button to close the tip

    private RelativeLayout tipContainer; // RelativeLayout for the tip of the day
    private boolean isFirstLaunch;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private SensorEventListener stepListener;
    private int stepCount = 0;
    private double baseBMR;
    private TextView stepsTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        isFirstLaunch = true;

        // Initialize UI components
        initializeUI();
        setupStepCounter();
        retrieveAndDisplayUserBMR();


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

    }

    private void initializeUI() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_topview);
        NavigationView botNavigationView = findViewById(R.id.nav_bottomview);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        textRemainingCalories = findViewById(R.id.textRemainingCalories);
        stepsTextView = findViewById(R.id.stepsTextView);
        slider = findViewById(R.id.seekBarCalories);
        textTipOfTheDay = findViewById(R.id.tipTextView);
        closeTipButton = findViewById(R.id.closeTipButton);
        tipContainer = findViewById(R.id.tipbg);
        ImageView recoAddImageView = findViewById(R.id.reco_add);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        navigationView.setNavigationItemSelectedListener(this);
        botNavigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        recoAddImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, Recommend.class);
                startActivity(intent);
            }
        });

        setupBottomNavigationView();
    }
    private void setupStep(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            setupStepCounter();
        }
    }

    private void retrieveAndDisplayUserBMR() {
        SignUpHelper dbHelper = new SignUpHelper(this);
        String currentUserEmail = dbHelper.getCurrentUserEmail();

        if (currentUserEmail != null) {
            SignUpHelper.UserDetails userDetails = dbHelper.getUserDetailsByEmail(currentUserEmail);
            if (userDetails != null) {
                baseBMR = calculateBMR(userDetails.gender, userDetails.age, userDetails.weight, userDetails.height);
                baseBMR = adjustBMRBasedOnWeightGoal(baseBMR, userDetails.weightGoal);

                // Rounding to the nearest whole number
                int roundedBMR = (int) Math.round(baseBMR);

                slider.setMax(roundedBMR);
                slider.setProgress(0);
                slider.setFocusable(false);
                slider.setClickable(false);
                slider.setEnabled(false);
                textRemainingCalories.setText(String.format(Locale.getDefault(), "%d kcal Remaining", roundedBMR));
            } else {
                textRemainingCalories.setText("User details not found");
                slider.setProgress(0);
            }
        } else {
            Log.d("Main", "No current user email found.");
        }
    }


    private double adjustBMRBasedOnWeightGoal(double bmr, String weightGoal) {
        switch (weightGoal) {
            case "Gain 0.2 kg per week": return bmr + 200;
            case "Gain 0.5 kg per week": return bmr + 500;
            case "Maintain Weight": return bmr;
            case "Lose 0.2 kg per week": return bmr - 200;
            case "Lose 0.5 kg per week": return bmr - 500;
            default: return bmr;
        }
    }

    private double calculateBMR(String gender, int age, double weight, double height) {
        if ("Male".equalsIgnoreCase(gender)) {
            return 10 * weight + 6.25 * height * 100 - 5 * age + 5+500;
        } else {
            return 10 * weight + 6.25 * height * 100 - 5 * age - 161 + 300;
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            Log.d("NavigationView", "Logout clicked");
            logoutUser();
        } else if (id == R.id.nav_home) {
            Intent intent = new Intent(this, Main.class);
            startActivity(intent);
        }

        item.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void setupBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id== R.id.bot_home){
                    return true;
                } else if (id==R.id.bot_food) {
                    Intent intent = new Intent(Main.this, Home.class);
                    startActivity(intent);
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
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            stepListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                        stepCount = (int) event.values[0];
                        stepsTextView.setText(String.valueOf(stepCount)); // Update steps count on the TextView
                        calculateAndDisplayUpdatedBMR();
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    // Optional: Handle sensor accuracy changes
                }
            };
            sensorManager.registerListener(stepListener, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }
    private void calculateAndDisplayUpdatedBMR() {
        double updatedBMR = baseBMR + (stepCount * 0.04);
        int roundedBMR = (int) Math.round(updatedBMR); // Round to nearest whole number
        textRemainingCalories.setText(String.format(Locale.getDefault(), "%d Remaining", roundedBMR));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stepCounterSensor != null) {
            sensorManager.registerListener(stepListener, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (stepCounterSensor != null) {
            sensorManager.unregisterListener(stepListener);
        }
    }

}



