package com.github.ppartisan.popularmoviesii.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.github.ppartisan.popularmoviesii.utils.FetchJsonMovieDataUtils;

public class FetchJsonMovieDataService extends IntentService {

    private static final String TAG = FetchJsonMovieDataService.class.getSimpleName();

    public static final String SORT_CODE_KEY = "sort_code_key";
    public static final String MOVIE_JSON_STRING_RESULT_KEY = "json_string_result_key";
    public static final String ACTION_COMPLETE = FetchJsonMovieDataUtils.class.getName() + ".COMPLETE";

    @SuppressWarnings("unused")
    public FetchJsonMovieDataService() {
        super(TAG);
    }

    @SuppressWarnings("unused")
    public FetchJsonMovieDataService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String sortByCode = intent.getStringExtra(SORT_CODE_KEY);
        if (sortByCode == null) sortByCode = FetchJsonMovieDataUtils.SORT_BY_POPULARITY;

        final String request = FetchJsonMovieDataUtils.getMovieDatabaseRequestString(sortByCode);
        final String result =
                FetchJsonMovieDataUtils.getMovieDatabaseJsonString(request);

        Intent resultIntent = new Intent(ACTION_COMPLETE);
        resultIntent.putExtra(MOVIE_JSON_STRING_RESULT_KEY, result);

        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);

    }

}
