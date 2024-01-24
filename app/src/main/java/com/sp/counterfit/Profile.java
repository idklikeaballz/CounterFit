package com.sp.counterfit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class Profile extends AppCompatActivity {
    private static final int IMAGE_PICK_CODE = 1000; // Define the request code

    private ImageView imageViewProfile;
    private EditText editEmail;
    private Button buttonUpdate;
    private TextView textViewChangePicture;
    private Uri imageUri; // Uri for the selected or captured image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_profile);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.headerbg));

        }
        imageViewProfile = findViewById(R.id.imageViewProfile);
        editEmail = findViewById(R.id.editEmail);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        textViewChangePicture = findViewById(R.id.textViewChangePicture);

        // Load current user's email
        loadCurrentUserEmail();
        loadCurrentProfileImage();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmailInDatabase();
                saveProfilePicture();

                // Start Main activity
                Intent intent = new Intent(Profile.this, Main.class);
                startActivity(intent);
                finish(); // Optionally call finish() if you don't want this activity in the back stack
            }
        });


        textViewChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }
    // In Profile.java

    private void loadCurrentUserEmail() {
        SignUpHelper dbHelper = new SignUpHelper(this);
        String currentUserEmail = dbHelper.getCurrentUserEmail();
        if (currentUserEmail != null) {
            editEmail.setText(currentUserEmail);
        }
    }

    private void updateEmailInDatabase() {
        String newEmail = editEmail.getText().toString();
        if (!newEmail.isEmpty() /* && other validations if needed */) {
            SignUpHelper dbHelper = new SignUpHelper(this);
            dbHelper.updateUserEmail(newEmail);
        } else {
            // Handle invalid input
        }
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }


    private void saveProfilePicture() {
        if (imageUri != null) {
            SignUpHelper dbHelper = new SignUpHelper(this);
            String currentUserEmail = dbHelper.getCurrentUserEmail();

            if (currentUserEmail != null) {
                dbHelper.updateProfileImageUri(currentUserEmail, imageUri.toString());
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null) {
            imageUri = data.getData();

            Glide.with(this)
                    .load(imageUri)
                    .circleCrop() // Apply circle crop transformation
                    .into(imageViewProfile);

            saveProfilePicture();
        }
    }
    private void loadCurrentProfileImage() {
        SignUpHelper dbHelper = new SignUpHelper(this);
        String currentUserEmail = dbHelper.getCurrentUserEmail();

        if (currentUserEmail != null) {
            String currentImageUriString = dbHelper.getProfileImageUri(currentUserEmail);
            if (currentImageUriString != null && !currentImageUriString.isEmpty()) {
                Uri currentImageUri = Uri.parse(currentImageUriString);
                Glide.with(this)
                        .load(currentImageUri)
                        .circleCrop()
                        .into(imageViewProfile);
            }
        }


}}






