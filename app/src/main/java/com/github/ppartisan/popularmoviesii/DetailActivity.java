package com.github.ppartisan.popularmoviesii;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.ppartisan.popularmoviesii.model.MovieModel;

public class DetailActivity extends AppCompatActivity {

    public static final String DETAIL_MODEL_KEY = "detail_movie_model_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            MovieModel model = getIntent().getParcelableExtra(DETAIL_MODEL_KEY);
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, DetailFragment.newInstance(model))
                    .commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
