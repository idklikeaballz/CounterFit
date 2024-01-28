package com.sp.counterfit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class Food extends AppCompatActivity implements AddFoodAdapter.OnFoodItemClickListener {

    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;
    private AddFoodAdapter foodItemAdapter;
    private SignUpHelper dbHelper;
    private Button btnNewMeal;
    private String currentUserEmail;
    private final ActivityResultLauncher<Intent> mealActivityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            loadFoodItems();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food);

        setupUI();
    }

    private void setupUI() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_food);
        setupBottomNavigationView();

        dbHelper = new SignUpHelper(this);
        recyclerView = findViewById(R.id.recyclerView_food);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnNewMeal = findViewById(R.id.btn_new_meal);
        btnNewMeal.setOnClickListener(view -> {
            Intent intent = new Intent(Food.this, AddMeal.class);
            mealActivityResultLauncher.launch(intent);
        });

        currentUserEmail = dbHelper.getCurrentUserEmail();
        recyclerView = findViewById(R.id.recyclerView_food);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
        intent.putExtra("imageUri", foodItem.getImageUrl()); // Ensure this is correct

        mealActivityResultLauncher.launch(intent);
    }


    @Override
    public void onAddMealClick(FoodItem foodItem) {
        if (currentUserEmail != null) {
            int caloriesRemaining = dbHelper.getCaloriesRemaining(currentUserEmail);
            dbHelper.updateUserRemainingCalories(currentUserEmail, caloriesRemaining - foodItem.getCalories());
            Toast.makeText(this, "Calories updated!", Toast.LENGTH_SHORT).show();
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
        }
    }
}
