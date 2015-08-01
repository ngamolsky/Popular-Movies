package com.example.nikitagamolsky.p1popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MovieDetailActivityFragment extends Fragment {
    private ArrayAdapter<String> trailerAdapter; //Basic Adapter for Trailer View
    private String currentMovieJSON; //Contains JSON serial for Current Movie JSON
    private Movie currentMovie; // Current Movie Object
    private ShareActionProvider mShareActionProvider; //Provider for Share button

    public MovieDetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Gets Current Movie Data from Main Activity Intent, Saved Instance State or Fragment Manager
        Intent intent = getActivity().getIntent();
        View rootView;
        Bundle arguments = getArguments();

        if (intent.getStringExtra(Intent.EXTRA_TEXT) == null && arguments == null){
            rootView = inflater.inflate(R.layout.no_movie_detail, container, false);
        } else {
            if (arguments != null) {
                currentMovieJSON = arguments.getString("currentMovieJSON");
            }
             else if (!intent.getStringExtra(Intent.EXTRA_TEXT).isEmpty()) {
                currentMovieJSON = intent.getStringExtra(Intent.EXTRA_TEXT);
            } else if (savedInstanceState != null) {
                currentMovieJSON = savedInstanceState.getString("Movie Object", intent.getStringExtra(Intent.EXTRA_TEXT));
            }


            //Gets id's main Views
            rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
            ListView trailerListView = (ListView) rootView.findViewById(R.id.trailerListView);


            //Gets id's of Movie Data Views
            TextView movieTitle = (TextView) rootView.findViewById(R.id.movieTitle);
            ImageView moviePoster = (ImageView) rootView.findViewById(R.id.moviePoster);
            TextView releaseDate = (TextView) rootView.findViewById(R.id.release_date);
            TextView userRating = (TextView) rootView.findViewById(R.id.user_rating);
            TextView synopsis = (TextView) rootView.findViewById(R.id.synopsis);
            Button reviewButton = (Button) rootView.findViewById(R.id.read_reviews);
            CheckBox favoritesCheckbox = (CheckBox) rootView.findViewById(R.id.favorites);

            //Creates ArrayList to contain Trailer Links Data
            ArrayList<String> trailerLinks = new ArrayList<>(0);

            //Initializes Trailer Adapter with trailerLinks data
            trailerAdapter = new TrailerAdapter(getActivity(), trailerLinks);

            //Sets adapter to the Listview of Trailers
            trailerListView.setAdapter(trailerAdapter);


            //Deserializes current Movie Data back into a Movie Object
            currentMovie = new Movie().jSONToMovie(currentMovieJSON);


            //Initializes context
            final Context context = getActivity();



            //Populates Movie Data Layout with Text and Images
            if (currentMovie.image.isEmpty()) {
                moviePoster.setImageResource(R.drawable.noposter);
            } else {
                Picasso.with(getActivity()).load(currentMovie.image).into(moviePoster);
            }


            if (currentMovie.original_title.isEmpty()) {
                movieTitle.setText("No Title Available");
            } else {
                movieTitle.setText(currentMovie.original_title);
            }

            if (currentMovie.release_date.isEmpty()) {
                releaseDate.setText("No Release Date Available");
            } else {
                releaseDate.setText("Release Date:" + System.getProperty("line.separator") + currentMovie.release_date);
            }

            if (currentMovie.user_rating.isEmpty()) {
                userRating.setText("No User Rating Available");
            } else {
                userRating.setText("User Rating: " + currentMovie.user_rating);
            }

            if (currentMovie.synopsis == null) {
                currentMovie.synopsis = "No Synopsis Available";

            } else {
                synopsis.setText(currentMovie.synopsis);
            }



            //Start Review Activity on Click of the Get Reviews Button, add currentMovie id to Fetch the correct Reviews
            reviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent reviewIntent = new Intent(getActivity(), ReviewActivity.class).putExtra(Intent.EXTRA_TEXT, currentMovie.id);
                    startActivity(reviewIntent);
                }
            });

            //Check if movie is in favorites, if so mark checked
            if (currentMovie.isInFavorites(context)) {
                favoritesCheckbox.setChecked(true);
            }
            //Adds to Favorite when checked, Removes from Favorites when unchecked
            favoritesCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean checked = ((CheckBox) v).isChecked();
                    if (checked) {
                        currentMovie.addMovieToFavorites(context);


                    } else {
                        currentMovie.removeMovieFromFavorites(context);
                    }
                }
            });

            //Fetches Trailers from the Web using Asynctask
            FetchTrailers fetchTrailers = new FetchTrailers();
            fetchTrailers.execute();


            //Starts YouTube Intent on Click of any trailer item
            trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String currentTrailer = trailerAdapter.getItem(position);
                    Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentTrailer));
                    startActivity(Intent.createChooser(youtubeIntent,"Watch the Trailer"));
                }
            });
        }
        return rootView;

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_movie_detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

    }
    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out the Trailer");
        if(trailerAdapter.getItem(0)!= null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, trailerAdapter.getItem(0));
        }
        return shareIntent;
    }

    //Saves currentMovieJSON serial in SavedInstanceState
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("Movie Object", currentMovieJSON);
        super.onSaveInstanceState(outState);
    }



    public class FetchTrailers extends AsyncTask<Void, Void, String[]> {
        private final String LOG_TAG = FetchTrailers.class.getSimpleName();

        //Method for parsing JSON data and creating an array of Trailer Strings
        private String[] getTrailerDatafromJson(String trailerJsonString)
                throws JSONException {
            final String OWM_RESULTS = "results";
            final String OWM_ID = "id";

            JSONObject trailerJson = new JSONObject(trailerJsonString);
            JSONArray trailerArray = trailerJson.getJSONArray(OWM_RESULTS);
            String[] trailerStrings = new String[trailerArray.length()];

            for (int i = 0; i < trailerArray.length(); i++) {

                JSONObject trailerObject = trailerArray.getJSONObject(i);
                trailerStrings[i] = "https://www.youtube.com/watch?v=" + trailerObject.getString(OWM_ID);
            }

            return trailerStrings;
        }

        //Fetches Trailers from the Movie Database
        @Override
        protected String[] doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String trailersJsonString = null;

            try {

                URL url = new URL("http://api.themoviedb.org/3/movie/" + currentMovie.id + "/videos?&api_key=fbbb6188327c43a3f9732cdecc794495");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                trailersJsonString = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getTrailerDatafromJson(trailersJsonString); //Returns Array of Trailer Links parsed from the JSON String
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        //Clears and assigns results to the trailer Adapter, thereby changing the Array List passed into it
        @Override
        protected void onPostExecute(String[] result) {
            //Checks if result is null
            if (result != null) {
                trailerAdapter.clear();
                trailerAdapter.addAll(result);
                trailerAdapter.notifyDataSetChanged();
                if (mShareActionProvider != null ) {
                    mShareActionProvider.setShareIntent(createShareForecastIntent());
                }
            } else{ // If no trailers, Show Toast
                Toast noTrailersToast = Toast.makeText(getActivity(),"No trailers for this movie", Toast.LENGTH_LONG);
                noTrailersToast.show();
            }
        }
    }

   }

