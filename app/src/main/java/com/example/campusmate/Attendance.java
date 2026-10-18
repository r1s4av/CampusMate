package com.example.campusmate;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Attendance extends AppCompatActivity {

    private Spinner subjectSpinner;
    private DatePicker attendanceDate;
    private RadioGroup statusGroup;
    private Button saveAttendanceBtn;
    private TextView attendanceListText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        dbHelper = new DatabaseHelper(this);

        subjectSpinner = findViewById(R.id.subjectSpinner);
        attendanceDate = findViewById(R.id.attendanceDate);
        statusGroup = findViewById(R.id.statusGroup);
        saveAttendanceBtn = findViewById(R.id.saveAttendance);
        attendanceListText = findViewById(R.id.attendanceList);

        String[] subjects = {"Data Structures", "Database Management Systems", "Computer Networks", "Operating Systems", "Software Engineering"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subjects);
        subjectSpinner.setAdapter(adapter);

        loadSavedAttendance();

        saveAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = subjectSpinner.getSelectedItem().toString();

                int day = attendanceDate.getDayOfMonth();
                int month = attendanceDate.getMonth() + 1;
                int year = attendanceDate.getYear();
                String date = day + "/" + month + "/" + year;

                int selectedId = statusGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(Attendance.this, "Please select attendance status", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedRadioButton = findViewById(selectedId);
                String status = selectedRadioButton.getText().toString();

                long result = dbHelper.insertAttendance(subject, date, status);
                if (result != -1) {
                    Toast.makeText(Attendance.this, "Attendance Saved Successfully!", Toast.LENGTH_SHORT).show();
                    loadSavedAttendance();
                } else {
                    Toast.makeText(Attendance.this, "Error saving attendance", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadSavedAttendance() {
        Cursor cursor = dbHelper.getAllAttendance();
        if (cursor != null && cursor.getCount() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            while (cursor.moveToNext()) {
                String subj = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
                String dt = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String stat = cursor.getString(cursor.getColumnIndexOrThrow("status"));

                stringBuilder.append("• ").append(subj).append("\n  Date: ").append(dt).append(" | Status: ").append(stat).append("\n\n");
            }
            attendanceListText.setText(stringBuilder.toString());
            cursor.close();
        } else {
            attendanceListText.setText("No records available");
        }
    }
}