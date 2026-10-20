
package com.example.campusmate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "campusmate.db";
    private static final int DATABASE_VERSION = 4;

    private static final String TABLE_STUDENTS = "students";
    private static final String TABLE_ATTENDANCE = "attendance";
    private static final String TABLE_ASSIGNMENTS = "assignments";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create all database tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_STUDENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "roll_number TEXT UNIQUE NOT NULL, " +
                "department TEXT, " +
                "semester INTEGER, " +
                "email TEXT UNIQUE NOT NULL, " +
                "gender TEXT, " +
                "phone TEXT, " +
                "photo_uri TEXT, " +
                "password_hash TEXT" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_ATTENDANCE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_email TEXT, " +
                "subject TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "status TEXT NOT NULL" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_ASSIGNMENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_email TEXT, " +
                "title TEXT NOT NULL, " +
                "subject TEXT, " +
                "due_date TEXT, " +
                "completed INTEGER DEFAULT 0" +
                ")");
    }

    // Check whether a column already exists
    private boolean columnExists(
            SQLiteDatabase db,
            String tableName,
            String columnName
    ) {
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "PRAGMA table_info(" + tableName + ")",
                    null
            );

            while (cursor.moveToNext()) {
                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                );

                if (columnName.equals(name)) {
                    return true;
                }
            }

            return false;

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void addColumnIfMissing(
            SQLiteDatabase db,
            String tableName,
            String columnName,
            String columnDefinition
    ) {
        if (!columnExists(db, tableName, columnName)) {
            db.execSQL(
                    "ALTER TABLE " + tableName
                            + " ADD COLUMN " + columnName
                            + " " + columnDefinition
            );
        }
    }

    // Upgrade existing databases without deleting saved records
    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        addColumnIfMissing(
                db, TABLE_STUDENTS, "email", "TEXT"
        );

        addColumnIfMissing(
                db, TABLE_STUDENTS, "gender", "TEXT"
        );

        addColumnIfMissing(
                db, TABLE_STUDENTS, "phone", "TEXT"
        );

        addColumnIfMissing(
                db, TABLE_STUDENTS, "photo_uri", "TEXT"
        );

        addColumnIfMissing(
                db, TABLE_STUDENTS, "password_hash", "TEXT"
        );

        // Added in database version 4
        addColumnIfMissing(
                db, TABLE_ATTENDANCE, "student_email", "TEXT"
        );

        addColumnIfMissing(
                db, TABLE_ASSIGNMENTS, "student_email", "TEXT"
        );
    }

    // -----------------------------------------
    // PASSWORD HASHING
    // -----------------------------------------

    private String createPasswordHash(String password) {

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        byte[] hash = derivePassword(password, salt);

        if (hash == null) {
            return null;
        }

        String encodedSalt = Base64.encodeToString(
                salt, Base64.NO_WRAP
        );

        String encodedHash = Base64.encodeToString(
                hash, Base64.NO_WRAP
        );

        return encodedSalt + ":" + encodedHash;
    }

    private byte[] derivePassword(String password, byte[] salt) {

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                120000,
                256
        );

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA1"
                    );

            return factory.generateSecret(spec).getEncoded();

        } catch (Exception e) {
            return null;

        } finally {
            spec.clearPassword();
        }
    }

    // -----------------------------------------
    // STUDENT REGISTRATION
    // -----------------------------------------

    public boolean insertStudent(
            String name,
            String rollNumber,
            String department,
            int semester,
            String email,
            String gender,
            String phone,
            String password
    ) {

        if (password == null || password.length() < 6) {
            return false;
        }

        String passwordHash = createPasswordHash(password);

        if (passwordHash == null) {
            return false;
        }

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("roll_number", rollNumber);
        values.put("department", department);
        values.put("semester", semester);
        values.put("email", email);
        values.put("gender", gender);
        values.put("phone", phone);
        values.put("photo_uri", "");
        values.put("password_hash", passwordHash);

        try {
            long result = getWritableDatabase().insert(
                    TABLE_STUDENTS, null, values
            );

            return result != -1;

        } catch (SQLiteConstraintException e) {
            return false;
        }
    }

    // -----------------------------------------
    // STUDENT LOGIN
    // -----------------------------------------

    public boolean verifyStudentPassword(
            String email,
            String password
    ) {

        if (email == null || password == null) {
            return false;
        }

        Cursor cursor = null;

        try {
            cursor = getReadableDatabase().query(
                    TABLE_STUDENTS,
                    new String[]{"password_hash"},
                    "email = ?",
                    new String[]{email},
                    null,
                    null,
                    null,
                    "1"
            );

            if (cursor == null || !cursor.moveToFirst()) {
                return false;
            }

            int index = cursor.getColumnIndex("password_hash");

            if (index < 0 || cursor.isNull(index)) {
                return false;
            }

            String storedValue = cursor.getString(index);

            if (storedValue == null) {
                return false;
            }

            String[] parts = storedValue.split(":", 2);

            if (parts.length != 2) {
                return false;
            }

            byte[] salt = Base64.decode(
                    parts[0], Base64.NO_WRAP
            );

            byte[] expectedHash = Base64.decode(
                    parts[1], Base64.NO_WRAP
            );

            byte[] actualHash = derivePassword(password, salt);

            return actualHash != null
                    && MessageDigest.isEqual(
                    expectedHash, actualHash
            );

        } catch (Exception e) {
            return false;

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // -----------------------------------------
    // STUDENT PROFILE
    // -----------------------------------------

    public Cursor getStudentProfile(String email) {

        return getReadableDatabase().query(
                TABLE_STUDENTS,
                null,
                "email = ?",
                new String[]{email},
                null,
                null,
                "id ASC",
                "1"
        );
    }

    public boolean updateStudentProfile(
            String currentEmail,
            String name,
            String rollNumber,
            String newEmail,
            String department,
            int semester,
            String gender,
            String phone,
            String photoUri
    ) {

        if (currentEmail == null
                || currentEmail.trim().isEmpty()) {
            return false;
        }

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("roll_number", rollNumber);
        values.put("email", newEmail);
        values.put("department", department);
        values.put("semester", semester);
        values.put("gender", gender);
        values.put("phone", phone);
        values.put("photo_uri", photoUri);

        try {
            int rows = getWritableDatabase().update(
                    TABLE_STUDENTS,
                    values,
                    "email = ?",
                    new String[]{currentEmail}
            );

            return rows > 0;

        } catch (SQLiteConstraintException e) {
            return false;
        }
    }

    public boolean updateStudentPhoto(
            String email,
            String photoUri
    ) {

        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("photo_uri", photoUri);

        int rows = getWritableDatabase().update(
                TABLE_STUDENTS,
                values,
                "email = ?",
                new String[]{email}
        );

        return rows > 0;
    }

    // -----------------------------------------
    // ATTENDANCE METHODS
    // -----------------------------------------

    // Original method retained for compatibility
    public long insertAttendance(
            String subject,
            String date,
            String status
    ) {
        return insertAttendance(null, subject, date, status);
    }

    // Save attendance for a particular student
    public long insertAttendance(
            String studentEmail,
            String subject,
            String date,
            String status
    ) {

        ContentValues values = new ContentValues();

        values.put("student_email", studentEmail);
        values.put("subject", subject);
        values.put("date", date);
        values.put("status", status);

        return getWritableDatabase().insert(
                TABLE_ATTENDANCE, null, values
        );
    }

    // Original method retained for compatibility
    public Cursor getAllAttendance() {

        return getReadableDatabase().query(
                TABLE_ATTENDANCE,
                null,
                null,
                null,
                null,
                null,
                "date DESC"
        );
    }

    // Get attendance for a particular student
    public Cursor getAllAttendance(String studentEmail) {

        if (studentEmail == null || studentEmail.trim().isEmpty()) {
            return getAllAttendance();
        }

        return getReadableDatabase().query(
                TABLE_ATTENDANCE,
                null,
                "student_email = ?",
                new String[]{studentEmail},
                null,
                null,
                "date DESC"
        );
    }

    // -----------------------------------------
    // ASSIGNMENT METHODS
    // -----------------------------------------

    // Original method retained for compatibility
    public long insertAssignment(
            String title,
            String subject,
            String dueDate
    ) {
        return insertAssignment(null, title, subject, dueDate);
    }

    // Save an assignment for a particular student
    public long insertAssignment(
            String studentEmail,
            String title,
            String subject,
            String dueDate
    ) {

        ContentValues values = new ContentValues();

        values.put("student_email", studentEmail);
        values.put("title", title);
        values.put("subject", subject);
        values.put("due_date", dueDate);
        values.put("completed", 0);

        return getWritableDatabase().insert(
                TABLE_ASSIGNMENTS, null, values
        );
    }

    // IMPORTANT: completed must be a boolean
    public int markAssignmentCompleted(
            long id,
            boolean completed
    ) {

        ContentValues values = new ContentValues();

        values.put("completed", completed ? 1 : 0);

        return getWritableDatabase().update(
                TABLE_ASSIGNMENTS,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Original method retained for compatibility
    public Cursor getAllAssignments() {

        return getReadableDatabase().query(
                TABLE_ASSIGNMENTS,
                null,
                null,
                null,
                null,
                null,
                "due_date ASC"
        );
    }

    // Get assignments for a particular student
    public Cursor getAllAssignments(String studentEmail) {

        if (studentEmail == null || studentEmail.trim().isEmpty()) {
            return getAllAssignments();
        }

        return getReadableDatabase().query(
                TABLE_ASSIGNMENTS,
                null,
                "student_email = ?",
                new String[]{studentEmail},
                null,
                null,
                "due_date ASC"
        );
    }
}
