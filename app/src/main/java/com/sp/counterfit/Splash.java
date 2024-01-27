package com.sp.counterfit;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class Splash extends AppCompatActivity {
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // Initialize ImageView and TextView
        ImageView logo = findViewById(R.id.logoAnim);
        TextView text = findViewById(R.id.textAnim);

        // Load and start the animation
        Animation splashAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        logo.startAnimation(splashAnimation);
        text.startAnimation(splashAnimation);

        // Initialize MediaPlayer and start playing the sound
        mediaPlayer = MediaPlayer.create(this, R.raw.opening);
        mediaPlayer.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLoggedIn()) {
                    // User is logged in, go to main activity
                    Intent intent = new Intent(Splash.this, Main.class);
                    startActivity(intent);
                    finish();
                } else {
                    // User is not logged in, go to login activity
                    Intent intent = new Intent(Splash.this, Home.class);
                    startActivity(intent);
                    finish();
                }
                finish();
            }
        }, 3000);

        // Optional: Set a listener to stop and release the MediaPlayer once the sound is complete
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });

    }
    private boolean isLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("IsLoggedIn", false);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release MediaPlayer resources if the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}