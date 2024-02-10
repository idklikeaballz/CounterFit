package com.sp.counterfit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Account extends AppCompatActivity {

    private TextInputEditText emailEditTextUpdate, passwordEditTextUpdate, confirmPasswordEditTextUpdate;
    private AppCompatButton updateButton;
    private SignUpHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        dbHelper = new SignUpHelper(this);

        emailEditTextUpdate = findViewById(R.id.emailEditTextUpdate);
        passwordEditTextUpdate = findViewById(R.id.PasswordEditTextUpdate);
        confirmPasswordEditTextUpdate = findViewById(R.id.ConfirmPasswordUpdate);
        updateButton = findViewById(R.id.Update);

        displayCurrentUserDetails();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserDetails();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_account);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.headerbg));
        }
    }

    private void displayCurrentUserDetails() {
        String currentUserEmail = dbHelper.getCurrentUserEmail();
        if (currentUserEmail != null) {
            emailEditTextUpdate.setText(currentUserEmail);
            passwordEditTextUpdate.setText("********");
            confirmPasswordEditTextUpdate.setText("********");
        }
    }

    private void updateUserDetails() {
        String email = emailEditTextUpdate.getText().toString().trim();
        String newPassword = passwordEditTextUpdate.getText().toString().trim();
        String confirmNewPassword = confirmPasswordEditTextUpdate.getText().toString().trim();

        boolean isPasswordChangeAttempted = !newPassword.equals("********");

        if (isPasswordChangeAttempted && (newPassword.isEmpty() || !newPassword.equals(confirmNewPassword))) {
            Toast.makeText(this, "Passwords do not match or empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email is changed from the current email
        String currentUserEmail = dbHelper.getCurrentUserEmail();
        if (currentUserEmail != null && !email.isEmpty() && !email.equals(currentUserEmail)) {
            // Check if the new email is already in use
            if (dbHelper.isEmailExists(email)) {
                Toast.makeText(this, "Email is already in use by another account.", Toast.LENGTH_SHORT).show();
                return;
            } else {
                // Email is unique and can be updated
                if (currentUserEmail != null && !email.isEmpty() && !email.equals(currentUserEmail) && !dbHelper.isEmailExists(email)) {
                    dbHelper.updateUserEmail(email); // Assuming updateUserEmail will update the email of the logged-in user
                }
            }
        }

        if (isPasswordChangeAttempted) {
            String hashedPassword = hashPassword(newPassword);
            if (hashedPassword != null) {
                dbHelper.updateUserPassword(currentUserEmail, hashedPassword); // Use current email as identifier
                Toast.makeText(this, "User details updated successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Account.this, Main.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Failed to update password.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // If only email is updated
            Toast.makeText(this, "Email updated successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Account.this, Main.class);
            startActivity(intent);
        }
    }


    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
