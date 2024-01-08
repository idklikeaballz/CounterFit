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
        RadioButton radioButtonLoseWeight=findViewById(R.id.radioButtonLoseWeight);
        RadioButton radioButtonMaintainWeight=findViewById(R.id.radioButtonMaintainWeight);
        Button buttonNext = findViewById(R.id.buttonNext);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_title_center);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.headerbg));
        }
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the "Gain Weight" radio button is selected
                if (radioButtonGainWeight.isChecked()) {
                    // Start the GainWeight activity
                    Intent intent = new Intent(SignUp2.this, GainWeight.class);
                    startActivity(intent);
                } else if (radioButtonLoseWeight.isChecked()) {
                    Intent intent1 = new Intent(SignUp2.this, LoseWeight.class);
                    startActivity(intent1);
                } else if (radioButtonMaintainWeight.isChecked()) {
                    Intent intent2 = new Intent(SignUp2.this,SignUp3.class);
                    startActivity(intent2);
                }else {
                    Toast.makeText(getApplicationContext(), "Please select an Option", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}