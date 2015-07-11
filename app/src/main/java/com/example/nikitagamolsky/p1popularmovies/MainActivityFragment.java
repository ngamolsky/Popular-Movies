package com.example.nikitagamolsky.p1popularmovies;

import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private MoviePosterAdapter moviesAdapter;
    private String sortType =  "sort_by=popularity.desc";


    public MainActivityFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_sort_popularity) {
            sortType = "sort_by=popularity.desc";
            FetchMovies moviesTask = new FetchMovies();
            moviesTask.execute();
            return true;
        }
        if (id == R.id.action_sort_rated) {
            sortType = "sort_by=vote_average.desc";
            FetchMovies moviesTask = new FetchMovies();
            moviesTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        Movie movie = new Movie();
        List<Movie> movieList = new ArrayList<Movie>(Arrays.asList(movie));
        moviesAdapter = new MoviePosterAdapter(getActivity(), movieList);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == 1) {
            gridView.setNumColumns(3);
            gridView.setColumnWidth(width/3);
        }
        if (orientation == 2) {
            gridView.setNumColumns(5);
            gridView.setColumnWidth(width/5);
        }
        gridView.setAdapter(moviesAdapter);
        FetchMovies moviesTask = new FetchMovies();
        moviesTask.execute();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie currentMovie = moviesAdapter.getItem(position);
                String[] currentMovieArray = currentMovie.movieToArray();

                Intent movieDetailIntent = new Intent(getActivity(), MovieDetailActivity.class).putExtra(Intent.EXTRA_TEXT,currentMovieArray);
                startActivity(movieDetailIntent);
            }
        });
        return rootView;
    }


    public class FetchMovies extends AsyncTask<Void, Void, Movie[]> {
        private final String LOG_TAG = FetchMovies.class.getSimpleName();

        private Movie[] getMoviesDatafromJson(String moviesJsonString)
                throws JSONException {
            final String OWM_RESULTS = "results";
            final String OWM_POSTER = "poster_path";
            final String OWM_ORIGINAL_TITLE = "original_title";
            final String OWM_SYNOPSIS = "overview";
            final String OWM_RELEASE_DATE = "release_date";
            final String OWM_RATING = "vote_average";

            JSONObject moviesJson = new JSONObject(moviesJsonString);
            JSONArray moviesArray = moviesJson.getJSONArray(OWM_RESULTS);
            Movie[] resultObjects = new Movie[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {



                JSONObject movieObject = moviesArray.getJSONObject(i);
                resultObjects[i] = new Movie("http://image.tmdb.org/t/p/w500/" + movieObject.getString(OWM_POSTER),movieObject.getString(OWM_ORIGINAL_TITLE),movieObject.getString(OWM_SYNOPSIS),movieObject.getString(OWM_RELEASE_DATE),movieObject.getString(OWM_RATING));

            }



            return resultObjects;
        }


        @Override
        protected Movie[] doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonString = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.themoviedb.org/3/discover/movie?" + sortType + "&api_key=fbbb6188327c43a3f9732cdecc794495");

                // Create the request to OpenWeatherMap, and open the connection
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
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonString = buffer.toString();
                //Log.v(LOG_TAG, "Movie Json String" + moviesJsonString);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
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
                return getMoviesDatafromJson(moviesJsonString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {

                moviesAdapter.clear();
                moviesAdapter.addAll(result);

                // New data is back from the server.  Hooray!
            }
        }
    }
}


