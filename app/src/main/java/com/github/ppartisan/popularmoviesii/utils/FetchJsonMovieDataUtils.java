package com.github.ppartisan.popularmoviesii.utils;

import android.net.Uri;
import android.util.Log;

import com.github.ppartisan.popularmoviesii.ApiKey;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;


public final class FetchJsonMovieDataUtils {

    public static final String TAG = FetchJsonMovieDataUtils.class.getSimpleName();

    private static final String MOVIE_REQUEST_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String API_KEY_PARAM = "api_key";

    private static final String REVIEWS_PARAM="reviews";
    private static final String TRAILERS_PARAM="trailers";

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p";

    public static final String SORT_BY_VOTE = "top_rated";
    public static final String SORT_BY_POPULARITY = "popular";

    public static final String IMAGE_WIDTH = "w500";

    private static final String INPUT_DATE_PATTERN = "yyyy-MM-dd";
    private static final String OUTPUT_DATE_PATTERN = "dd MMMM yyyy";

    private static final SimpleDateFormat INPUT_DATE_FORMAT =
            new SimpleDateFormat(INPUT_DATE_PATTERN, Locale.getDefault());
    private static final SimpleDateFormat OUTPUT_DATE_FORMAT =
            new SimpleDateFormat(OUTPUT_DATE_PATTERN, Locale.getDefault());

    private FetchJsonMovieDataUtils() { throw new AssertionError(); }

    public static String getMovieDatabaseRequestString(String sortCriteria) {
        return Uri.parse(MOVIE_REQUEST_BASE_URL).buildUpon()
                .appendPath(sortCriteria)
                .appendQueryParameter(API_KEY_PARAM, ApiKey.KEY).build().toString();
    }

    public static String getMovieReviewRequestString(String movieId) {
        return Uri.parse(MOVIE_REQUEST_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(REVIEWS_PARAM)
                .appendQueryParameter(API_KEY_PARAM, ApiKey.KEY).build().toString();
    }

    public static String getMovieTrailersRequestString(String movieId) {
        return Uri.parse(MOVIE_REQUEST_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(TRAILERS_PARAM)
                .appendQueryParameter(API_KEY_PARAM, ApiKey.KEY).build().toString();
    }

    public static String getMovieDatabaseJsonString(String urlString) {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        String resultString = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            if (inputStream == null) {
                return null;
            }
            StringBuilder builder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }

            if (builder.length() == 0) {
                return null;
            }

            resultString = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) connection.disconnect();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }

        return resultString;

    }

    public static String getImageUrlFromRelativePath(String relativePath) {
        return getImageUrlFromRelativePath(IMAGE_WIDTH, relativePath);
    }

    public static String getImageUrlFromRelativePath(String preferredWidth, String relativePath) {
        if (relativePath.charAt(0) == '/') {
            relativePath = relativePath.substring(1);
        }
        return Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(preferredWidth).appendPath(relativePath).build().toString();
    }

    public static String getDateInReadableFormat(String inputDate) {

        try {
            return OUTPUT_DATE_FORMAT.format(INPUT_DATE_FORMAT.parse(inputDate));
        } catch (ParseException e) {
            e.printStackTrace();
            return inputDate;
        }

    }

}
