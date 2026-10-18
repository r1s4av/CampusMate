package com.example.campusmate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;

public class ProfileActivity extends Activity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imgProfile, btnChangePhoto, btnBack;
    private TextView tvStudentName, tvRollNumber, tvStudentEmail;
    private TextView tvDepartment, tvSemester, tvGender, tvPhone;
    private Button btnEditProfile;

    private DatabaseHelper databaseHelper;

    private String currentEmail = "";
    private String studentName = "";
    private String rollNumber = "";
    private String studentEmail = "";
    private String department = "";
    private String semester = "";
    private String gender = "";
    private String phone = "";
    private String photoUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        databaseHelper = new DatabaseHelper(this);

        btnBack = findViewById(R.id.btnBack);
        imgProfile = findViewById(R.id.imgProfile);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);

        tvStudentName = findViewById(R.id.tvStudentName);
        tvRollNumber = findViewById(R.id.tvRollNumber);
        tvStudentEmail = findViewById(R.id.tvStudentEmail);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvSemester = findViewById(R.id.tvSemester);
        tvGender = findViewById(R.id.tvGender);
        tvPhone = findViewById(R.id.tvPhone);

        btnEditProfile = findViewById(R.id.btnEditProfile);

        btnBack.setOnClickListener(v -> finish());

        // Email passed from LoginActivity via HomeActivity
        String email = getIntent().getStringExtra("student_email");

        if (email == null || email.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    "Student email is missing. Please log in again.",
                    Toast.LENGTH_LONG
            ).show();
            finish();
            return;
        }

        currentEmail = email.trim();

        if (!loadProfile()) {
            Toast.makeText(
                    this,
                    "Student profile not found. Please register first.",
                    Toast.LENGTH_LONG
            ).show();
            finish();
            return;
        }

        btnChangePhoto.setOnClickListener(v -> openImagePicker());
        btnEditProfile.setOnClickListener(v -> showEditDialog());
    }

    private boolean loadProfile() {

        Cursor cursor = null;

        try {
            cursor = databaseHelper.getStudentProfile(currentEmail);

            if (cursor == null || !cursor.moveToFirst()) {
                return false;
            }

            studentName = readValue(cursor, "name");
            rollNumber = readValue(cursor, "roll_number");
            studentEmail = readValue(cursor, "email");
            department = readValue(cursor, "department");
            semester = readValue(cursor, "semester");
            gender = readValue(cursor, "gender");
            phone = readValue(cursor, "phone");
            photoUri = readValue(cursor, "photo_uri");

            displayProfile();
            loadProfilePhoto();

            return true;

        } catch (Exception e) {
            return false;

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String readValue(Cursor cursor, String column) {

        int index = cursor.getColumnIndex(column);

        if (index < 0 || cursor.isNull(index)) {
            return "";
        }

        String value = cursor.getString(index);
        return value == null ? "" : value;
    }

    private void displayProfile() {
        tvStudentName.setText(studentName);
        tvRollNumber.setText(rollNumber);
        tvStudentEmail.setText(studentEmail);
        tvDepartment.setText(department);
        tvSemester.setText(semester);
        tvGender.setText(gender);
        tvPhone.setText(phone);
    }

    private void loadProfilePhoto() {

        if (photoUri == null || photoUri.isEmpty()) {
            return;
        }

        try {
            imgProfile.setImageURI(Uri.parse(photoUri));
        } catch (Exception e) {
            // Keep default image if saved photo is unavailable
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != PICK_IMAGE_REQUEST
                || resultCode != RESULT_OK
                || data == null
                || data.getData() == null) {
            return;
        }

        Uri imageUri = data.getData();

        try {
            getContentResolver().takePersistableUriPermission(
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );
        } catch (SecurityException ignored) {}

        String newPhotoUri = imageUri.toString();

        boolean saved = databaseHelper.updateStudentPhoto(
                currentEmail,
                newPhotoUri
        );

        if (saved) {
            photoUri = newPhotoUri;
            imgProfile.setImageURI(imageUri);
            Toast.makeText(this, "Profile photo saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Could not save profile photo", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, 0, padding, 0);

        EditText nameInput = addField(layout, "Name", studentName);
        EditText rollInput = addField(layout, "Roll Number", rollNumber);
        EditText emailInput = addField(layout, "Email", studentEmail);
        EditText departmentInput = addField(layout, "Department", department);
        EditText semesterInput = addField(layout, "Semester (1-8)", semester);
        EditText genderInput = addField(layout, "Gender", gender);
        EditText phoneInput = addField(layout, "Phone", phone);

        semesterInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        emailInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        phoneInput.setInputType(android.text.InputType.TYPE_CLASS_PHONE);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Student Profile")
                .setView(layout)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
        ).setOnClickListener(v -> {

            String newName = nameInput.getText().toString().trim();
            String newRoll = rollInput.getText().toString().trim();
            String newEmail = emailInput.getText().toString().trim();
            String newDepartment = departmentInput.getText().toString().trim();
            String semesterText = semesterInput.getText().toString().trim();
            String newGender = genderInput.getText().toString().trim();
            String newPhone = phoneInput.getText().toString().trim();

            if (newName.isEmpty()) {
                nameInput.setError("Enter your name");
                return;
            }
            if (newRoll.isEmpty()) {
                rollInput.setError("Enter roll number");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                emailInput.setError("Enter a valid email");
                return;
            }
            if (newDepartment.isEmpty()) {
                departmentInput.setError("Enter your department");
                return;
            }

            int newSemester;
            try {
                newSemester = Integer.parseInt(semesterText);
                if (newSemester < 1 || newSemester > 8) {
                    semesterInput.setError("Semester must be between 1 and 8");
                    return;
                }
            } catch (NumberFormatException e) {
                semesterInput.setError("Enter a valid semester");
                return;
            }

            if (newGender.isEmpty()) {
                genderInput.setError("Enter your gender");
                return;
            }
            if (newPhone.isEmpty() || !Patterns.PHONE.matcher(newPhone).matches()) {
                phoneInput.setError("Enter a valid phone number");
                return;
            }

            boolean saved = databaseHelper.updateStudentProfile(
                    currentEmail,
                    newName,
                    newRoll,
                    newEmail,
                    newDepartment,
                    newSemester,
                    newGender,
                    newPhone,
                    photoUri
            );

            if (saved) {
                studentName = newName;
                rollNumber = newRoll;
                studentEmail = newEmail;
                department = newDepartment;
                semester = String.valueOf(newSemester);
                gender = newGender;
                phone = newPhone;
                currentEmail = newEmail;

                displayProfile();
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Could not save profile. Email or Roll Number may already be in use.", Toast.LENGTH_LONG).show();
            }
        }));

        dialog.show();
    }

    private EditText addField(LinearLayout layout, String hint, String value) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setSingleLine(true);
        input.setText(value);
        layout.addView(input);
        return input;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
