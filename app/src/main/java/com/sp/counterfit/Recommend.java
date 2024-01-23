package com.sp.counterfit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Recommend extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MealAdapter adapter;
    private List<MealItem> mealItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommend);

        // Assuming you have ViewPager2 set up in your layout with the IDs healthyDietViewPager and bulkingDietViewPager
        setupViewPager(findViewById(R.id.healthyDietViewPager), getHealthyDietMeals());
        setupViewPager(findViewById(R.id.bulkingDietViewPager), getBulkingDietMeals());



        // You can set up page transformers or indicators as needed
    }
    private void setupViewPager(ViewPager2 viewPager, List<MealItem> meals) {
        MealAdapter adapter = new MealAdapter(meals);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2); // Determines how many pages should be retained to either side of the current page
    }

    private List<MealItem> getHealthyDietMeals() {
        List<MealItem> meals = new ArrayList<>();
        meals.add(new MealItem("Garlic & Parmesan Potato Soup", "620 Calories", R.drawable.healthy_meal1));
        meals.add(new MealItem("Caesar Salad", "470 Calories", R.drawable.healthy_meal2));
        meals.add(new MealItem("Salmon Bowl","324 Calories",R.drawable.healthy_meal3));
        meals.add(new MealItem("Summer Slice","388 Calories",R.drawable.healthy_meal4));

        // Add more meal items as needed
        return meals;
    }

    private List<MealItem> getBulkingDietMeals() {
        List<MealItem> meals = new ArrayList<>();
        meals.add(new MealItem("Double Cheeseburger", "762 Calories", R.drawable.bulking_meal1));
        meals.add(new MealItem("Supreme Pizza", "1276 Calories", R.drawable.bulking_meal2));
        meals.add(new MealItem("Lobster Mac & Cheese", "731 Calories", R.drawable.bulking_meal3));
        meals.add(new MealItem("Roast Chicken Pie", "751 Calories", R.drawable.bulking_meal4));
        // Add more meal items as needed
        return meals;
    }
}