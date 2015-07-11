package com.example.nikitagamolsky.p1popularmovies;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Intent intent = getActivity().getIntent();
        int orientation = getResources().getConfiguration().orientation;
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String[] currentMovieArray = intent.getStringArrayExtra(Intent.EXTRA_TEXT);
            LinearLayout movieInfoBox = (LinearLayout) rootView.findViewById(R.id.movieInfoBox);
            TextView movieTitle = (TextView) rootView.findViewById(R.id.movieTitle);
            ImageView moviePoster = (ImageView) rootView.findViewById(R.id.moviePoster);
            TextView releaseDate = (TextView) rootView.findViewById(R.id.release_date);
            TextView userRating = (TextView) rootView.findViewById(R.id.user_rating);
            TextView synopsis = (TextView) rootView.findViewById(R.id.synopsis);
            movieTitle.setText(currentMovieArray[1]);
            Picasso.with(getActivity()).load(currentMovieArray[0]).into(moviePoster);
            moviePoster.getLayoutParams().width = width/3;
            releaseDate.setText("Release Date:" +System.getProperty ("line.separator")+ currentMovieArray[3]);
            userRating.setText("User Rating: " + currentMovieArray[4]);
            synopsis.setText(currentMovieArray[2]);
            movieInfoBox.getLayoutParams().height = moviePoster.getLayoutParams().height;

        }
        return rootView;

    }
}
