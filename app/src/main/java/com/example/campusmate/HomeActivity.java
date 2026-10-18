package com.example.campusmate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HomeActivity extends Activity {

    private CardView cardEvents, cardProfile, cardCampusInfo, cardMap;
    private Button btnLogout;
    private String studentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get logged-in student's email
        studentEmail = getIntent().getStringExtra("student_email");

        // Initialize views
        cardEvents = findViewById(R.id.cardEvents);
        cardProfile = findViewById(R.id.cardProfile);
        cardCampusInfo = findViewById(R.id.cardCampusInfo);
        cardMap = findViewById(R.id.cardMap);
        btnLogout = findViewById(R.id.btnLogout);

        // Open Events screen
        cardEvents.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    EventsActivity.class
            );
            startActivity(intent);
        });

        // Open Profile screen and pass student email
        cardProfile.setOnClickListener(v -> {
            if (studentEmail == null || studentEmail.trim().isEmpty()) {
                Toast.makeText(
                        HomeActivity.this,
                        "Student email not found. Please login again.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Intent intent = new Intent(
                    HomeActivity.this,
                    ProfileActivity.class
            );

            intent.putExtra("student_email", studentEmail);
            startActivity(intent);
        });

        // Show Campus Information
        cardCampusInfo.setOnClickListener(v -> {
            String info = readCampusInfo();

            new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("Campus Info & Hours")
                    .setMessage(info)
                    .setPositiveButton("OK", null)
                    .show();
        });

        // Open Campus Map
        cardMap.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    MapActivity.class
            );
            startActivity(intent);
        });

        // Logout
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    LoginActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }

    private String readCampusInfo() {
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        getAssets().open("campus_info.txt"),
                        StandardCharsets.UTF_8
                ))) {

            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

        } catch (Exception e) {
            return "Failed to load campus information. "
                    + "Please check that campus_info.txt exists "
                    + "inside the app/src/main/assets folder.";
        }

        String info = stringBuilder.toString().trim();

        if (info.isEmpty()) {
            return "Campus information is currently unavailable.";
        }

        return info;
    }
}
