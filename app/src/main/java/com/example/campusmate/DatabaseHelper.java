<<<<<<< HEAD

=======
>>>>>>> 879e7b8943b2123630cb35e18312e38436ab67d9
package com.example.campusmate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
<<<<<<< HEAD
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "CampusMate.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE Attendance (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "subject TEXT, " +
                "date TEXT, " +
                "status TEXT)");

        db.execSQL("CREATE TABLE Assignments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "subject TEXT, " +
                "due_date TEXT, " +
                "completed INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS Attendance");
        db.execSQL("DROP TABLE IF EXISTS Assignments");
        onCreate(db);
    }

    public boolean insertAttendance(String subject,
                                    String date,
                                    String status) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("subject", subject);
        values.put("date", date);
        values.put("status", status);

        long result = db.insert("Attendance", null, values);

        return result != -1;
    }

    public Cursor getAttendance() {
        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM Attendance ORDER BY id DESC",
                null);
    }

    public boolean insertAssignment(String name,
                                    String subject,
                                    String date,
                                    boolean completed) {

        SQLiteDatabase db = getWritableDatabase();
=======
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
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_STUDENTS = "students";
    private static final String TABLE_ATTENDANCE = "attendance";
    private static final String TABLE_ASSIGNMENTS = "assignments";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create database tables
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
                "subject TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "status TEXT NOT NULL" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_ASSIGNMENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "subject TEXT, " +
                "due_date TEXT, " +
                "completed INTEGER DEFAULT 0" +
                ")");
    }

    // Upgrade database without deleting existing data
    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        if (oldVersion < 2) {
            db.execSQL(
                    "ALTER TABLE students ADD COLUMN email TEXT"
            );
            db.execSQL(
                    "ALTER TABLE students ADD COLUMN gender TEXT"
            );
            db.execSQL(
                    "ALTER TABLE students ADD COLUMN phone TEXT"
            );
            db.execSQL(
                    "ALTER TABLE students ADD COLUMN photo_uri TEXT"
            );
        }

        if (oldVersion < 3) {
            db.execSQL(
                    "ALTER TABLE students ADD COLUMN password_hash TEXT"
            );
        }
    }

    // Generate salted password hash
    private String createPasswordHash(String password) {

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        byte[] hash = derivePassword(password, salt);

        if (hash == null) {
            return null;
        }

        String encodedSalt =
                Base64.encodeToString(salt, Base64.NO_WRAP);

        String encodedHash =
                Base64.encodeToString(hash, Base64.NO_WRAP);

        return encodedSalt + ":" + encodedHash;
    }

    // Derive hash from password and salt
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

    // Register student with password
    public boolean insertStudent(
            String name,
            String rollNumber,
            String department,
            int semester,
            String email,
            String gender,
            String phone,
            String password) {

        if (password == null || password.length() < 6) {
            return false;
        }

        String passwordHash = createPasswordHash(password);

        if (passwordHash == null) {
            return false;
        }
>>>>>>> 879e7b8943b2123630cb35e18312e38436ab67d9

        ContentValues values = new ContentValues();

        values.put("name", name);
<<<<<<< HEAD
        values.put("subject", subject);
        values.put("due_date", date);
        values.put("completed", completed ? 1 : 0);

        long result = db.insert("Assignments", null, values);

        return result != -1;
    }

    public Cursor getAssignments() {
        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM Assignments ORDER BY id DESC",
                null);
    }

    public boolean updateAssignmentStatus(int id,
                                          boolean completed) {

        SQLiteDatabase db = getWritableDatabase();
=======
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
                    TABLE_STUDENTS,
                    null,
                    values
            );

            return result != -1;

        } catch (SQLiteConstraintException e) {
            return false;
        }
    }

    // Verify email and password during login
    public boolean verifyStudentPassword(
            String email,
            String password) {

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

            byte[] salt =
                    Base64.decode(parts[0], Base64.NO_WRAP);

            byte[] expectedHash =
                    Base64.decode(parts[1], Base64.NO_WRAP);

            byte[] actualHash = derivePassword(password, salt);

            return actualHash != null
                    && MessageDigest.isEqual(
                    expectedHash,
                    actualHash
            );

        } catch (Exception e) {
            return false;

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Get student profile by email
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

    // Update student profile
    public boolean updateStudentProfile(
            String currentEmail,
            String name,
            String rollNumber,
            String newEmail,
            String department,
            int semester,
            String gender,
            String phone,
            String photoUri) {

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

    // Update profile photo
    public boolean updateStudentPhoto(
            String email,
            String photoUri) {

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

    // Insert attendance record
    public long insertAttendance(
            String subject,
            String date,
            String status) {

        ContentValues values = new ContentValues();

        values.put("subject", subject);
        values.put("date", date);
        values.put("status", status);

        return getWritableDatabase().insert(
                TABLE_ATTENDANCE,
                null,
                values
        );
    }

    // Get all attendance records
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

    // Insert assignment
    public long insertAssignment(
            String title,
            String subject,
            String dueDate) {

        ContentValues values = new ContentValues();

        values.put("title", title);
        values.put("subject", subject);
        values.put("due_date", dueDate);
        values.put("completed", 0);

        return getWritableDatabase().insert(
                TABLE_ASSIGNMENTS,
                null,
                values
        );
    }

    // Mark assignment complete or incomplete
    public int markAssignmentCompleted(
            long id,
            boolean completed) {
>>>>>>> 879e7b8943b2123630cb35e18312e38436ab67d9

        ContentValues values = new ContentValues();
        values.put("completed", completed ? 1 : 0);

<<<<<<< HEAD
        int result = db.update(
                "Assignments",
                values,
                "id=?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }
}

=======
        return getWritableDatabase().update(
                TABLE_ASSIGNMENTS,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Get all assignments
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
}
>>>>>>> 879e7b8943b2123630cb35e18312e38436ab67d9
