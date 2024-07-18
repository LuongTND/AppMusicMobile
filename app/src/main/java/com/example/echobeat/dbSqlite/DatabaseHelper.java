package com.example.echobeat.dbSqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MusicApp";
    private static final int DATABASE_VERSION = 3;

    // Table names
    public static final String TB_USER = "USER";
    public static final String TB_ARTIST = "ARTIST";
    public static final String TB_ALBUM = "ALBUM";
    public static final String TB_SONG = "SONG";
    public static final String TB_CATEGORY = "CATEGORY";
    public static final String TB_PLAYLISTDETAIL = "PLAYLISTDETAIL";
    public static final String TB_PLAYLIST = "PLAYLIST";
    public static final String TB_HISTORY = "HISTORY";
    public static final String TB_ROLE = "ROLE";

    public static final String TB_LIST_PLAYLIST = "LISTPLAYLIST";

    // Common column names
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_USER_ID = "USER_ID";
    public static final String COLUMN_SONG_ID = "SONG_ID";

    // Columns in USER table
    public static final String COLUMN_USERNAME = "USERNAME";
    public static final String COLUMN_EMAIL = "EMAIL";

    public static final String COLUMN_PROFILE_PICTURE = "PROFILE_PICTURE";
    public static final String COLUMN_ROLE_ID = "ROLE_ID";
    public static final String COLUMN_GOOGLE_ID = "GOOGLE_ID";

    // Columns in ARTIST table
    public static final String COLUMN_ARTIST_NAME = "ARTIST_NAME";
    public static final String COLUMN_BIOGRAPHY = "BIOGRAPHY";

    // Columns in ALBUM table
    public static final String COLUMN_ALBUM_ID = "ALBUM_ID";
    public static final String COLUMN_RELEASE_YEAR = "RELEASE_YEAR";

    // Columns in SONG table
    public static final String COLUMN_SONG_URL = "SONG_URL";
    public static final String COLUMN_TITLE = "TITLE";
    public static final String COLUMN_DURATION = "DURATION";
    public static final String COLUMN_PICTURE_SONG = "PICTURE_SONG";
    public static final String COLUMN_CATEGORY_ID = "CATEGORY_ID";


    //Colums in List playlist
    public static final String COLUMN_PL_SONG_ID = "PL_SONG_ID";
    public static final String COLUMN_PL_SONG_URL = "PL_SONG_URL";
    public static final String COLUMN_PL_TITLE = "PL_TITLE";
    public static final String COLUMN_PL_PICTURE_SONG = "PL_PICTURE_SONG";


    // Columns in CATEGORY table
    public static final String COLUMN_CATEGORY_NAME = "CATEGORY_NAME";

    // Columns in PLAYLIST table
    public static final String COLUMN_PLAYLIST_ID = "PLAYLIST_ID";
    public static final String COLUMN_PICTURE_PLAYLIST = "PICTURE_PLAYLIST";
    public static final String COLUMN_PLAYLIST_NAME = "PLAYLIST_NAME";

    // Columns in PLAYLISTDETAIL table
    public static final String COLUMN_PLAYLIST_DETAIL_TITLE = "DETAIL_TITLE";

    // Columns in HISTORY table
    public static final String COLUMN_HISTORY_ID = "HISTORY_ID";

    // Columns in ROLE table
    public static final String COLUMN_ROLE_NAME = "ROLE_NAME";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Role table
        String createRoleTable = "CREATE TABLE " + TB_ROLE + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ROLE_NAME + " TEXT)";

        // Create User table with foreign key to ROLE table
        String createUserTable = "CREATE TABLE " + TB_USER + " ("
