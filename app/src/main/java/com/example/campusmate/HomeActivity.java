package com.example.campusmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;

public class HomeActivity extends ComponentActivity {

    private CardView cardAttendance, cardAssignments;
    private CardView cardEvents, cardProfile, cardCampusInfo, cardMap;
    private Button btnLogout;

    private String studentEmail;
    private boolean isCampusInfoOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        studentEmail = getIntent().getStringExtra("student_email");

        // Handle Back button using AndroidX
        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (isCampusInfoOpen) {
                            showDashboard();
                        } else {
                            finish();
                        }
                    }
                }
        );

        showDashboard();
    }

    // Display the dashboard
    private void showDashboard() {

        isCampusInfoOpen = false;

        setContentView(R.layout.activity_home);

        // Initialize dashboard views
        cardAttendance = findViewById(R.id.cardAttendance);
        cardAssignments = findViewById(R.id.cardAssignments);
        cardEvents = findViewById(R.id.cardEvents);
        cardProfile = findViewById(R.id.cardProfile);
        cardCampusInfo = findViewById(R.id.cardCampusInfo);
        cardMap = findViewById(R.id.cardMap);
        btnLogout = findViewById(R.id.btnLogout);

        // Open Attendance screen
        cardAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    Attendance.class
            );
            intent.putExtra("student_email", studentEmail);
            startActivity(intent);
        });

        // Open Assignments screen
        cardAssignments.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    Assignment.class
            );
            intent.putExtra("student_email", studentEmail);
            startActivity(intent);
        });

        // Open Events screen
        cardEvents.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    EventsActivity.class
            );
            startActivity(intent);
        });

        // Open Profile screen
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

        // Display Campus Info XML in the same Activity
        cardCampusInfo.setOnClickListener(v -> {
            isCampusInfoOpen = true;
            setContentView(R.layout.activity_campus_info);
        });

        // Open Campus Map separately
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
}
