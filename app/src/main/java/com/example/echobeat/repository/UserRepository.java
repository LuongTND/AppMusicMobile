package com.example.echobeat.repository;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.echobeat.dbSqlite.DatabaseHelper;
import com.example.echobeat.modelSqlite.User;

public class UserRepository {
    private Context context;
    private DatabaseHelper dbHelper;

    public static final String TABLE_NAME = DatabaseHelper.TB_USER;
    public static final String COLUMN_ID = DatabaseHelper.COLUMN_USER_ID;
    public static final String COLUMN_NAME = DatabaseHelper.COLUMN_USERNAME;
    public static final String COLUMN_EMAIL = DatabaseHelper.COLUMN_EMAIL;
    public static final String COLUMN_AVATAR = DatabaseHelper.COLUMN_PROFILE_PICTURE;
    public static final String COLUMN_ROLE = DatabaseHelper.COLUMN_ROLE_ID;
    public static final String COLUMN_GOOGLE_ID = DatabaseHelper.COLUMN_GOOGLE_ID; // Added column for Google ID

    public UserRepository(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void register(User userInfo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, userInfo.getUsername());
            values.put(COLUMN_EMAIL, userInfo.getEmail());
            values.put(COLUMN_AVATAR, userInfo.getProfilePicture());
            values.put(COLUMN_ROLE, userInfo.getRoleId());
            values.put(COLUMN_GOOGLE_ID, userInfo.getGoogleId());

            long result = db.insert(TABLE_NAME, null, values);
            db.setTransactionSuccessful();
            if (result == -1) {
                Toast.makeText(context, "Failed to register!!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Welcome new member", Toast.LENGTH_SHORT).show();
            }
        } finally {
            db.endTransaction();
            db.close();
            checkpointDatabase();
        }
    }
    public void checkpointDatabase() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.rawQuery("PRAGMA wal_checkpoint(FULL);", null).close();
        db.close();
    }

    public boolean checkExistIdGoogle(String googleId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean checkExist = false;

        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COLUMN_GOOGLE_ID},
                COLUMN_GOOGLE_ID + "=?",
                new String[]{googleId},
                null,
                null,
                null,
                null);

        if (cursor.moveToNext()) {
            checkExist = true;
        }
        cursor.close();
        return checkExist;
    }

    public User getUserByIdGoogle(String googleId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_AVATAR, COLUMN_ROLE, COLUMN_GOOGLE_ID},
                COLUMN_GOOGLE_ID + "=?",
                new String[]{googleId},
                null,
                null,
                null,
                null);

        if (cursor.moveToNext()) {
            @SuppressLint("Range") String userId = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            @SuppressLint("Range") String fullname = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));
            @SuppressLint("Range") String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_AVATAR));
            @SuppressLint("Range") int role = cursor.getInt(cursor.getColumnIndex(COLUMN_ROLE));

            User user = new User(userId, fullname, email,avatar,role, googleId);
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }

    public boolean updateProfile(String googleId, String fullname) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, fullname);
        db.update(TABLE_NAME, cv, COLUMN_GOOGLE_ID + " = ?", new String[]{googleId});

        return true;
    }

    public boolean updateRole(String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ROLE, 2);
        int rowsAffected = db.update(TABLE_NAME, cv, COLUMN_ID + " = ? AND " + COLUMN_ROLE + " = ?", new String[]{userId, "1"});

        return rowsAffected > 0;
    }
    public String getUserIdByGoogleId(String googleId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String userId = null;

        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COLUMN_ID},
                COLUMN_GOOGLE_ID + "=?",
                new String[]{googleId},
                null,
                null,
                null,
                null);

        if (cursor.moveToNext()) {
            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            userId = id;
        }
        cursor.close();
        db.close();
        return userId;
    }


}
