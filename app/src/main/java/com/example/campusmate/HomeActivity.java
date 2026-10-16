package com.example.campusmate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.cardview.widget.CardView;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HomeActivity extends Activity {

    CardView cardEvents, cardProfile, cardCampusInfo;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        cardEvents = findViewById(R.id.cardEvents);
        cardProfile = findViewById(R.id.cardProfile);
        cardCampusInfo = findViewById(R.id.cardCampusInfo);
        btnLogout = findViewById(R.id.btnLogout);

        cardEvents.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, EventsActivity.class);
            startActivity(intent);
        });

        cardProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        cardCampusInfo.setOnClickListener(v -> {
            String info = readCampusInfo();
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Campus Info & Hours")
                    .setMessage(info)
                    .setPositiveButton("OK", null)
                    .show();
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private String readCampusInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("campus_info.txt"))
            );
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            stringBuilder.append("Failed to load campus info.");
        }
        return stringBuilder.toString().trim();
    }
}
