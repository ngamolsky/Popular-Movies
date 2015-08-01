package com.example.nikitagamolsky.p1popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.nikitagamolsky.p1popularmovies.FavoritesContract.FavoritesEntry;

//Database Helper for the Favorites
public class FavoritesDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;

    static final String DATABASE_NAME = "favorites.db";

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +
                FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoritesEntry.COLUMN_MOVIE_ID + " TEXT UNIQUE NOT NULL," +
                FavoritesEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                FavoritesEntry.COLUMN_POSTER_PATH + " TEXT," +
                FavoritesEntry.COLUMN_RELEASE_DATE + " TEXT," +
                FavoritesEntry.COLUMN_USER_RATING + " TEXT," +
                FavoritesEntry.COLUMN_SYNOPSIS + " TEXT" + " )";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
