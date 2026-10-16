package com.example.campusmate;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity {

    private TextView tvStudentName;
    private TextView tvRollNumber;
    private TextView tvDepartment;
    private TextView tvSemester;
    private Button btnEditProfile;

    private String studentName = "Student Name";
    private String rollNumber = "Not available";
    private String department = "Not available";
    private String semester = "Not available";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvStudentName = findViewById(R.id.tvStudentName);
        tvRollNumber = findViewById(R.id.tvRollNumber);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvSemester = findViewById(R.id.tvSemester);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        displayProfile();

        btnEditProfile.setOnClickListener(v -> showEditDialog());
    }

    private void displayProfile() {
        tvStudentName.setText(studentName);
        tvRollNumber.setText(rollNumber);
        tvDepartment.setText(department);
        tvSemester.setText(semester);
    }

    private void showEditDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter student name");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(studentName.equals("Student Name") ? "" : studentName);

        new AlertDialog.Builder(this)
                .setTitle("Edit Student Name")
                .setMessage("Update your profile name.")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = input.getText().toString().trim();

                    if (newName.isEmpty()) {
                        Toast.makeText(this,
                                "Name cannot be empty",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        studentName = newName;
                        displayProfile();

                        Toast.makeText(this,
                                "Name updated",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
