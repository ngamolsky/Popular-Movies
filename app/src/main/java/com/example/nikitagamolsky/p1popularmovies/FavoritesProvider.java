package com.example.nikitagamolsky.p1popularmovies;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

//This is the Content Provider for the Favorites
public class FavoritesProvider extends ContentProvider {
    static final int FAVORITES = 100;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavoritesDbHelper mOpenHelper;

    static ArrayList<Movie> getFavoritesFromCursor(Cursor cursor){
        ArrayList<Movie> favoritesArray= new ArrayList<>();
        while(cursor.moveToNext()){
            Movie favoriteMovie = new Movie(cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH)),cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_TITLE)),cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_SYNOPSIS)),cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE)),cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_USER_RATING)),cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID)));
            favoritesArray.add(favoriteMovie);
        }
        return favoritesArray;
    }


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoritesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority,FavoritesContract.PATH_FAVORITES,FAVORITES);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new FavoritesDbHelper(getContext());
        return true;
    }



    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVORITES:
                return FavoritesContract.FavoritesEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case FAVORITES:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(FavoritesContract.FavoritesEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case FAVORITES: {
                long _id = db.insert(FavoritesContract.FavoritesEntry.TABLE_NAME, null, values);
                Log.d("_id", String.valueOf(_id));
                if ( _id > 0 )
                    returnUri = FavoritesContract.FavoritesEntry.CONTENT_URI;
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case FAVORITES: {
                rowsDeleted = db.delete(FavoritesContract.FavoritesEntry.TABLE_NAME,selection,selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case FAVORITES: {
                rowsUpdated = db.update(FavoritesContract.FavoritesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }
}