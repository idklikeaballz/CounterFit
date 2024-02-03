package com.sp.counterfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class Recommend extends AppCompatActivity implements MealAdapter.OnMealClickListener {
    private ViewPager2 healthyDietViewPager, bulkingDietViewPager;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommend);

        // Initialize ViewPager2 elements
        healthyDietViewPager = findViewById(R.id.healthyDietViewPager);
        bulkingDietViewPager = findViewById(R.id.bulkingDietViewPager);
        bottomNavigationView = findViewById(R.id.bottom_navigation_recommend);
        setupBottomNavigationView();

        // Setup ViewPager2 with adapters
        setupViewPager(healthyDietViewPager, getHealthyDietMeals());
        setupViewPager(bulkingDietViewPager, getBulkingDietMeals());
    }

    private void setupViewPager(ViewPager2 viewPager, List<MealItem> meals) {
        MealAdapter adapter = new MealAdapter(meals, this);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
    }

    @Override
    public void onMealClick(final MealItem mealItem) {
        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Add Meal")
                .setMessage("Do you want to add " + mealItem.getMealName() + " to your diet?")
                .setPositiveButton("Add", (dialog, which) -> addMealToDiet(mealItem))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addMealToDiet(MealItem mealItem) {
        int calories = Integer.parseInt(mealItem.getCalorieCount().split(" ")[0]);

        // Prepare data intent for result
        Intent data = new Intent();
        data.putExtra("addedCalories", calories);

        // Set result and finish the activity
        setResult(RESULT_OK, data);
        finish();
    }

    private List<MealItem> getHealthyDietMeals() {
        // Add healthy diet meals
        List<MealItem> meals = new ArrayList<>();
        meals.add(new MealItem("Garlic & Parmesan Potato Soup", "620 Calories", R.drawable.healthy_meal1));
        meals.add(new MealItem("Caesar Salad", "470 Calories", R.drawable.healthy_meal2));
        meals.add(new MealItem("Salmon Bowl","324 Calories",R.drawable.healthy_meal3));
        meals.add(new MealItem("Summer Slice","388 Calories",R.drawable.healthy_meal4));
        return meals;
    }

    private List<MealItem> getBulkingDietMeals() {
        // Add bulking diet meals
        List<MealItem> meals = new ArrayList<>();
        meals.add(new MealItem("Double Cheeseburger", "762 Calories", R.drawable.bulking_meal1));
        meals.add(new MealItem("Supreme Pizza", "1276 Calories", R.drawable.bulking_meal2));
        meals.add(new MealItem("Lobster Mac & Cheese", "731 Calories", R.drawable.bulking_meal3));
        meals.add(new MealItem("Roast Chicken Pie", "751 Calories", R.drawable.bulking_meal4));
        return meals;
    }
    private void setupBottomNavigationView() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.bot_home) {
                        Intent intent = new Intent(Recommend.this, Main.class);
                        item.setCheckable(true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        return true;
                    } else if (id==R.id.bot_gym) {
                        Intent intent = new Intent(Recommend.this, Gym.class);
                        item.setCheckable(true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        return true;
                    }else if (id == R.id.bot_food) {
                        Intent intent = new Intent(Recommend.this, Food.class);
                        item.setCheckable(true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        return true;
                    }
                    return false;
                }
            });
            unselectBottomNavigationViewItems();


        } else {
            Log.e("AboutActivity", "BottomNavigationView not found in the layout.");
        }
    }
    private void unselectBottomNavigationViewItems() {
        // We can loop through all menu items and uncheck them
        int size = bottomNavigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            bottomNavigationView.getMenu().getItem(i).setCheckable(false);
        }
    }
}
