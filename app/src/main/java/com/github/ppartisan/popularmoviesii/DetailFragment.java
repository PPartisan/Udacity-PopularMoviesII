package com.github.ppartisan.popularmoviesii;

import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ppartisan.popularmoviesii.adapter.ReviewAdapter;
import com.github.ppartisan.popularmoviesii.adapter.TrailerAdapter;
import com.github.ppartisan.popularmoviesii.data.FavouriteMoviesOperations;
import com.github.ppartisan.popularmoviesii.data.FetchDataBaseMovieExtrasTask;
import com.github.ppartisan.popularmoviesii.data.FetchJsonMovieExtrasTask;
import com.github.ppartisan.popularmoviesii.model.MovieModel;
import com.github.ppartisan.popularmoviesii.model.ReviewModel;
import com.github.ppartisan.popularmoviesii.model.TrailerModel;
import com.github.ppartisan.popularmoviesii.utils.CursorMovieDatabaseParser;
import com.github.ppartisan.popularmoviesii.utils.JsonMovieDatabaseParser;
import com.github.ppartisan.popularmoviesii.view.RatingsView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

public class DetailFragment extends Fragment implements
        FetchJsonMovieExtrasTask.OnJsonMovieExtrasReadyListener,
        ReviewAdapter.OnReviewAdapterItemClickListener, View.OnClickListener,
        FetchDataBaseMovieExtrasTask.OnDataBaseMovieExtrasReadyListener {

    private static final String IS_FAVOURITE_KEY = "is_fav_key";

    private FetchJsonMovieExtrasTask jsonTask = null;
    private FetchDataBaseMovieExtrasTask dataTask = null;

    private JsonMovieDatabaseParser mJsonParser;
    private CursorMovieDatabaseParser mCursorParser;

    private TrailerAdapter mTrailersAdapter;
    private ReviewAdapter mReviewsAdapter;

    private CustomTabsIntent mCustomTabIntent;
    private FavouriteMoviesOperations mFavouritesOps;

    private FloatingActionButton mFab;

    private boolean isFavourite = false;

    public static DetailFragment newInstance(MovieModel model) {

        Bundle args = new Bundle();
        args.putParcelable(DetailActivity.DETAIL_MODEL_KEY, model);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if (jsonTask == null) {
            jsonTask = new FetchJsonMovieExtrasTask(
                    new WeakReference<FetchJsonMovieExtrasTask.OnJsonMovieExtrasReadyListener>(this)
            );
            jsonTask.execute(getModel().id);
        }
        mJsonParser = new JsonMovieDatabaseParser();
        mCursorParser = new CursorMovieDatabaseParser();
        mFavouritesOps = new FavouriteMoviesOperations.MovieModelOperations(getContext().getContentResolver());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_detail, container, false);

        final TextView title = (TextView) root.findViewById(R.id.df_movie_title);
        final ImageView thumbnail = (ImageView) root.findViewById(R.id.fd_thumbnail);
        final TextView ratingsText = (TextView) root.findViewById(R.id.df_ratings_text);
        final RatingsView ratingsView = (RatingsView) root.findViewById(R.id.df_ratings_view);
        final TextView releaseDate = (TextView) root.findViewById(R.id.df_release_date_text);
        final TextView synopsis = (TextView) root.findViewById(R.id.df_synopsis_text);

        MovieModel model = getModel();
        title.setText(model.title);
        final String rating = String.format(Locale.getDefault(),"%.1f", model.averageVote);
        ratingsText.setText(getString(R.string.df_rating_content, rating));
        ratingsView.setScore(model.averageVote);
        releaseDate.setText(model.releaseDate);
        synopsis.setText(model.synopsis);

        Picasso.with(getContext()).load(model.imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(thumbnail);

        RecyclerView mTrailersRecyclerView = (RecyclerView) root.findViewById(R.id.df_trailers_recycler);
        mTrailersAdapter = new TrailerAdapter(model.getTrailers());
        mTrailersRecyclerView.setAdapter(mTrailersAdapter);
        mTrailersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mTrailersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mTrailersRecyclerView.setNestedScrollingEnabled(false);

        RecyclerView mReviewsRecyclerView = (RecyclerView) root.findViewById(R.id.df_reviews_recycler);
        mReviewsAdapter = new ReviewAdapter(model.getReviews(), this);
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);
        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mReviewsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mReviewsRecyclerView.setNestedScrollingEnabled(false);

        final int primaryColor = ContextCompat.getColor(getContext(), R.color.primary);
        mCustomTabIntent = new CustomTabsIntent.Builder()
                .setToolbarColor(primaryColor)
                .setSecondaryToolbarColor(primaryColor)
                .build();

        mFab = (FloatingActionButton) root.findViewById(R.id.df_fav);
        mFab.setOnClickListener(this);

        if (!(savedInstanceState == null)) isFavourite = savedInstanceState.getBoolean(IS_FAVOURITE_KEY);
        setIsFavourite(isFavourite);

        return root;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_FAVOURITE_KEY, isFavourite);
    }

    private MovieModel getModel() {
        return getArguments().getParcelable(DetailActivity.DETAIL_MODEL_KEY);
    }

    @Override
    public void onJsonMovieExtrasReady(FetchJsonMovieExtrasTask.Result result) {

        try {
            final List<ReviewModel> reviews =
                    mJsonParser.getReviewModelListFromJson(result.reviewsJsonString);
            final List<TrailerModel> trailers =
                    mJsonParser.getTrailerModelListFromJson(result.trailersJsonString);
            getModel().setReviews(reviews);
            getModel().setTrailers(trailers);
            mTrailersAdapter.setTrailers(getModel().getTrailers());
            mReviewsAdapter.setReviews(getModel().getReviews());

            launchFetchExtrasDataTask(true);

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            jsonTask = null;
        }
    }

    @Override
    public void onDataBaseMovieExtrasError() {
        launchFetchExtrasDataTask(false);
    }

    @Override
    public void onAdapterItemLinkClick(String targetUrl) {
        mCustomTabIntent.launchUrl(getActivity(), Uri.parse(targetUrl));
    }

    @Override
    public void onAdapterItemExpandClick(int position) {
        final ReviewModel review = mReviewsAdapter.getReviews().get(position);
        new AlertDialog.Builder(getContext(), R.style.AppDialogTheme)
                .setTitle(getString(R.string.df_rl_expand_title, review.author))
                .setMessage(review.content)
                .setNegativeButton(getString(R.string.alert_dismiss), null)
                .show();
    }

    @Override
    public void onClick(View view) {

        try {
            if (isFavourite) {
                mFavouritesOps.deleteMovieModel(getModel());
                setIsFavourite(false);
                deletionSnackBar().show();
            } else {
                mFavouritesOps.insertMovieModel(getModel());
                setIsFavourite(true);
                insertionSnackBar().show();
            }
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCursorReady(Cursor cursor, boolean isOnline) {

        try {

            if (!mCursorParser.isFavourite(cursor)) {
                setIsFavourite(false);
                return;
            } else {
                setIsFavourite(true);
            }

            MovieModel model = mCursorParser.attachExtrasToMovieModelFromCursor(cursor, getModel());

            getModel().setReviews(model.getReviews());
            getModel().setTrailers(model.getTrailers());

        } finally {

            if (!cursor.isClosed()) cursor.close();

        }

    }

    private void launchFetchExtrasDataTask(boolean isOnline) {

        if (dataTask == null) {
            dataTask = new FetchDataBaseMovieExtrasTask(
                    getActivity().getContentResolver(),
                    new WeakReference<FetchDataBaseMovieExtrasTask.OnDataBaseMovieExtrasReadyListener>(this),
                    isOnline
            );
            dataTask.execute(getModel().id);

        }

    }

    private void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
        if (isFavourite) {
            mFab.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else {
            mFab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private Snackbar deletionSnackBar() {

        final String message = getString(R.string.df_sb_removed_from_favourites);
        final String undo = getString(R.string.df_sb_undo);

        Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT);
        snackbar.setAction(undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            mFavouritesOps.insertMovieModel(getModel());
                            setIsFavourite(true);
                            restoredSnackBar().show();
                        } catch (RemoteException | OperationApplicationException e) {
                            e.printStackTrace();
                        }
                    }
                });

        return snackbar;

    }

    @SuppressWarnings("ConstantConditions")
    private Snackbar insertionSnackBar() {

        final String message = getString(R.string.df_sb_added_to_favourites);
        final String undo = getString(R.string.df_sb_undo);

        Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT);
        snackbar.setAction(undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            mFavouritesOps.deleteMovieModel(getModel());
                            setIsFavourite(false);
                            removedSnackBar().show();
                        } catch (RemoteException | OperationApplicationException e) {
                            e.printStackTrace();
                        }
                    }
                });

        return snackbar;

    }

    @SuppressWarnings("ConstantConditions")
    private Snackbar restoredSnackBar() {
        final String message = getString(R.string.df_sb_restored_to_favourites);
        return Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT);
    }

    @SuppressWarnings("ConstantConditions")
    private Snackbar removedSnackBar() {
        final String message = getString(R.string.df_sb_removed_from_favourites);
        return Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT);
    }

}
