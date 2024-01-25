package com.sp.counterfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class Gym extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gym);
        List<GymItem> gymItems = new ArrayList<>();
        gymItems.add(new GymItem("Gym Name", "Gym Address", R.drawable.bulking_meal1, 1.3554, 103.8512));
// Ensure gymItems is not empty




        bottomNavigationView = findViewById(R.id.bottom_navigation_gym);
        setupBottomNavigationView(); // Setup the bottom navigation
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_gym);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.headerbg));
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView_gym);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        GymAdapter adapter = new GymAdapter(gymItems);
        recyclerView.setAdapter(adapter);


    }
    private void setupBottomNavigationView() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.bot_home) {
                        Intent intent = new Intent(Gym.this, Main.class);
                        item.setCheckable(true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        return true;
                    } else if (id==R.id.bot_gym) {
                        item.setCheckable(true);
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

    @Override
    protected void onResume() {
        super.onResume();
        // Unselect all items when the activity resumes
        unselectBottomNavigationViewItems();
    }
}