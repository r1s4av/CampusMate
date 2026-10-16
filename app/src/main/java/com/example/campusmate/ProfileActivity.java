package com.example.campusmate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imgProfile, btnChangePhoto, btnBack;
    private TextView tvStudentName, tvStudentEmail, tvDepartment, tvSemester, tvGender, tvPhone;
    private Button btnEditProfile;

    private String studentName = "Aditi Sharma";
    private final String studentEmail = "aditi@college.edu";
    private String department = "Computer Science";
    private String semester = "5";
    private final String gender = "Female";
    private final String phone = "9876543210";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnBack = findViewById(R.id.btnBack);
        imgProfile = findViewById(R.id.imgProfile);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        tvStudentName = findViewById(R.id.tvStudentName);
        tvStudentEmail = findViewById(R.id.tvStudentEmail);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvSemester = findViewById(R.id.tvSemester);
        tvGender = findViewById(R.id.tvGender);
        tvPhone = findViewById(R.id.tvPhone);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        btnBack.setOnClickListener(v -> finish());

        displayProfile();

        btnChangePhoto.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

        btnEditProfile.setOnClickListener(v -> showEditDialog());
    }

    private void displayProfile() {
        tvStudentName.setText(studentName);
        tvStudentEmail.setText(studentEmail);
        tvDepartment.setText(department);
        tvSemester.setText(semester);
        tvGender.setText(gender);
        tvPhone.setText(phone);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            imgProfile.setImageURI(imageUri);
            Toast.makeText(this, "Profile photo updated", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditDialog() {
        EditText input = new EditText(this);
        input.setText(studentName);
        input.setHint("Enter name");

        new AlertDialog.Builder(this)
                .setTitle("Edit Profile Name")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        studentName = newName;
                        displayProfile();
                        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
