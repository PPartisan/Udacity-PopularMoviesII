package com.github.ppartisan.popularmoviesii.utils;


import android.database.Cursor;

import com.github.ppartisan.popularmoviesii.data.FavouriteMoviesContract.MoviesEntry;
import com.github.ppartisan.popularmoviesii.data.FavouriteMoviesContract.ExtrasEntry;
import com.github.ppartisan.popularmoviesii.model.MovieModel;
import com.github.ppartisan.popularmoviesii.model.ReviewModel;
import com.github.ppartisan.popularmoviesii.model.TrailerModel;

import java.util.ArrayList;
import java.util.List;

public final class CursorMovieDatabaseParser {

    public List<MovieModel> getMovieModelsFromCursor(Cursor cursor) {

        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }

        List<MovieModel> models = new ArrayList<>(cursor.getCount());

        do {

            final String id = cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_ID));
            final String title = cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_TITLE));
            final String releaseDate = cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_RELEASE_DATE));
            final String imageUrl = cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_IMAGE_URL));
            final String synopsis = cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_SYNOPSIS));
            final double averageVote = cursor.getDouble(cursor.getColumnIndex(MoviesEntry.COLUMN_AVERAGE_VOTE));

            MovieModel model = new MovieModel.Builder()
                    .id(id)
                    .title(title)
                    .releaseDate(releaseDate)
                    .imageUrl(imageUrl)
                    .synopsis(synopsis)
                    .averageVote(averageVote)
                    .build();

            models.add(model);

        } while (cursor.moveToNext());

        return models;

    }

    public boolean isFavourite(Cursor cursor) {
        return (cursor != null && cursor.moveToFirst());
    }

    public MovieModel attachExtrasToMovieModelFromCursor(Cursor cursor, MovieModel model) {

        if (cursor == null || !cursor.moveToFirst() || cursor.getColumnCount() <= 1) {
            return model;
        }

        List<ReviewModel> reviews = new ArrayList<>();
        List<TrailerModel> trailers = new ArrayList<>();

        do {

            final String id = cursor.getString(cursor.getColumnIndex(ExtrasEntry.COLUMN_MOVIE_ID));

            if (id == null) {
                trailers.add(buildTrailerModelFromCursor(cursor));
            } else {
                reviews.add(buildReviewModelFromCursor(cursor, id));
            }

        } while(cursor.moveToNext());

        model.setReviews(reviews);
        model.setTrailers(trailers);

        return model;

    }

    private TrailerModel buildTrailerModelFromCursor(Cursor cursor) {

        final String title = cursor.getString(cursor.getColumnIndex(ExtrasEntry.COLUMN_TRAILER_TITLE));
        final String source = cursor.getString(cursor.getColumnIndex(ExtrasEntry.COLUMN_TRAILER_SOURCE));

        return new TrailerModel(title, source);

    }

    private ReviewModel buildReviewModelFromCursor(Cursor cursor, String id) {

        final String author = cursor.getString(cursor.getColumnIndex(ExtrasEntry.COLUMN_REVIEW_AUTHOR));
        final String content = cursor.getString(cursor.getColumnIndex(ExtrasEntry.COLUMN_REVIEW_CONTENT));
        final String url = cursor.getString(cursor.getColumnIndex(ExtrasEntry.COLUMN_REVIEW_URL));

        return new ReviewModel(id, author, content, url);

    }

}
