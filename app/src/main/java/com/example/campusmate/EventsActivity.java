package com.example.campusmate;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class EventsActivity extends Activity {

    private Button btnTechFestDetails;
    private Button btnWorkshopDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        btnTechFestDetails = findViewById(R.id.btnTechFestDetails);
        btnWorkshopDetails = findViewById(R.id.btnWorkshopDetails);

        btnTechFestDetails.setOnClickListener(v ->
                showEventDetails(
                        "Campus Tech Fest",
                        "Date: 25 October 2026\n" +
                        "Venue: Main Auditorium\n\n" +
                        "Join us for a campus technology festival " +
                        "featuring student projects and demonstrations."
                )
        );

        btnWorkshopDetails.setOnClickListener(v ->
                showEventDetails(
                        "AI and Innovation Workshop",
                        "Date: 30 October 2026\n" +
                        "Venue: Computer Lab\n\n" +
                        "Learn about artificial intelligence " +
                        "and explore innovative student projects."
                )
        );

        Toast.makeText(
                this,
                "Events loaded successfully",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void showEventDetails(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Close", (dialog, which) ->
                        dialog.dismiss()
                )
                .show();
    }
}
