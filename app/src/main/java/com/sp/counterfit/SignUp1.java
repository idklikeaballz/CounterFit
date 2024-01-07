package com.sp.counterfit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignUp1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up1);
        Button nextButton1 = findViewById(R.id.nextButton); // Replace with your actual button ID if different
        nextButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to start SignUp1 activity
                Intent intent = new Intent(SignUp1.this, SignUp2.class);
                startActivity(intent);
            }
        });
    }
    }
