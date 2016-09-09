package com.github.ppartisan.popularmoviesii;


import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.ppartisan.popularmoviesii.data.FavouriteMoviesContract;
import com.github.ppartisan.popularmoviesii.model.MovieModel;
import com.github.ppartisan.popularmoviesii.utils.CursorMovieDatabaseParser;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        MovieGridFragment.Callbacks{

    private boolean mIsTwoPane;
    private CursorMovieDatabaseParser mCursorParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIsTwoPane = (findViewById(R.id.fragment_detail_container) != null);

        if(getSupportFragmentManager().findFragmentById(R.id.fragment_main_container) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_main_container, MovieGridFragment.newInstance())
                    .commit();
        }

        mCursorParser = new CursorMovieDatabaseParser();

    }

    @Override
    public void onMovieAdapterItemClick(MovieModel model) {
        if (mIsTwoPane) {
            replaceDetailFragment(model);
        } else {
            launchDetailActivity(model);
        }
    }

    @Override
    public void onJsonMovieDataLoaded(MovieModel model) {
        if (mIsTwoPane && getSupportFragmentManager().findFragmentById(R.id.fragment_detail_container) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_detail_container, DetailFragment.newInstance(model))
                    .commit();
        }
    }

    @Override
    public void launchFavouritesTask() {
        getSupportLoaderManager().initLoader(0, null, MainActivity.this);
    }

    private void launchDetailActivity(MovieModel model) {
        Intent launchDetailActivityIntent = new Intent(this, DetailActivity.class);
        launchDetailActivityIntent.putExtra(DetailActivity.DETAIL_MODEL_KEY, model);
        startActivity(launchDetailActivityIntent);
    }

    private void replaceDetailFragment(MovieModel model) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_detail_container, DetailFragment.newInstance(model)).commit();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, FavouriteMoviesContract.MoviesEntry.CONTENT_URI,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<MovieModel> models = mCursorParser.getMovieModelsFromCursor(data);
        MovieGridFragment frag =
                (MovieGridFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_main_container);
        frag.updateAdapter(models);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

}