//                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID + " TEXT , "
                + COLUMN_USERNAME + " TEXT, "
                + COLUMN_EMAIL + " TEXT, "

                + COLUMN_PROFILE_PICTURE + " TEXT, "
                + COLUMN_ROLE_ID + " INTEGER, "
                + COLUMN_GOOGLE_ID + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_ROLE_ID + ") REFERENCES " + TB_ROLE + "(" + COLUMN_ID + "))";

        // Create Artist table with foreign key to USER table
        String createArtistTable = "CREATE TABLE " + TB_ARTIST + " ("
                + COLUMN_USER_ID + " TEXT PRIMARY KEY, "
                + COLUMN_ARTIST_NAME + " TEXT, "
                + COLUMN_BIOGRAPHY + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TB_USER + "(" + COLUMN_USER_ID + "))";

        // Create Album table with foreign keys to ARTIST and SONG tables
        String createAlbumTable = "CREATE TABLE " + TB_ALBUM + " ("
                + COLUMN_ALBUM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID + " TEXT, "
                + COLUMN_SONG_ID + " INTEGER, "
                + COLUMN_RELEASE_YEAR + " INTEGER, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TB_ARTIST + "(" + COLUMN_USER_ID + "), "
                + "FOREIGN KEY(" + COLUMN_SONG_ID + ") REFERENCES " + TB_SONG + "(" + COLUMN_SONG_ID + "))";

        // Create Song table with foreign keys to USER and CATEGORY tables
        String createSongTable = "CREATE TABLE " + TB_SONG + " ("
                + COLUMN_SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID + " TEXT, "
                + COLUMN_SONG_URL + " TEXT, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_DURATION + " INTEGER, "
                + COLUMN_RELEASE_YEAR + " INTEGER, "
                + COLUMN_PICTURE_SONG + " TEXT, "
                + COLUMN_CATEGORY_ID + " INTEGER, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TB_USER + "(" + COLUMN_USER_ID + "), "
                + "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " + TB_CATEGORY + "(" + COLUMN_ID + "))";

        String CreateListPlaylistTable = "CREATE TABLE " + TB_LIST_PLAYLIST + " ("
                + COLUMN_PL_SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PL_SONG_URL + " TEXT, "
                + COLUMN_PL_TITLE + " TEXT, "
                + COLUMN_PL_PICTURE_SONG + " TEXT )";

        // Create Category table
        String createCategoryTable = "CREATE TABLE " + TB_CATEGORY + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CATEGORY_NAME + " TEXT)";

        // Create Playlist table with foreign key to USER table
        String createPlaylistTable = "CREATE TABLE " + TB_PLAYLIST + " ("
                + COLUMN_PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID + " TEXT, "
                + COLUMN_PICTURE_PLAYLIST + " TEXT, "
                + COLUMN_PLAYLIST_NAME + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TB_USER + "(" + COLUMN_USER_ID + "))";

        // Create PlaylistDetail table with foreign keys to PLAYLIST and SONG tables
        String createPlaylistDetailTable = "CREATE TABLE " + TB_PLAYLISTDETAIL + " ("
                + COLUMN_PLAYLIST_ID + " INTEGER, "
                + COLUMN_SONG_ID + " INTEGER, "
                + COLUMN_PLAYLIST_DETAIL_TITLE + " TEXT, "
                + "PRIMARY KEY (" + COLUMN_PLAYLIST_ID + ", " + COLUMN_SONG_ID + "), "
                + "FOREIGN KEY(" + COLUMN_PLAYLIST_ID + ") REFERENCES " + TB_PLAYLIST + "(" + COLUMN_PLAYLIST_ID + "), "
                + "FOREIGN KEY(" + COLUMN_SONG_ID + ") REFERENCES " + TB_SONG + "(" + COLUMN_SONG_ID + "))";

        // Create History table with foreign keys to SONG and USER tables
        String createHistoryTable = "CREATE TABLE " + TB_HISTORY + " ("
                + COLUMN_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SONG_ID + " INTEGER, "
                + COLUMN_USER_ID + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_SONG_ID + ") REFERENCES " + TB_SONG + "(" + COLUMN_SONG_ID + "), "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TB_USER + "(" + COLUMN_USER_ID + "))";

        // Execute the SQL statements to create the tables
        db.execSQL(createRoleTable);
        db.execSQL(createUserTable);
        db.execSQL(createArtistTable);
        db.execSQL(createCategoryTable);
        db.execSQL(createHistoryTable);
        db.execSQL(createSongTable);
        db.execSQL(createAlbumTable);
        db.execSQL(createPlaylistTable);
        db.execSQL(createPlaylistDetailTable);
        db.execSQL(CreateListPlaylistTable);

        // Insert data into Role table
        String insertRole1 = "INSERT INTO " + TB_ROLE + " (" + COLUMN_ID + ", " + COLUMN_ROLE_NAME + ") VALUES (1, 'listener')";
        String insertRole2 = "INSERT INTO " + TB_ROLE + " (" + COLUMN_ID + ", " + COLUMN_ROLE_NAME + ") VALUES (2, 'artist')";

        db.execSQL(insertRole1);
        db.execSQL(insertRole2);

        Log.d("DatabaseHelper", "Tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables
        db.execSQL("DROP TABLE IF EXISTS " + TB_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TB_PLAYLISTDETAIL);
        db.execSQL("DROP TABLE IF EXISTS " + TB_PLAYLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TB_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TB_SONG);
        db.execSQL("DROP TABLE IF EXISTS " + TB_LIST_PLAYLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TB_ALBUM);
        db.execSQL("DROP TABLE IF EXISTS " + TB_ARTIST);
        db.execSQL("DROP TABLE IF EXISTS " + TB_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TB_ROLE);

        // Create new tables
        onCreate(db);
    }
}
