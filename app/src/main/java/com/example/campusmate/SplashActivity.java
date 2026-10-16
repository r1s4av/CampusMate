package com.example.campusmate;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);

        // Logo fade-in animation
        Animation fade = new Animation() {
            {
                setDuration(1500);
                setFillAfter(true);
            }
        };

        logo.startAnimation(fade);

        // Open Login screen after 3 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(
                    SplashActivity.this,
                    LoginActivity.class
            );
            startActivity(intent);
            finish();
        }, 3000);
    }
}
