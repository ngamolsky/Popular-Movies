package com.example.nikitagamolsky.p1popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

//Activity for Displaying Reviews
public class ReviewActivityFragment extends Fragment {
    private ArrayAdapter<String> reviewAdapter; //Basic Adapter for Listing the Reviews



    public ReviewActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Gets variables for the Review View
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);
        ListView reviewlistview = (ListView) rootView.findViewById(R.id.reviewsList);

        //Initializes Array and Adapter containing reviews
        ArrayList<String> reviewList = new ArrayList<>(0);
        reviewAdapter = new ArrayAdapter<>(getActivity(), R.layout.reviewtext, reviewList);

        //Sets Adapter to the review view
        reviewlistview.setAdapter(reviewAdapter);

        //Fetches Reviews
        FetchReviews fetchReviews = new FetchReviews();
        fetchReviews.execute();
        return rootView;
    }
    //Async Task returning String Array of Reviews
    public class FetchReviews extends AsyncTask<Void, Void, String[]> {
        Intent intent = getActivity().getIntent();
        String currentMovieID = intent.getStringExtra(Intent.EXTRA_TEXT);
        private final String LOG_TAG = FetchReviews.class.getSimpleName();

        private String[] getReviewDatafromJson(String reviewJsonString)
                throws JSONException {
            final String OWM_RESULTS = "results";
            final String OWM_AUTHOR = "author";
            final String OWM_CONTENT = "content";

            JSONObject reviewJson = new JSONObject(reviewJsonString);
            JSONArray reviewArray = reviewJson.getJSONArray(OWM_RESULTS);
            String[] reviewStrings = new String[reviewArray.length()];

            for (int i = 0; i < reviewArray.length(); i++) {

                JSONObject reviewObject = reviewArray.getJSONObject(i);
                reviewStrings[i] = reviewObject.getString(OWM_CONTENT)  + System.getProperty("line.separator") + System.getProperty("line.separator") + " - " + reviewObject.getString(OWM_AUTHOR);
            }

            return reviewStrings;

        }

        @Override
        protected String[] doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String reviewsJsonString = null;

            try {

                URL url = new URL("http://api.themoviedb.org/3/movie/" + currentMovieID + "/reviews?&api_key=fbbb6188327c43a3f9732cdecc794495");

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
                reviewsJsonString = buffer.toString();
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
                return getReviewDatafromJson(reviewsJsonString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {

                reviewAdapter.clear();
                reviewAdapter.addAll(result);
                reviewAdapter.notifyDataSetChanged();


            }

            else{
                reviewAdapter.clear();
                reviewAdapter.add("No Reviews");
                reviewAdapter.notifyDataSetChanged();
            }
        }

    }
}
