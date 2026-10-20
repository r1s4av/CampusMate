package com.example.campusmate;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Assignment extends AppCompatActivity {

    private EditText titleInput, subjectInput, dueDateInput;
    private Button saveAssignmentBtn;
    private TextView assignmentListText;
    private LinearLayout assignmentContainer;
    private DatabaseHelper dbHelper;

    private String studentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        dbHelper = new DatabaseHelper(this);

        // Get the logged-in student's email
        studentEmail = getIntent().getStringExtra("student_email");

        // Connect XML views
        titleInput = findViewById(R.id.assignmentTitleInput);
        subjectInput = findViewById(R.id.assignmentSubjectInput);
        dueDateInput = findViewById(R.id.assignmentDueDateInput);
        saveAssignmentBtn = findViewById(R.id.saveAssignmentBtn);
        assignmentListText = findViewById(R.id.assignmentListText);

        // Container for dynamically created assignment checkboxes
        assignmentContainer = findViewById(R.id.assignmentContainer);

        // Display saved assignments
        loadAssignments();

        // Save button
        saveAssignmentBtn.setOnClickListener(v -> saveAssignment());
    }

    // Save a new assignment
    private void saveAssignment() {

        String title = titleInput.getText().toString().trim();
        String subject = subjectInput.getText().toString().trim();
        String dueDate = dueDateInput.getText().toString().trim();

        if (title.isEmpty() || subject.isEmpty() || dueDate.isEmpty()) {
            Toast.makeText(
                    Assignment.this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Save assignment for the logged-in student
        long result = dbHelper.insertAssignment(
                studentEmail,
                title,
                subject,
                dueDate
        );

        if (result != -1) {

            Toast.makeText(
                    Assignment.this,
                    "Assignment added successfully!",
                    Toast.LENGTH_SHORT
            ).show();

            titleInput.setText("");
            subjectInput.setText("");
            dueDateInput.setText("");

            loadAssignments();

        } else {

            Toast.makeText(
                    Assignment.this,
                    "Error adding assignment",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // Load saved assignments from SQLite
    private void loadAssignments() {

        Cursor cursor;

        if (studentEmail == null || studentEmail.trim().isEmpty()) {
            cursor = dbHelper.getAllAssignments();
        } else {
            cursor = dbHelper.getAllAssignments(studentEmail);
        }

        if (cursor == null) {
            assignmentListText.setText("Unable to load assignments.");
            return;
        }

        StringBuilder sb = new StringBuilder();

        if (assignmentContainer != null) {
            assignmentContainer.removeAllViews();
        }

        try {

            if (cursor.getCount() == 0) {

                assignmentListText.setText("No assignments added yet");

            } else {

                while (cursor.moveToNext()) {

                    int id = cursor.getInt(
                            cursor.getColumnIndexOrThrow("id")
                    );

                    String title = cursor.getString(
                            cursor.getColumnIndexOrThrow("title")
                    );

                    String subject = cursor.getString(
                            cursor.getColumnIndexOrThrow("subject")
                    );

                    String dueDate = cursor.getString(
                            cursor.getColumnIndexOrThrow("due_date")
                    );

                    int completed = cursor.getInt(
                            cursor.getColumnIndexOrThrow("completed")
                    );

                    sb.append(title)
                            .append(" (")
                            .append(subject)
                            .append(")\n")
                            .append("Due: ")
                            .append(dueDate)
                            .append(" | Status: ")
                            .append(completed == 1
                                    ? "Completed"
                                    : "Pending")
                            .append("\n\n");

                    // Create assignment details and checkbox
                    if (assignmentContainer != null) {

                        LinearLayout itemLayout =
                                new LinearLayout(this);

                        itemLayout.setOrientation(
                                LinearLayout.VERTICAL
                        );

                        itemLayout.setPadding(12, 12, 12, 12);

                        TextView details = new TextView(this);

                        details.setText(
                                title
                                        + "\nSubject: " + subject
                                        + "\nDue date: " + dueDate
                        );

                        details.setTextSize(16);

                        CheckBox completedCheckBox =
                                new CheckBox(this);

                        completedCheckBox.setText("Completed");
                        completedCheckBox.setChecked(completed == 1);

                        // Save checkbox changes in the database
                        completedCheckBox.setOnCheckedChangeListener(
                                (buttonView, isChecked) -> {

                                    int rowsUpdated =
                                            dbHelper.markAssignmentCompleted(
                                                    id,
                                                    isChecked
                                            );

                                    if (rowsUpdated > 0) {

                                        Toast.makeText(
                                                Assignment.this,
                                                isChecked
                                                        ? "Marked as completed"
                                                        : "Marked as pending",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        loadAssignments();

                                    } else {

                                        Toast.makeText(
                                                Assignment.this,
                                                "Unable to update assignment",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                }
                        );

                        itemLayout.addView(details);
                        itemLayout.addView(completedCheckBox);

                        assignmentContainer.addView(itemLayout);
                    }
                }

                assignmentListText.setText(sb.toString());
            }

        } finally {
            cursor.close();
        }
    }
}
