package com.example.campusmate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

    private EditText etName, etRollNumber, etEmail;
    private EditText etDepartment, etSemester, etGender;
    private EditText etPhone, etPassword, etConfirmPassword;

    private Button btnRegister;
    private TextView tvGoToLogin;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etRollNumber = findViewById(R.id.etRollNumber);
        etEmail = findViewById(R.id.etEmail);
        etDepartment = findViewById(R.id.etDepartment);
        etSemester = findViewById(R.id.etSemester);
        etGender = findViewById(R.id.etGender);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnRegister.setOnClickListener(v -> registerStudent());

        tvGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(
                    RegisterActivity.this,
                    LoginActivity.class
            );
            startActivity(intent);
            finish();
        });
    }

    private void registerStudent() {

        String name = etName.getText().toString().trim();
        String rollNumber = etRollNumber.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
        String semesterText = etSemester.getText().toString().trim();
        String gender = etGender.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword =
                etConfirmPassword.getText().toString();

        if (name.isEmpty()) {
            etName.setError("Enter your full name");
            etName.requestFocus();
            return;
        }

        if (rollNumber.isEmpty()) {
            etRollNumber.setError("Enter your roll number");
            etRollNumber.requestFocus();
            return;
        }

        if (email.isEmpty()
                || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (department.isEmpty()) {
            etDepartment.setError("Enter your department");
            etDepartment.requestFocus();
            return;
        }

        if (semesterText.isEmpty()) {
            etSemester.setError("Enter your semester");
            etSemester.requestFocus();
            return;
        }

        int semester;

        try {
            semester = Integer.parseInt(semesterText);

            if (semester < 1 || semester > 8) {
                etSemester.setError(
                        "Semester must be between 1 and 8"
                );
                etSemester.requestFocus();
                return;
            }

        } catch (NumberFormatException e) {
            etSemester.setError("Enter a valid semester number");
            etSemester.requestFocus();
            return;
        }

        if (gender.isEmpty()) {
            etGender.setError("Enter your gender");
            etGender.requestFocus();
            return;
        }

        if (phone.isEmpty()
                || !Patterns.PHONE.matcher(phone).matches()
                || phone.replaceAll("\\D", "").length() < 7) {
            etPhone.setError("Enter a valid phone number");
            etPhone.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError(
                    "Password must be at least 6 characters"
            );
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        boolean inserted;

        try {
            // Save student details and hashed password
            inserted = databaseHelper.insertStudent(
                    name,
                    rollNumber,
                    department,
                    semester,
                    email,
                    gender,
                    phone,
                    password
            );

        } catch (Exception e) {
            Toast.makeText(
                    RegisterActivity.this,
                    "Registration failed. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        if (inserted) {

            Toast.makeText(
                    RegisterActivity.this,
                    "Registration successful!",
                    Toast.LENGTH_LONG
            ).show();

            Intent intent = new Intent(
                    RegisterActivity.this,
                    LoginActivity.class
            );

            intent.putExtra("registered_email", email);

            startActivity(intent);
            finish();

        } else {
            Toast.makeText(
                    RegisterActivity.this,
                    "Registration failed. Email or roll number may already exist.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
