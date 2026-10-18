package com.example.campusmate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

    private EditText etEmail, etPassword;
    private CheckBox cbRemember;
    private Button btnLogin;
    private TextView tvGoToRegister;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbRemember = findViewById(R.id.cbRemember);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // Registration screen open karein
        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            ));
        });

        // Login button
        btnLogin.setOnClickListener(v -> loginStudent());

        // Registration ke baad email automatically fill karein
        String registeredEmail =
                getIntent().getStringExtra("registered_email");

        if (registeredEmail != null) {
            etEmail.setText(registeredEmail);
            etPassword.requestFocus();
        }
    }

    private void loginStudent() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (email.isEmpty()
                || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Please enter your password");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError(
                    "Password must be at least 6 characters"
            );
            etPassword.requestFocus();
            return;
        }

        boolean valid;

        try {
            valid = databaseHelper.verifyStudentPassword(
                    email, password
            );
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Unable to verify login. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        if (!valid) {
            Toast.makeText(
                    this,
                    "Invalid email or password. Please register first if needed.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        Intent intent = new Intent(
                LoginActivity.this,
                HomeActivity.class
        );

        intent.putExtra("student_email", email);
        intent.putExtra("remember_me", cbRemember.isChecked());

        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
