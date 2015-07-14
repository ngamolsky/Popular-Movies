package com.example.nikitagamolsky.p1popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nikitagamolsky on 7/8/15.
 */
public class Movie implements Parcelable {
    String image;
    String original_title;
    String synopsis;
    String user_rating;
    String release_date;
    int numOfParameters;

    public Movie() {
        super();

    }

    public Movie(String image, String original_title, String synopsis, String release_date, String user_rating) {
        super();
        this.image = image;
        this.original_title = original_title;
        this.synopsis = synopsis;
        this.release_date = release_date;
        this.user_rating = user_rating;
        this.numOfParameters = 5;
    }

    private Movie(Parcel in) {
        image = in.readString();
        original_title = in.readString();
        synopsis = in.readString();
        user_rating = in.readString();
        release_date = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(image);
        out.writeString(original_title);
        out.writeString(synopsis);
        out.writeString(release_date);
        out.writeString(user_rating );

    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String[] movieToArray(){
        String[] currentMovieArray = new String[this.numOfParameters];
        currentMovieArray[0] = this.image;
        currentMovieArray[1] = this.original_title;
        currentMovieArray[2] = this.synopsis;
        currentMovieArray[3] = this.release_date;
        currentMovieArray[4] = this.user_rating;


        return currentMovieArray;
    }


}
