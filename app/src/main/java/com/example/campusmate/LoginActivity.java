
package com.example.campusmate;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.Toast;
import android.util.Patterns;

public class LoginActivity extends Activity {

    EditText etEmail, etPassword;
    CheckBox cbRemember;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbRemember = findViewById(R.id.cbRemember);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,
                        "Please enter email and password",
                        Toast.LENGTH_SHORT).show();
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Enter a valid email");
            }
            else if (password.length() < 6) {
                etPassword.setError(
                        "Password must be at least 6 characters");
            }
            else {
                Toast.makeText(this,
                        "Login successful",
                        Toast.LENGTH_SHORT).show();

                Intent i = new Intent(
                        LoginActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
