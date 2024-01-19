package com.sp.counterfit;

import static android.app.ProgressDialog.show;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class SignUp2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up2);
        RadioButton radioButtonGainWeight = findViewById(R.id.radioButtonGainWeight);
        RadioButton radioButtonLoseWeight = findViewById(R.id.radioButtonLoseWeight);
        RadioButton radioButtonMaintainWeight = findViewById(R.id.radioButtonMaintainWeight);
        Button buttonNext = findViewById(R.id.buttonNext);

        // Receive data from SignUp1
        String gender = getIntent().getStringExtra("gender");
        int age = getIntent().getIntExtra("age", 0);
        double weight = getIntent().getDoubleExtra("weight", 0.0);
        double height = getIntent().getDoubleExtra("height", 0.0);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_title_center);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.headerbg));
        }
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (radioButtonGainWeight.isChecked()) {
                    intent = new Intent(SignUp2.this, GainWeight.class);
                } else if (radioButtonLoseWeight.isChecked()) {
                    intent = new Intent(SignUp2.this, LoseWeight.class);
                } else if (radioButtonMaintainWeight.isChecked()) {
                    intent = new Intent(SignUp2.this, SignUp3.class);
                    intent.putExtra("weightGoal", "Maintain Weight");
                } else {
                    Toast.makeText(getApplicationContext(), "Please select an Option", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Pass the received data to the next activity
                intent.putExtra("gender", gender);
                intent.putExtra("age", age);
                intent.putExtra("weight", weight);
                intent.putExtra("height", height);
                startActivity(intent);
            }
        });
    }
}