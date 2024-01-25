package com.sp.counterfit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private Button loginButton;
    private SignUpHelper dbHelper;
    private CheckBox rememberMeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_title_login);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.headerbg));
        }

        // Initialize UI components
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.PasswordEditText);
        rememberMeCheckBox = findViewById(R.id.checkbox_remember_me);
        loginButton = findViewById(R.id.nextButton);
        dbHelper = new SignUpHelper(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
        checkLoginStatus();
    }

    private void checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("IsLoggedIn", false);
        if (isLoggedIn) {
            // Retrieve email from shared preferences
            String email = sharedPreferences.getString("Email", null);

            // Proceed only if email is not null
            if (email != null) {
                Intent intent = new Intent(Login.this, Main.class);
                startActivity(intent);
                finish();
            }
        }
    }


    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = dbHelper.getUserByEmail(email);
        if (user != null && user.getPassword().equals(hashPassword(password))) {
            // Save login status with user details if "Remember Me" is checked
            saveLoginStatus(rememberMeCheckBox.isChecked(), email, hashPassword(password));

            // Pass the user details to Main activity
            SignUpHelper.UserDetails userDetails = dbHelper.getUserDetailsByEmail(email);
            if (userDetails != null) {
                dbHelper.setCurrentSession(userDetails.userId);
                Intent intent = new Intent(Login.this, Main.class);
                intent.putExtra("userDetails", userDetails); // Passing userDetails
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "User details not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveLoginStatus(boolean isLoggedIn, String email, String hashedPassword) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("IsLoggedIn", isLoggedIn);
        if (isLoggedIn && email != null) {
            editor.putString("Email", email); // Store email if logged in
            editor.putString("HashedPassword", hashedPassword); // Store hashed password
        } else {
            editor.remove("Email"); // Remove email if not logged in
            editor.remove("HashedPassword"); // Remove hashed password
        }
        editor.apply();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null; // Handle this case appropriately
        }
    }

}