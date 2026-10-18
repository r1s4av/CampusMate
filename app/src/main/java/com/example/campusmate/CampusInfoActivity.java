package com.example.campusmate;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CampusInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setTextSize(18);
        textView.setPadding(32, 32, 32, 32);

        setContentView(textView);

        try {
            InputStream inputStream = getAssets().open("campus_info.txt");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream)
            );

            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            reader.close();
            textView.setText(content.toString());

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Unable to read campus information",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
