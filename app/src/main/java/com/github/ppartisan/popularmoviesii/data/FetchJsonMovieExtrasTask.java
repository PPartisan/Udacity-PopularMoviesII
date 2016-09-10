package com.github.ppartisan.popularmoviesii.data;

import android.os.AsyncTask;

import com.github.ppartisan.popularmoviesii.utils.FetchJsonMovieDataUtils;

import java.lang.ref.WeakReference;

public class FetchJsonMovieExtrasTask extends AsyncTask<String, Void, FetchJsonMovieExtrasTask.Result> {

    private final WeakReference<OnJsonMovieExtrasReadyListener> mListener;

    public FetchJsonMovieExtrasTask(WeakReference<OnJsonMovieExtrasReadyListener> listener) {
        this.mListener = listener;
    }

    @Override
    protected Result doInBackground(String... movieId) {

        if (movieId == null || movieId.length < 1) {
            return null;
        }

        final String reviewRequest = FetchJsonMovieDataUtils.getMovieReviewRequestString(movieId[0]);
        final String reviewResult = FetchJsonMovieDataUtils.getMovieDatabaseJsonString(reviewRequest);

        final String trailerRequest = FetchJsonMovieDataUtils.getMovieTrailersRequestString(movieId[0]);
        final String trailerResult = FetchJsonMovieDataUtils.getMovieDatabaseJsonString(trailerRequest);

        return new Result(trailerResult, reviewResult);
    }

    @Override
    protected void onPostExecute(Result results) {
        if (mListener.get() != null && results.isNotHoldingNullVariables()) {
            mListener.get().onJsonMovieExtrasReady(results);
        } else if (mListener.get() != null && results == null) {
            //Offline, but might still be a favourite!
            mListener.get().onDataBaseMovieExtrasError();
        }
    }

    public interface OnJsonMovieExtrasReadyListener {

        void onJsonMovieExtrasReady(Result jsonResults);
        void onDataBaseMovieExtrasError();

    }

    public static final class Result {

        public final String trailersJsonString;
        public final String reviewsJsonString;

        public Result(String trailersJsonString, String reviewsJsonString) {
            this.trailersJsonString = trailersJsonString;
            this.reviewsJsonString = reviewsJsonString;
        }

        public boolean isNotHoldingNullVariables() {
            return (trailersJsonString != null) && (reviewsJsonString != null);
        }

    }

}
