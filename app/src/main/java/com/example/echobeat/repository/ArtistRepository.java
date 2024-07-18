package com.example.echobeat.repository;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.echobeat.dbSqlite.DatabaseHelper;
import com.example.echobeat.modelSqlite.Artist;


public class ArtistRepository {

    private Context context;
    private DatabaseHelper dbHelper;

    public static final String TABLE_NAME = DatabaseHelper.TB_ARTIST;
    public static final String COLUMN_ARTIST_NAME = DatabaseHelper.COLUMN_ARTIST_NAME;
    public static final String COLUMN_USER_ID = DatabaseHelper.COLUMN_USER_ID;
    public static final String COLUMN_BIOGRAPHY = DatabaseHelper.COLUMN_BIOGRAPHY;


    public ArtistRepository(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }
    public void AddArtist(Artist artistInfo){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ARTIST_NAME, artistInfo.getArtistName());
            values.put(COLUMN_BIOGRAPHY, artistInfo.getBiography());


            long result = db.insert(TABLE_NAME, null, values);
            db.setTransactionSuccessful();
            if (result == -1) {
                Toast.makeText(context, "Failed to add information!!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Welcome Artist", Toast.LENGTH_SHORT).show();
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
    public boolean saveArtist(Artist artist) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, artist.getUserId());
        values.put(COLUMN_ARTIST_NAME, artist.getArtistName());
        values.put(COLUMN_BIOGRAPHY, artist.getBiography());

        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1;
    }
}
