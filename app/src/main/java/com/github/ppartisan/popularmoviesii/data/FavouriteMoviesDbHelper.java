package com.github.ppartisan.popularmoviesii.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.ppartisan.popularmoviesii.data.FavouriteMoviesContract.MoviesEntry;
import com.github.ppartisan.popularmoviesii.data.FavouriteMoviesContract.ExtrasEntry;


public class FavouriteMoviesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";
    public static final int SCHEMA = 1;

    private static FavouriteMoviesDbHelper sInstance = null;

    public static synchronized FavouriteMoviesDbHelper get(Context context) {
        if (sInstance == null) {
            sInstance = new FavouriteMoviesDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private FavouriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesEntry.COLUMN_TITLE + " TEXT, " +
                MoviesEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MoviesEntry.COLUMN_IMAGE_URL + " TEXT, " +
                MoviesEntry.COLUMN_SYNOPSIS + " TEXT, " +
                MoviesEntry.COLUMN_AVERAGE_VOTE + " REAL, " +
                "UNIQUE (" + MoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);

        final String SQL_CREATE_EXTRAS_TABLE = "CREATE TABLE " + ExtrasEntry.TABLE_NAME + " (" +
                ExtrasEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ExtrasEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                ExtrasEntry.COLUMN_REVIEW_ID + " INTEGER, " +
                ExtrasEntry.COLUMN_REVIEW_AUTHOR + " TEXT, " +
                ExtrasEntry.COLUMN_REVIEW_CONTENT + " TEXT, " +
                ExtrasEntry.COLUMN_REVIEW_URL + " TEXT, " +
                ExtrasEntry.COLUMN_TRAILER_TITLE + " TEXT, " +
                ExtrasEntry.COLUMN_TRAILER_SOURCE + " TEXT, " +
                "UNIQUE ( " +
                ExtrasEntry.COLUMN_REVIEW_ID + ", " + ExtrasEntry.COLUMN_TRAILER_SOURCE +
                ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_EXTRAS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ExtrasEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
