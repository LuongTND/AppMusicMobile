package com.example.echobeat.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.echobeat.dbSqlite.DatabaseHelper;
import com.example.echobeat.modelFirebase.Song;

import java.util.ArrayList;
import java.util.List;

public class SongRepository {
    private final DatabaseHelper dbHelper;

    public SongRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void addSongToPlaylist(Song song) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("COLUMN_PL_SONG_URL", song.getSongUrl());
        values.put("COLUMN_PL_TITLE", song.getTitle());
        values.put("COLUMN_PL_PICTURE_SONG", song.getPictureSong());

        db.insert("TB_LIST_PLAYLIST", null, values);
        db.close();
    }
}


//}public List<Song> getAllSongsFromPlaylist() {
//    List<Song> songs = new ArrayList<>();
//    SQLiteDatabase db = dbHelper.getReadableDatabase();
//    Cursor cursor = db.query("TB_LIST_PLAYLIST", null, null, null, null, null, null);
//
//    if (cursor != null) {
//        while (cursor.moveToNext()) {
//            String songUrl = cursor.getString(cursor.getColumnIndex("COLUMN_PL_SONG_URL"));
//            String title = cursor.getString(cursor.getColumnIndex("COLUMN_PL_TITLE"));
//            String pictureSong = cursor.getString(cursor.getColumnIndex("COLUMN_PL_PICTURE_SONG"));
//            Song song = new Song(songUrl, title, pictureSong);
//            songs.add(song);
//        }
//        cursor.close();
//    }
//    db.close();
//    return songs;
//}
