package com.github.ppartisan.popularmoviesii.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class MovieJsonStringReceiver extends BroadcastReceiver {

    private OnJsonMovieDataReadyListener mListener;

    public MovieJsonStringReceiver(OnJsonMovieDataReadyListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String result =
                intent.getStringExtra(FetchJsonMovieDataService.MOVIE_JSON_STRING_RESULT_KEY);

        if (result == null) {
            mListener.onJsonMovieDataRetrievalError();
        } else {
            mListener.onJsonMovieDataReady(result);
        }

    }

    public interface OnJsonMovieDataReadyListener {
        void onJsonMovieDataReady(String movieDataJson);
        void onJsonMovieDataRetrievalError();
    }

}
