package com.example.nikitagamolsky.p1popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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


/**
This is the Main Fragment containing the Popular Movies List
 */
public class MainActivityFragment extends Fragment {
    private MoviePosterAdapter moviesAdapter; // The custom adapter for the Gridvew of movies
    private String sortType = "sort_by=popularity.desc"; // The String containing the sort type for the Movie Database Query
    private ArrayList<Movie> currentMovieList = new ArrayList<>(); //ArrayList containing all the movie objects from the Fetch Movies Task

    public MainActivityFragment() {
    }

    public interface Callback {
        void onItemSelected(String currentMovieJSON);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //Initialize FavoritePreferences
        Context context = getActivity();

        //Changes sortType to Fetch Movies by popularity
        if (id == R.id.action_sort_popularity) {
            sortType = "sort_by=popularity.desc";
            FetchMovies moviesTask = new FetchMovies();
            moviesTask.execute();
            return true;
        }

        //Changes sortType to Fetch Movies by rating
        if (id == R.id.action_sort_rated) {
            sortType = "sort_by=vote_average.desc";
            FetchMovies moviesTask = new FetchMovies();
            moviesTask.execute();
            return true;
        }

        //Displays the list of Favorite Movies
        if (id == R.id.action_sort_favorites) {
            sortType = "sort by Favorites";
            Cursor cursor = context.getContentResolver().query(FavoritesContract.FavoritesEntry.CONTENT_URI, null, null, null, null);
            try{
                moviesAdapter.clear();
                moviesAdapter.addAll(FavoritesProvider.getFavoritesFromCursor(cursor));
                moviesAdapter.notifyDataSetChanged();}
            finally {cursor.close();}
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Initializes context
        Context context = getActivity();

        //Checks if there is a savedInstanceState, if so, recovers data without connecting to the web
        if (savedInstanceState == null) {
            FetchMovies moviesTask = new FetchMovies();
            moviesTask.execute();

        } else {
            currentMovieList = savedInstanceState.getParcelableArrayList("currentMovieList");

            //Checks if last sort type was sort by Favorites, if so refreshes Favorites list
            if (savedInstanceState.getString("savedText", null).equals("sort by Favorites")) {
                Cursor cursor = context.getContentResolver().query(FavoritesContract.FavoritesEntry.CONTENT_URI, null, null, null, null);
                try{
                    moviesAdapter.clear();
                    moviesAdapter.addAll(FavoritesProvider.getFavoritesFromCursor(cursor));
                    moviesAdapter.notifyDataSetChanged();}
                finally {cursor.close();}
            }
        }


        //Initializes our custom adapter for the Gridview with the current Movie ArrayList data and fetches id's to identify Gridview
        moviesAdapter = new MoviePosterAdapter(getActivity(), currentMovieList);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.movieGridView);



        // Sets Custom Adapter to the Gridview
        gridView.setAdapter(moviesAdapter);

        //Create and Launch Intent on Item Click
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie currentMovie = moviesAdapter.getItem(position);

                //Create new Intent and add a String Array of data for the Item Clicked
                ((Callback) getActivity()).onItemSelected(currentMovie.movieToJSON());
            }
        });
        return rootView;
    }


    //Calls OnSaveInstanceState to save both the sortType and the ArrayList of movies
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("savedText", sortType);
        outState.putParcelableArrayList("currentMovieList", currentMovieList);
        super.onSaveInstanceState(outState);
    }

    public class FetchMovies extends AsyncTask<Void, Void, Movie[]> {
        private final String LOG_TAG = FetchMovies.class.getName();

        //Method for parsing JSON data and creating an array of Movie Objects
        private Movie[] getMoviesDatafromJson(String moviesJsonString)
                throws JSONException {
            final String OWM_RESULTS = "results";
            final String OWM_POSTER = "poster_path";
            final String OWM_ORIGINAL_TITLE = "original_title";
            final String OWM_SYNOPSIS = "overview";
            final String OWM_RELEASE_DATE = "release_date";
            final String OWM_RATING = "vote_average";
            final String OWM_ID = "id";

            JSONObject moviesJson = new JSONObject(moviesJsonString);
            JSONArray moviesArray = moviesJson.getJSONArray(OWM_RESULTS);
            Movie[] resultObjects = new Movie[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieObject = moviesArray.getJSONObject(i);
                resultObjects[i] = new Movie("http://image.tmdb.org/t/p/w300/" + movieObject.getString(OWM_POSTER),movieObject.getString(OWM_ORIGINAL_TITLE),movieObject.getString(OWM_SYNOPSIS),movieObject.getString(OWM_RELEASE_DATE),movieObject.getString(OWM_RATING),movieObject.getString(OWM_ID));
            }
            return resultObjects;
        }

        //Fetches Movies from the Movie Database
        @Override
        protected Movie[] doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonString = null;

            try {

                URL url = new URL("http://api.themoviedb.org/3/discover/movie?" + sortType + "&api_key=fbbb6188327c43a3f9732cdecc794495");
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
                moviesJsonString = buffer.toString();
            }
            catch (IOException e) {
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
                return getMoviesDatafromJson(moviesJsonString); //Returns Array of Movie Objects parsed from the JSON String
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        //Clears and assigns results to the custom Adapter, thereby changing the Array List passed into it
        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                Context context = getActivity();
                moviesAdapter.clear();
                moviesAdapter.addAll(result);
                moviesAdapter.notifyDataSetChanged();
                if (currentMovieList.isEmpty()){
                    Toast noMoviesToast = Toast.makeText(context, "There are no movies to display", Toast.LENGTH_LONG);
                    noMoviesToast.show();
                }

            }
        }
    }
}






