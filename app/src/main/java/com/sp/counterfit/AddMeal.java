package com.sp.counterfit;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

public class AddMeal extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private TextInputEditText mealNameEditText;
    private TextInputEditText caloriesEditText;
    private ImageView mealImageView;
    private Button saveButton;
    private Button addButton;
    private Button deleteButton;
    private SignUpHelper dbHelper;
    private int mealId = -1; // Class variable to store the meal ID

    private Uri imageUri; // Uri for the selected or captured image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_meal);

        dbHelper = new SignUpHelper(this);
        mealNameEditText = findViewById(R.id.meal);
        caloriesEditText = findViewById(R.id.calories);
        mealImageView = findViewById(R.id.iv_meal_image);
        saveButton = findViewById(R.id.btn_save);
        addButton = findViewById(R.id.btn_add);
        deleteButton = findViewById(R.id.btn_delete);

        addButton.setOnClickListener(view -> chooseImageSource());
        saveButton.setOnClickListener(view -> saveMeal());
        deleteButton.setOnClickListener(view -> deleteMeal());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_food_meal);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.headerbg));
        }

        // Get data passed from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mealId = extras.getInt("mealId", -1);
            mealNameEditText.setText(extras.getString("mealName", ""));
            caloriesEditText.setText(String.valueOf(extras.getInt("calories", 0)));
            String imageUriString = extras.getString("imageUri", "");
            if (!imageUriString.isEmpty()) {
                imageUri = Uri.parse(imageUriString);
                Glide.with(this).load(imageUri).into(mealImageView); // Use Glide to load the image
            }
        }
        
    }

    private void chooseImageSource() {
        CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                captureImage();
            } else if (options[item].equals("Choose from Gallery")) {
                pickImageFromGallery();
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void captureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void pickImageFromGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (pickPhotoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data.getExtras() != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, "Title", null));
                mealImageView.setImageBitmap(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                imageUri = data.getData();
                mealImageView.setImageURI(imageUri);
            }
        }
    }


    private void saveMeal() {
        String mealName = mealNameEditText.getText().toString();
        String calorieStr = caloriesEditText.getText().toString();
        int calories = calorieStr.isEmpty() ? 0 : Integer.parseInt(calorieStr);

        if (mealName.isEmpty() || calories == 0 || imageUri == null) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageUriString = (imageUri != null) ? imageUri.toString() : "";

        if (mealId == -1) {
            // Insert the new meal and get its ID
            long newMealId = dbHelper.insertFoodItem(dbHelper.getCurrentUserId(), mealName, calories, imageUriString);
            if (newMealId == -1) {
                Toast.makeText(this, "Error saving meal", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Meal saved successfully", Toast.LENGTH_SHORT).show();
                // Prepare data to pass back to calling activity
                Intent data = new Intent();
                data.putExtra("mealId", newMealId); // Use the new meal ID
                data.putExtra("imageUri", imageUriString);
                setResult(RESULT_OK, data);
            }
        } else {
            // Update existing meal
            dbHelper.updateFoodItem(mealId, mealName, calories, imageUriString);
            // No need to pass back the meal ID since it hasn't changed
            setResult(RESULT_OK);
        }

        // Close this activity
        finish();
    }


    private void deleteMeal() {
        if (mealId != -1) {
            dbHelper.deleteFoodItem(mealId);
            // Notify the Food activity about the change
            setResult(RESULT_OK);
            resetFieldsAndFinish();
        } else {
            Toast.makeText(this, "No meal selected to delete", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetFieldsAndFinish() {
        mealNameEditText.setText("");
        caloriesEditText.setText("");
        mealImageView.setImageResource(R.drawable.add_image);
        finish(); // Close the activity after saving or deleting
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_CAPTURE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            captureImage();
        }
    }
}
