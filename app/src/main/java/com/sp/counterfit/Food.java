package com.sp.counterfit;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Food extends AppCompatActivity implements AddFoodAdapter.OnFoodItemClickListener {

    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;
    private AddFoodAdapter foodItemAdapter;
    private SignUpHelper dbHelper;
    private Button btnNewMeal;
    private ImageView mealImageView;
    private SearchView searchView;

    private String currentUserEmail;
    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 1;

    private final ActivityResultLauncher<Intent> mealActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if(uri != null){
                        // Explicitly set the flags for persistable permissions
                        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;

                        try {
                            // Request persistable permissions
                            getContentResolver().takePersistableUriPermission(uri, takeFlags);
                        } catch (SecurityException e) {
                            Log.e("FoodActivity", "Error taking persistable URI permission", e);
                        }

                        // Your existing code to handle the result
                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK, returnIntent);
                        loadFoodItems();
                    }
                }
            });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food);

        checkStoragePermission();
        setupUI();
        setupSearchView();

    }
    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadFoodItems();
        } else {
            Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupUI() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_food);
        setupBottomNavigationView();

        dbHelper = new SignUpHelper(this);

        // Correctly initialize the recyclerView only once
        recyclerView = findViewById(R.id.recyclerView_food);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnNewMeal = findViewById(R.id.btn_new_meal);
        btnNewMeal.setOnClickListener(view -> {
            Intent intent = new Intent(Food.this, AddMeal.class);
            mealActivityResultLauncher.launch(intent);
        });

        currentUserEmail = dbHelper.getCurrentUserEmail();

        // Correctly initializing the class field `searchView`
        searchView = findViewById(R.id.search_food); // This line was corrected

        loadFoodItems(); // Load food items initially
        setupActionBar();
    }


    private void setupBottomNavigationView() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.bot_home) {
                    startActivity(new Intent(Food.this, Main.class));
                    return true;
                } else if (id == R.id.bot_gym) {
                    startActivity(new Intent(Food.this, Gym.class));
                } else if (id == R.id.bot_food) {
                    // Current activity, do nothing
                }
                return false;
            });
            unselectBottomNavigationViewItems();
        } else {
            Log.e("Food", "BottomNavigationView not found in the layout.");
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_food);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.headerbg));
        }
    }

    private void unselectBottomNavigationViewItems() {
        int size = bottomNavigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            bottomNavigationView.getMenu().getItem(i).setCheckable(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        unselectBottomNavigationViewItems();

        loadFoodItems();
    }


    @Override
    public void onUpdateMealClick(FoodItem foodItem) {
        Intent intent = new Intent(this, AddMeal.class);
        intent.putExtra("mealId", foodItem.getId());
        intent.putExtra("mealName", foodItem.getName());
        intent.putExtra("calories", foodItem.getCalories());
        intent.putExtra("imageUri", foodItem.getImageUrl()); // Pass the image URI here
        mealActivityResultLauncher.launch(intent);
    }

    @Override
    public void onAddMealClick(FoodItem foodItem) {
        if (currentUserEmail != null) {
            int userId = dbHelper.getCurrentUserId();
            int caloriesRemaining = dbHelper.getCaloriesRemaining(currentUserEmail);
            int caloriesToAdd = Math.round((float) foodItem.getCalories());
            dbHelper.updateUserRemainingCalories(currentUserEmail, caloriesRemaining - caloriesToAdd);

            // Insert meal to meal history
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            dbHelper.insertMealHistory(userId, foodItem.getName(), caloriesToAdd, date);

            Toast.makeText(this, "Meal added to diet and history!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }
    }






    private void loadFoodItems() {
        int currentUserId = dbHelper.getCurrentUserId();
        if (currentUserId != -1) {
            List<FoodItem> foodItems = dbHelper.getFoodItemsByUserId(currentUserId);
            foodItemAdapter = new AddFoodAdapter(this, foodItems, this);
            recyclerView.setAdapter(foodItemAdapter);
            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.containsKey("imageUri")) {
                Uri imageUri = Uri.parse(extras.getString("imageUri"));
                Glide.with(this).load(imageUri).into(mealImageView);
            }
        }

    }
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                EdamamService.searchFood(query, new EdamamService.Callback() {
                    @Override
                    public void onResponse(List<FoodItem> foodItems) {
                        runOnUiThread(() -> showFoodDialog(foodItems));
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> Toast.makeText(Food.this, "Error: " + error, Toast.LENGTH_SHORT).show());
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void showFoodDialog(List<FoodItem> foodItems) {
        if(isFinishing()) {
            // Activity is finishing, do not proceed to show dialog
            return;
        }
        CharSequence[] foodNames = new CharSequence[foodItems.size()];
        for (int i = 0; i < foodItems.size(); i++) {
            foodNames[i] = foodItems.get(i).getName() + ": " + foodItems.get(i).getCalories() + " calories"; // Updated to show name and calories
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Food");
        builder.setItems(foodNames, (dialog, which) -> {
            FoodItem selectedFood = foodItems.get(which);
            Toast.makeText(this, selectedFood.getName() + " added!", Toast.LENGTH_SHORT).show();
            if (currentUserEmail != null) {
                int caloriesRemaining = dbHelper.getCaloriesRemaining(currentUserEmail);
                // Round calories to the nearest integer
                int caloriesToAdd = Math.round((float) selectedFood.getCalories());
                dbHelper.updateUserRemainingCalories(currentUserEmail, caloriesRemaining - caloriesToAdd);
                Toast.makeText(this, "Calories updated!", Toast.LENGTH_SHORT).show();
                // Prepare data intent for result
                Intent data = new Intent();
                data.putExtra("addedCalories", caloriesToAdd); // Pass the rounded value
                setResult(RESULT_OK, data);
                finish();
            }


        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
            // Ensure the dialog closes on the first press of "Cancel"
            dialog.dismiss();
        });
    }
}
