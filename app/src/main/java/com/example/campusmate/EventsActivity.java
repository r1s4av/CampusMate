package com.example.campusmate;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class EventsActivity extends Activity {

    private ImageView btnBack;
    private Button btnTechFestDetails, btnWorkshopDetails, btnHackathonDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        btnBack = findViewById(R.id.btnBack);
        btnTechFestDetails = findViewById(R.id.btnTechFestDetails);
        btnWorkshopDetails = findViewById(R.id.btnWorkshopDetails);
        btnHackathonDetails = findViewById(R.id.btnHackathonDetails);

        btnBack.setOnClickListener(v -> finish());

        btnTechFestDetails.setOnClickListener(v ->
                showEventDetails(
                        "Tech Fest 2026",
                        "Date: 25 September 2026\n" +
                        "Venue: Main Auditorium\n\n" +
                        "Join us for a campus technology festival featuring student projects, robotics, and coding demonstrations."
                )
        );

        btnWorkshopDetails.setOnClickListener(v ->
                showEventDetails(
                        "Workshop on AI",
                        "Date: 28 September 2026\n" +
                        "Venue: Seminar Hall\n\n" +
                        "Learn about artificial intelligence, machine learning models, and explore innovative student projects."
                )
        );

        btnHackathonDetails.setOnClickListener(v ->
                showEventDetails(
                        "Hackathon",
                        "Date: 5 October 2026\n" +
                        "Venue: Computer Lab\n\n" +
                        "24-hour coding marathon. Build amazing solutions for real-world campus problems and win exciting prizes!"
                )
        );
    }

    private void showEventDetails(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
