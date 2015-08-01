package com.example.nikitagamolsky.p1popularmovies;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


//Contract for the Content Provider
public class FavoritesContract {
    public static final String CONTENT_AUTHORITY = "com.example.nikitagamolsky.p1popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITES = "favorites";

    public static final class FavoritesEntry implements BaseColumns {

        public static final String TABLE_NAME = "favorites";
        public static final  String COLUMN_TITLE = "original_title";
        public static final  String COLUMN_POSTER_PATH = "poster_path";
        public static final  String COLUMN_RELEASE_DATE = "release_date";
        public static final  String COLUMN_USER_RATING = "vote_average";
        public static final  String COLUMN_SYNOPSIS = "overview";
        public static final  String COLUMN_MOVIE_ID = "movie_id";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();


        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
    }
}
