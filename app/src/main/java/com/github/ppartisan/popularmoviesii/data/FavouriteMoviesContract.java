package com.github.ppartisan.popularmoviesii.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public final class FavouriteMoviesContract {

    public static final String CONTENT_AUTHORITY = "com.github.ppartisan.popularmoviesii";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_EXTRAS = "extras";

    public static final class MoviesEntry implements BaseColumns {

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "movie_title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_AVERAGE_VOTE = "average_vote";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildFavouriteMovieUri(long movieId) {
            return ContentUris.withAppendedId(MoviesEntry.CONTENT_URI, movieId);
        }

    }

    public static final class ExtrasEntry implements BaseColumns {

        public static final String TABLE_NAME = "extras";

        public static final String COLUMN_MOVIE_ID = MoviesEntry.COLUMN_MOVIE_ID;
        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_REVIEW_AUTHOR = "review_author";
        public static final String COLUMN_REVIEW_CONTENT = "review_content";
        public static final String COLUMN_REVIEW_URL = "review_url";
        public static final String COLUMN_TRAILER_TITLE = "trailer_title";
        public static final String COLUMN_TRAILER_SOURCE = "trailer_source";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EXTRAS).build();

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXTRAS;

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXTRAS;

        public static Uri buildFavouriteExtraUri(long extraId) {
            return ContentUris.withAppendedId(ExtrasEntry.CONTENT_URI, extraId);
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

}
