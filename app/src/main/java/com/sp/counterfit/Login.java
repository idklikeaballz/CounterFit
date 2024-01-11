package com.sp.counterfit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class Login extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private Button loginButton;
    private SignUpHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Initialize UI components
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.PasswordEditText);
        loginButton = findViewById(R.id.nextButton);
        dbHelper = new SignUpHelper(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();


        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user exists in the database
        User user = dbHelper.getUserByEmail(email);
        if (user != null) {
            // Compare passwords (hash the entered password if you're storing hashed passwords)
            if (user.getPassword().equals(hashPassword(password))) {
                // Login successful
                // Navigate to the main activity or show a success message
            } else {
                // Password does not match
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        } else {
            // User not found
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    private String hashPassword(String password) {
        // Implement your password hashing here
        // This method should match the one you used when storing passwords
        return password; // Placeholder
    }
}
