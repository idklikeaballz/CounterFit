package com.sp.counterfit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class GainWeight extends AppCompatActivity {

    private RadioGroup goalRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gain_weight);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_title_center);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.headerbg));
        }

        // Initialize RadioGroup and Button
        goalRadioGroup = findViewById(R.id.goalRadioGroup2);
        Button nextButton = findViewById(R.id.buttonNext1);

        // Set the button click listener
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWeightGoal();
            }
        });
    }

    private void saveWeightGoal() {
        // Get the selected RadioButton ID from the RadioGroup
        int selectedGoalId = goalRadioGroup.getCheckedRadioButtonId();
        if (selectedGoalId != -1) { // Check if any RadioButton is selected
            RadioButton selectedGoalButton = findViewById(selectedGoalId);
            String weightGoal = selectedGoalButton.getText().toString();
            // Insert or update the weight goal in the database
            SignUpHelper dbHelper = new SignUpHelper(this);
            int userId = getIntent().getIntExtra("UserId", -1);
            dbHelper.insertOrUpdateGoal(userId, weightGoal);

            // Optionally, provide feedback to the user
            Toast.makeText(this, "Weight goal saved!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(GainWeight.this, SignUp3.class);
            startActivity(intent);
        } else {
            // Notify the user to make a selection
            Toast.makeText(this, "Please select a weight goal", Toast.LENGTH_SHORT).show();
        }
    }
}
