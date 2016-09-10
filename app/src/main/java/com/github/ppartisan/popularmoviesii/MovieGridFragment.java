package com.github.ppartisan.popularmoviesii;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.github.ppartisan.popularmoviesii.adapter.MovieGridAdapter;
import com.github.ppartisan.popularmoviesii.model.MovieModel;
import com.github.ppartisan.popularmoviesii.service.FetchJsonMovieDataService;
import com.github.ppartisan.popularmoviesii.service.MovieJsonStringReceiver;
import com.github.ppartisan.popularmoviesii.utils.FetchJsonMovieDataUtils;
import com.github.ppartisan.popularmoviesii.utils.JsonMovieDatabaseParser;
import com.github.ppartisan.popularmoviesii.utils.MetricUtils;
import com.github.ppartisan.popularmoviesii.utils.ViewUtils;

import org.json.JSONException;

import java.util.List;

public class MovieGridFragment extends Fragment implements
        MovieJsonStringReceiver.OnJsonMovieDataReadyListener, ViewTreeObserver.OnGlobalLayoutListener {

    private static final String SORT_PREFERENCE_KEY = "sort_pref_key";
    private static final String RECYCLER_VIEW_POSITION_KEY = "rv_position_key";
    private static final String IS_SHOWING_FAVOURITES_KEY = "is_showings_favourites_key";

    private Callbacks mCallbacks;

    private JsonMovieDatabaseParser parser;

    private MovieGridAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private MovieJsonStringReceiver mMovieJsonStringReceiver;

    private String sortPreference = FetchJsonMovieDataUtils.SORT_BY_POPULARITY;

    private boolean isShowingFavourites = false;
    private MenuItem mSortPreferenceItem, mFavItem;

    public static MovieGridFragment newInstance() {

        Bundle args = new Bundle();

        MovieGridFragment fragment = new MovieGridFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallbacks = (Callbacks) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement " +
                    Callbacks.class.getName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovieJsonStringReceiver = new MovieJsonStringReceiver(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View root = inflater.inflate(R.layout.fragment_main, container, false);

        mAdapter = new MovieGridAdapter(mCallbacks);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.fm_recycler);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        parser = new JsonMovieDatabaseParser();

        if (savedInstanceState != null) {
            sortPreference = savedInstanceState.getString(
                    SORT_PREFERENCE_KEY, FetchJsonMovieDataUtils.SORT_BY_POPULARITY
            );
            isShowingFavourites = savedInstanceState.getBoolean(IS_SHOWING_FAVOURITES_KEY);
        }

        return root;

    }

    @Override
    public void onStart() {
        super.onStart();
        if (isShowingFavourites) {
            mCallbacks.launchFavouritesTask();
        } else {
            launchFetchMovieDataTask(sortPreference);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter movieJsonRequestFilter =
                new IntentFilter(FetchJsonMovieDataService.ACTION_COMPLETE);
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(mMovieJsonStringReceiver,movieJsonRequestFilter);
        restoreRecyclerViewState();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMovieJsonStringReceiver);
        saveRecyclerViewState();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SORT_PREFERENCE_KEY, sortPreference);
        outState.putBoolean(IS_SHOWING_FAVOURITES_KEY, isShowingFavourites);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mSortPreferenceItem == null) {
            mSortPreferenceItem = menu.findItem(R.id.action_sort_parent);
        }

        if (mFavItem == null) {
            mFavItem = menu.findItem(R.id.action_favorites);
        }

        updateFavouriteUiElements();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_sort_popularity:
                launchFetchMovieDataTask(FetchJsonMovieDataUtils.SORT_BY_POPULARITY);
                break;
            case R.id.action_sort_votes:
                launchFetchMovieDataTask(FetchJsonMovieDataUtils.SORT_BY_VOTE);
                break;
            case R.id.action_favorites:

                String message;

                if (!isShowingFavourites) {
                    isShowingFavourites = true;
                    mCallbacks.launchFavouritesTask();
                    updateFavouriteUiElements();
                    message = getString(R.string.df_action_favourites_showing);
                } else {
                    isShowingFavourites = false;
                    launchFetchMovieDataTask(sortPreference);
                    updateFavouriteUiElements();
                    message = getString(R.string.df_action_live_data_showing);
                }

                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onJsonMovieDataReady(String movieDataJson) {
        try {
            List<MovieModel> movieModels = parser.getMovieModelsFromJson(movieDataJson);
            mCallbacks.onJsonMovieDataLoaded(movieModels.get(0));
            mAdapter.setMovieModels(movieModels);
            restoreRecyclerViewState();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onJsonMovieDataRetrievalError() {
        new AlertDialog.Builder(getContext(), R.style.AppDialogTheme)
                .setMessage(getString(R.string.alert_no_connection_content))
                .setNegativeButton(getString(R.string.alert_dismiss), null)
                .show();
    }

    public void updateAdapter(List<MovieModel> models) {
        if (isShowingFavourites) {
            mAdapter.setMovieModels(models);
            restoreRecyclerViewState();
        }
    }

    private void launchFetchMovieDataTask(String sortCode) {
        sortPreference = sortCode;
        saveRecyclerViewState();
        Intent fetchMovieJsonDataIntent = new Intent(getContext(),FetchJsonMovieDataService.class);
        fetchMovieJsonDataIntent.putExtra(FetchJsonMovieDataService.SORT_CODE_KEY, sortCode);
        getActivity().startService(fetchMovieJsonDataIntent);
    }

    private void restoreRecyclerViewState() {
        Parcelable parcelable = getArguments().getParcelable(RECYCLER_VIEW_POSITION_KEY);
        if (mRecyclerView.getLayoutManager() != null) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
        }
    }

    private void saveRecyclerViewState() {
        if (mRecyclerView.getLayoutManager() != null) {
            getArguments().putParcelable(
                    RECYCLER_VIEW_POSITION_KEY, mRecyclerView.getLayoutManager().onSaveInstanceState()
            );
        }
    }

    private void updateFavouriteUiElements() {

        final int primaryDark = ContextCompat.getColor(getContext(), R.color.primary_dark);
        int id;

        if (isShowingFavourites) {
            id = R.drawable.ic_favorite_white_24dp;
            mSortPreferenceItem.setEnabled(false);
            mSortPreferenceItem.getIcon().setColorFilter(primaryDark, PorterDuff.Mode.MULTIPLY);
        } else {
            id = R.drawable.ic_favorite_border_white_24dp;
            mSortPreferenceItem.setEnabled(true);
            mSortPreferenceItem.getIcon().clearColorFilter();
        }

        mFavItem.setIcon(id);

        mCallbacks.onIsShowingFavouritesChange(isShowingFavourites);

    }

    @Override
    public void onGlobalLayout() {
        if (mRecyclerView.getWidth() > 0) {
            final int columnCount = MetricUtils.getColumnCountForViewWidth(mRecyclerView);
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), columnCount));
            ViewUtils.removeOnGlobalLayoutListener(mRecyclerView, this);
            restoreRecyclerViewState();
        }
    }

    public interface Callbacks extends MovieGridAdapter.OnMovieAdapterItemClickListener {

        void onJsonMovieDataLoaded(MovieModel model);
        void launchFavouritesTask();
        void onIsShowingFavouritesChange(boolean isShowingFavourites);

    }

}
