package com.example.nikitagamolsky.p1popularmovies;

/**
 * Created by nikitagamolsky on 7/8/15.
 */
public class Movie {
    String image;
    String original_title;
    String synopsis;
    String user_rating;
    String release_date;
    int numOfParameters;


    public Movie(){
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
