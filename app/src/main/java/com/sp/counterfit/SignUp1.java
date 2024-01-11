package com.sp.counterfit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SignUp1 extends AppCompatActivity {

    private RadioGroup genderGroup;
    private EditText ageEdit, weightEdit, heightEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up1);

        // Initialize your UI components
        genderGroup = findViewById(R.id.genderGroup);
        ageEdit = findViewById(R.id.AgeEdit);
        weightEdit = findViewById(R.id.WeightEdit);
        heightEdit = findViewById(R.id.HeightEdit);
        Button nextButton1 = findViewById(R.id.nextButton);

        nextButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
                // Intent to start the next activity
                Intent intent = new Intent(SignUp1.this, SignUp2.class);
                startActivity(intent);
            }
        });
    }

    private void saveUserData() {
        int selectedId = genderGroup.getCheckedRadioButtonId();
        String age = ageEdit.getText().toString();
        String weight = weightEdit.getText().toString();
        String height = heightEdit.getText().toString();

        // Check if any field is empty or if no RadioButton is selected
        if (selectedId == -1 || age.isEmpty() || weight.isEmpty() || height.isEmpty()) {
            // Display toast message
            Toast.makeText(SignUp1.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        } else {
            // Your existing code to save user data
            RadioButton selectedGender = findViewById(selectedId);
            String gender = selectedGender.getText().toString();

            SharedPreferences sharedPref = getSharedPreferences("UserSignUpData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("Gender", gender);
            editor.putString("Age", age);
            editor.putString("Weight", weight);
            editor.putString("Height", height);
            editor.apply();

            // Intent to start the next activity
            Intent intent = new Intent(SignUp1.this, SignUp2.class);
            startActivity(intent);
        }
    }

}
