package com.github.ppartisan.popularmoviesii.data;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public class FetchDataBaseMovieExtrasTask extends AsyncTask<String, Void, Cursor> {

    private ContentResolver cr;
    private final WeakReference<OnDataBaseMovieExtrasReadyListener> mListener;
    private final boolean isOnline;

    public FetchDataBaseMovieExtrasTask(ContentResolver cr, WeakReference<OnDataBaseMovieExtrasReadyListener> listener, boolean isOnline) {
        mListener = listener;
        this.isOnline = isOnline;
        this.cr = cr;
    }

    @Override
    protected Cursor doInBackground(String... movieId) {

        if (movieId == null || movieId.length < 1) {
            throw new IllegalArgumentException("Valid movieId required");
        }

        final Uri request =
                FavouriteMoviesContract.MoviesEntry.buildFavouriteMovieUri(Long.parseLong(movieId[0]));

        final String[] columns = (isOnline)
                ? new String[] { FavouriteMoviesContract.MoviesEntry.COLUMN_MOVIE_ID }
                : null;

        return cr.query(
                request, columns, null, null, null
        );
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        cr = null;
        if (mListener.get() != null) {
            mListener.get().onCursorReady(cursor, isOnline);
        }
    }

    public interface OnDataBaseMovieExtrasReadyListener {
        void onCursorReady(Cursor cursor, boolean isOnline);
    }

}
