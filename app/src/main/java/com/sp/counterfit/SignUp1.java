package com.sp.counterfit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
                if (validateAndSaveUserData()) {
                    // The data is now being validated and sent within the validateAndSaveUserData() method
                }
            }
        });
    }

    private boolean validateAndSaveUserData() {
        int selectedId = genderGroup.getCheckedRadioButtonId();
        String age = ageEdit.getText().toString();
        String weight = weightEdit.getText().toString();
        String height = heightEdit.getText().toString();

        if (selectedId == -1 || age.isEmpty() || weight.isEmpty() || height.isEmpty()) {
            Toast.makeText(SignUp1.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            RadioButton selectedGender = findViewById(selectedId);
            String gender = selectedGender.getText().toString();

            Intent intent = new Intent(SignUp1.this, SignUp2.class);
            // Convert and add the extras before starting the activity
            try {
                int ageInt = Integer.parseInt(age);
                double weightDouble = Double.parseDouble(weight);
                double heightDouble = Double.parseDouble(height);

                intent.putExtra("gender", gender);
                intent.putExtra("age", ageInt);
                intent.putExtra("weight", weightDouble);
                intent.putExtra("height", heightDouble);

                startActivity(intent);
                return true;
            } catch (NumberFormatException e) {
                // Handle the exception if the parsing fails
                Toast.makeText(SignUp1.this, "Please enter valid numbers for age, weight, and height", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }
}
