package com.example.nikitagamolsky.p1popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{
    boolean TwoPane;
    private static final String MOVIEDETAILFRAGMENT_TAG = "DFTAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Determines whether we are in the Tablet or Phone UI
        if (findViewById(R.id.movieDetailContainer) != null){
            TwoPane = true;


            //If there is no savedInstanceState, adds the Fragment
            if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.movieDetailContainer, new MovieDetailActivityFragment()).commit();
            }
        } else{
            TwoPane = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String currentMovieJSON) {
        if (TwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.

            //Adds current movie info
            Bundle args = new Bundle();
            args.putString("currentMovieJSON", currentMovieJSON);
            MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
            fragment.setArguments(args);

            //Replaces Fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movieDetailContainer, fragment, MOVIEDETAILFRAGMENT_TAG)
                    .commit();
        } else {// If in phone UI mode, start Intent
            Intent intent = new Intent(this, MovieDetailActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, currentMovieJSON);
            startActivity(intent);
        }
    }

}
