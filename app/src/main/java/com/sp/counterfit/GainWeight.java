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
        goalRadioGroup = findViewById(R.id.gainweightRadioGroup);
        Button nextButton = findViewById(R.id.buttonNext1);

        // Receive data from SignUp2
        Intent receivedIntent = getIntent();
        String gender = receivedIntent.getStringExtra("gender");
        int age = receivedIntent.getIntExtra("age", 0);
        double weight = receivedIntent.getDoubleExtra("weight", 0.0);
        double height = receivedIntent.getDoubleExtra("height", 0.0);

        // Set the button click listener
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = goalRadioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedGoal = findViewById(selectedId);
                    String weightGoal = selectedGoal.getText().toString();

                    Intent intent = new Intent(GainWeight.this, SignUp3.class);
                    intent.putExtra("gender", gender);
                    intent.putExtra("age", age);
                    intent.putExtra("weight", weight);
                    intent.putExtra("height", height);
                    intent.putExtra("weightGoal", weightGoal );
                    startActivity(intent);
                } else {
                    Toast.makeText(GainWeight.this, "Please select a weight goal", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
