
package com.example.campusmate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

        ContentValues values = new ContentValues();

        values.put("name", name);
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

        ContentValues values = new ContentValues();
        values.put("completed", completed ? 1 : 0);

        int result = db.update(
                "Assignments",
                values,
                "id=?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }
}

