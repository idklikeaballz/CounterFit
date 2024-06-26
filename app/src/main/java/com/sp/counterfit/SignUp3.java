package com.sp.counterfit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignUp3 extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText, confirmPasswordEditText;
    private SignUpHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up3);
        dbHelper = new SignUpHelper(this);

        // Setting up the custom action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_create_account);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.headerbg));
        }
        // Initialize input fields
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.PasswordEditText);
        confirmPasswordEditText = findViewById(R.id.ConfirmPassword);

        // Initialize the button and set up a click listener
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });

    }

    private void attemptSignUp() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Receive data from previous activity
        Intent receivedIntent = getIntent();
        String gender = receivedIntent.getStringExtra("gender");
        int age = receivedIntent.getIntExtra("age", 0);
        double weight = receivedIntent.getDoubleExtra("weight", 0.0);
        double height = receivedIntent.getDoubleExtra("height", 0.0);
        String weightGoal = receivedIntent.getStringExtra("weightGoal");

        if (!validateInputs(email, password, confirmPassword)) {
            return;
        }

        if (dbHelper.isEmailExists(email)) {
            emailEditText.setError("Email already in use");
            return;
        }

        // Hash the password
        String hashedPassword = hashPassword(password);

        dbHelper.insertUserDetails(gender, age, weight, height, email, hashedPassword, weightGoal);

        // Calculate and set the initial remaining calories for the new user
        double initialBMR = calculateBMR(gender, age, weight, height);
        int initialCalories = (int) Math.round(initialBMR);
        updateUserRemainingCalories(email, initialCalories);

        // Show a success message or navigate to the next screen
        Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SignUp3.this, Login.class);
        startActivity(intent);
    }
    private double calculateBMR(String gender, int age, double weight, double height) {
        if ("Male".equalsIgnoreCase(gender)) {
            return 10 * weight + 6.25 * height * 100 - 5 * age + 5 + 500;
        } else {
            return 10 * weight + 6.25 * height * 100 - 5 * age - 161 + 300;
        }
    }
    private void updateUserRemainingCalories(String userEmail, int remainingCalories) {
        // Update user's remaining calories in the database
        // Use your database helper class (dbHelper) to update the user's remaining calories in the database
        dbHelper.updateUserRemainingCalories(userEmail, remainingCalories);
    }





    private boolean validateInputs(String email, String password, String confirmPassword) {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email address");
            return false;
        }

        if (password.isEmpty() || password.length() < 10) {
            passwordEditText.setError("Password must be at least 10 characters");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return false;
        }

        return true;
    }



    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
