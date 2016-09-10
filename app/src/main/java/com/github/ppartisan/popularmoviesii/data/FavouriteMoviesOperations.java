package com.github.ppartisan.popularmoviesii.data;


import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import com.github.ppartisan.popularmoviesii.model.MovieModel;
import com.github.ppartisan.popularmoviesii.data.FavouriteMoviesContract.MoviesEntry;
import com.github.ppartisan.popularmoviesii.data.FavouriteMoviesContract.ExtrasEntry;
import com.github.ppartisan.popularmoviesii.model.ReviewModel;
import com.github.ppartisan.popularmoviesii.model.TrailerModel;


import java.util.ArrayList;
import java.util.List;

public interface FavouriteMoviesOperations {

    ContentProviderResult[] insertMovieModel(MovieModel model) throws RemoteException, OperationApplicationException;
    ContentProviderResult[] deleteMovieModel(MovieModel model) throws RemoteException, OperationApplicationException;

    final class MovieModelOperations implements FavouriteMoviesOperations{

        private final ContentResolver mContentResolver;

        public MovieModelOperations(ContentResolver resolver) {
            mContentResolver = resolver;
        }

        @Override
        public ContentProviderResult[] insertMovieModel(MovieModel model) throws RemoteException, OperationApplicationException {

            final int listSize = 1 + model.getTrailers().size() + model.getReviews().size();
            final ArrayList<ContentProviderOperation> ops = new ArrayList<>(listSize);

            ContentProviderOperation movieOp =
                    ContentProviderOperation.newInsert(MoviesEntry.CONTENT_URI)
                            .withValue(MoviesEntry.COLUMN_MOVIE_ID, model.id)
                            .withValue(MoviesEntry.COLUMN_TITLE, model.title)
                            .withValue(MoviesEntry.COLUMN_RELEASE_DATE, model.releaseDate)
                            .withValue(MoviesEntry.COLUMN_IMAGE_URL, model.imageUrl)
                            .withValue(MoviesEntry.COLUMN_SYNOPSIS, model.synopsis)
                            .withValue(MoviesEntry.COLUMN_AVERAGE_VOTE, model.averageVote)
                            .build();

            ops.add(movieOp);

            final List<ReviewModel> reviews = model.getReviews();

            for (ReviewModel review : reviews) {

                ContentProviderOperation reviewOp =
                        ContentProviderOperation.newInsert(ExtrasEntry.CONTENT_URI)
                                .withValue(ExtrasEntry.COLUMN_MOVIE_ID, model.id)
                                .withValue(ExtrasEntry.COLUMN_REVIEW_ID, review.id)
                                .withValue(ExtrasEntry.COLUMN_REVIEW_AUTHOR, review.author)
                                .withValue(ExtrasEntry.COLUMN_REVIEW_CONTENT, review.content)
                                .withValue(ExtrasEntry.COLUMN_REVIEW_URL, review.url)
                                .build();

                ops.add(reviewOp);

            }

            final List<TrailerModel> trailers = model.getTrailers();

            for (TrailerModel trailer : trailers) {

                ContentProviderOperation trailerOp =
                        ContentProviderOperation.newInsert(ExtrasEntry.CONTENT_URI)
                                .withValue(ExtrasEntry.COLUMN_MOVIE_ID, model.id)
                                .withValue(ExtrasEntry.COLUMN_TRAILER_TITLE, trailer.title)
                                .withValue(ExtrasEntry.COLUMN_TRAILER_SOURCE, trailer.source)
                                .build();

                ops.add(trailerOp);

            }

            return mContentResolver.applyBatch(FavouriteMoviesContract.CONTENT_AUTHORITY, ops);
        }

        @Override
        public ContentProviderResult[] deleteMovieModel(MovieModel model) throws RemoteException, OperationApplicationException {

            final ArrayList<ContentProviderOperation> ops = new ArrayList<>(2);

            final String[] whereArgs = new String[] {model.id };

            ContentProviderOperation movieOp =
                    ContentProviderOperation.newDelete(MoviesEntry.CONTENT_URI)
                            .withSelection(MoviesEntry.COLUMN_MOVIE_ID + "=?", whereArgs)
                            .build();
            ops.add(movieOp);

            ContentProviderOperation extrasOp =
                    ContentProviderOperation.newDelete(ExtrasEntry.CONTENT_URI)
                            .withSelection(ExtrasEntry.COLUMN_MOVIE_ID + "=?", whereArgs)
                            .build();

            ops.add(extrasOp);

            return mContentResolver.applyBatch(FavouriteMoviesContract.CONTENT_AUTHORITY, ops);

        }

    }

}
