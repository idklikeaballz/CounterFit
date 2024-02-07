package com.sp.counterfit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Setting extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_setting);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.headerbg));
        }

        // Set click listeners for each TextView
        findViewById(R.id.Goals).setOnClickListener(this);
        findViewById(R.id.History).setOnClickListener(this);
        findViewById(R.id.Account).setOnClickListener(this);
        findViewById(R.id.About).setOnClickListener(this);
        findViewById(R.id.Workout).setOnClickListener(this);
        findViewById(R.id.Logout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        // Determine which TextView was clicked and create the corresponding Intent
        if (v.getId() == R.id.Goals) {
            intent = new Intent(this, Home.class);
        } else if (v.getId() == R.id.History) {
            intent = new Intent(this, History.class);
        } else if (v.getId() == R.id.Account) {
            intent = new Intent(this, Account.class);
        } else if (v.getId() == R.id.About) {
            intent = new Intent(this, About.class);
        } else if (v.getId() == R.id.Workout) {
            intent = new Intent(this, Home.class);
        } else if (v.getId() == R.id.Logout) {
            intent = new Intent(this, Home.class);
            logoutUser();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        // Start the activity if the intent was created
        if (intent != null) {
            startActivity(intent);
        }
    }
    private void logoutUser() {
        // Clear the current session from the database
        SignUpHelper dbHelper = new SignUpHelper(this);
        dbHelper.clearCurrentSession();

        // Clear the shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to the Login Activity
        Intent intent = new Intent(this, Home.class); // Replace with actual login activity class
        startActivity(intent);
        finish();
    }
}
