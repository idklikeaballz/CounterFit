package com.sp.counterfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class About extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about); // Set the content view to your layout
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_about);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.headerbg));
        }

        // Initialize the bottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation_about);
        setupBottomNavigationView(); // Setup the bottom navigation
    }

    private void setupBottomNavigationView() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.bot_home) {
                        Intent intent = new Intent(About.this, Main.class);
                        item.setCheckable(true);
                        startActivity(intent);
                        return true;
                    } else if (id==R.id.bot_gym) {
                        Intent intent=new Intent(About.this, Gym.class);
                        item.setCheckable(true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        return true;

                    }
                    // Handle other navigation items if necessary
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

