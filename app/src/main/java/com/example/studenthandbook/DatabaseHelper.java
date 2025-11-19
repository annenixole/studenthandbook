package com.example.studenthandbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ClassScheduler.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CLASSES = "classes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DAY = "day";
    private static final String COLUMN_START_TIME = "start_time";
    private static final String COLUMN_END_TIME = "end_time";
    private static final String COLUMN_SUBJECT = "subject";
    private static final String COLUMN_LOCATION = "location";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_CLASSES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DAY + " TEXT NOT NULL, " +
                COLUMN_START_TIME + " TEXT NOT NULL, " +
                COLUMN_END_TIME + " TEXT NOT NULL, " +
                COLUMN_SUBJECT + " TEXT NOT NULL, " +
                COLUMN_LOCATION + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        onCreate(db);
    }

    public long addClass(ClassItem classItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY, classItem.getDay());
        values.put(COLUMN_START_TIME, classItem.getStartTime());
        values.put(COLUMN_END_TIME, classItem.getEndTime());
        values.put(COLUMN_SUBJECT, classItem.getSubject());
        values.put(COLUMN_LOCATION, classItem.getLocation());
        return db.insert(TABLE_CLASSES, null, values);
    }

    public List<ClassItem> getAllClasses() {
        List<ClassItem> classes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLASSES, null, null, null, null, null, COLUMN_DAY + ", " + COLUMN_START_TIME);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ClassItem classItem = new ClassItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION))
                );
                classes.add(classItem);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return classes;
    }

    public List<ClassItem> getClassesByDay(String day) {
        List<ClassItem> classes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_DAY + " = ?";
        String[] selectionArgs = {day};
        Cursor cursor = db.query(TABLE_CLASSES, null, selection, selectionArgs, null, null, COLUMN_START_TIME);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ClassItem classItem = new ClassItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION))
                );
                classes.add(classItem);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return classes;
    }

    public int updateClass(ClassItem classItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY, classItem.getDay());
        values.put(COLUMN_START_TIME, classItem.getStartTime());
        values.put(COLUMN_END_TIME, classItem.getEndTime());
        values.put(COLUMN_SUBJECT, classItem.getSubject());
        values.put(COLUMN_LOCATION, classItem.getLocation());

        String whereClause = COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(classItem.getId())};
        return db.update(TABLE_CLASSES, values, whereClause, whereArgs);
    }

    public int deleteClass(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_CLASSES, whereClause, whereArgs);
    }
}