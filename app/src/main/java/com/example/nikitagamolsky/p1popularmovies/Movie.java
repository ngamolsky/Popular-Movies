package com.example.nikitagamolsky.p1popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


// Custom Movie Class
public class Movie implements Parcelable {


    String image;
    String original_title;
    String synopsis;
    String user_rating;
    String release_date;
    String id;
    int numOfParameters;

    //Constructors

    public Movie() {
        super();
    }

    public Movie(String image, String original_title, String synopsis, String release_date, String user_rating, String id) {
        super();
        this.image = image;
        this.original_title = original_title;
        this.synopsis = synopsis;
        this.release_date = release_date;
        this.user_rating = user_rating;
        this.id = id;
        this.numOfParameters = 6;
    }


    //Making the Object Parceable

    private Movie(Parcel in) {
        image = in.readString();
        original_title = in.readString();
        synopsis = in.readString();
        user_rating = in.readString();
        release_date = in.readString();
        id = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(image);
        out.writeString(original_title);
        out.writeString(synopsis);
        out.writeString(release_date);
        out.writeString(user_rating);
        out.writeString(id);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


//Methods for converting movie objects to and from strings
    public String movieToJSON() {
        return new Gson().toJson(this);
    }

    public Movie jSONToMovie(String movieJSON) {
        Type MovieType = new TypeToken<Movie>() {
        }.getType();
        return new Gson().fromJson(movieJSON, MovieType);
    }


    //Prints Movie Title for reference
    @Override
    public String toString() {
        return this.original_title;
    }


//Favorites methods for adding, removing and quetying favorites
    public boolean isInFavorites(Context context) {
        boolean inFavorites = false;
        Cursor cursor = context.getContentResolver().query(FavoritesContract.FavoritesEntry.CONTENT_URI, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                int movieIDColumn = cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID);
                if (this.id.equals(cursor.getString(movieIDColumn))) {
                    inFavorites = true;
                }
            }

        } finally {
            cursor.close();
        }
        return inFavorites;
    }

    public void addMovieToFavorites(Context context) {
            if (!isInFavorites(context)) {
                ContentValues currentMovieValues = new ContentValues();
                currentMovieValues.clear();
                currentMovieValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID, this.id);
                currentMovieValues.put(FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH, this.image);
                currentMovieValues.put(FavoritesContract.FavoritesEntry.COLUMN_TITLE, this.original_title);
                currentMovieValues.put(FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE, this.release_date);
                currentMovieValues.put(FavoritesContract.FavoritesEntry.COLUMN_USER_RATING, this.user_rating);
                currentMovieValues.put(FavoritesContract.FavoritesEntry.COLUMN_SYNOPSIS, this.synopsis);
                context.getContentResolver().insert(FavoritesContract.FavoritesEntry.CONTENT_URI, currentMovieValues);
            }
    }


    public void removeMovieFromFavorites(Context context) {
            if (isInFavorites(context)) {
                context.getContentResolver().delete(FavoritesContract.FavoritesEntry.CONTENT_URI, FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + "=" + this.id, null);
            }
    }

}




