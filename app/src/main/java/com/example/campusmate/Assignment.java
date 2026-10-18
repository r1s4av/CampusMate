package com.example.campusmate;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Assignment extends AppCompatActivity {

    private EditText titleInput, subjectInput, dueDateInput;
    private Button saveAssignmentBtn;
    private TextView assignmentListText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        dbHelper = new DatabaseHelper(this);

        titleInput = findViewById(R.id.assignmentTitleInput);
        subjectInput = findViewById(R.id.assignmentSubjectInput);
        dueDateInput = findViewById(R.id.assignmentDueDateInput);
        saveAssignmentBtn = findViewById(R.id.saveAssignmentBtn);
        assignmentListText = findViewById(R.id.assignmentListText);

        loadAssignments();

        saveAssignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleInput.getText().toString().trim();
                String subject = subjectInput.getText().toString().trim();
                String dueDate = dueDateInput.getText().toString().trim();

                if (title.isEmpty() || subject.isEmpty() || dueDate.isEmpty()) {
                    Toast.makeText(Assignment.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                long result = dbHelper.insertAssignment(title, subject, dueDate);
                if (result != -1) {
                    Toast.makeText(Assignment.this, "Assignment Added Successfully!", Toast.LENGTH_SHORT).show();
                    titleInput.setText("");
                    subjectInput.setText("");
                    dueDateInput.setText("");
                    loadAssignments();
                } else {
                    Toast.makeText(Assignment.this, "Error adding assignment", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadAssignments() {
        Cursor cursor = dbHelper.getAllAssignments();
        if (cursor != null && cursor.getCount() > 0) {
            StringBuilder sb = new StringBuilder();
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
                String dueDate = cursor.getString(cursor.getColumnIndexOrThrow("due_date"));
                int completed = cursor.getInt(cursor.getColumnIndexOrThrow("completed"));

                sb.append("• ").append(title).append(" (").append(subject).append(")\n")
                        .append("  Due: ").append(dueDate)
                        .append(" | Status: ").append(completed == 1 ? "Completed" : "Pending")
                        .append("\n\n");
            }
            assignmentListText.setText(sb.toString());
            cursor.close();
        } else {
            assignmentListText.setText("No assignments added yet");
        }
    }
}