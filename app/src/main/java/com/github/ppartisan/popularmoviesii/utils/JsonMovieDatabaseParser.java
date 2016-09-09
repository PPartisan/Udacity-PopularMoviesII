package com.github.ppartisan.popularmoviesii.utils;

import android.util.Log;

import com.github.ppartisan.popularmoviesii.model.MovieModel;
import com.github.ppartisan.popularmoviesii.model.ReviewModel;
import com.github.ppartisan.popularmoviesii.model.TrailerModel;
import com.github.ppartisan.popularmoviesii.utils.FetchJsonMovieDataUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class JsonMovieDatabaseParser {

    private static final String KEY_RESULTS = "results";
    private static final String KEY_IMAGE = "poster_path";
    private static final String KEY_SYNOPSIS = "overview";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_ID = "id";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_AVERAGE_VOTE = "vote_average";

    private static final String KEY_REVIEW_RESULTS = "results";
    private static final String KEY_REVIEW_ID = "id";
    private static final String KEY_REVIEW_AUTHOR = "author";
    private static final String KEY_REVIEW_CONTENT = "content";
    private static final String KEY_REVIEW_URL = "url";

    private static final String KEY_TRAILER_YOUTUBE = "youtube";
    private static final String KEY_TRAILER_NAME = "name";
    private static final String KEY_TRAILER_SOURCE = "source";

    public List<MovieModel> getMovieModelsFromJson(String jsonString) throws JSONException {

        final JSONObject json = new JSONObject(jsonString);
        final JSONArray results = json.getJSONArray(KEY_RESULTS);

        final List<MovieModel> models = new ArrayList<>(results.length());

        for (int i = 0; i < results.length(); i++) {

            JSONObject item = results.getJSONObject(i);
            final String imageUrl = item.getString(KEY_IMAGE);
            final String synopsis = item.getString(KEY_SYNOPSIS);
            final String id = item.getString(KEY_ID);
            final String releaseDate = item.getString(KEY_RELEASE_DATE);
            final String title = item.getString(KEY_ORIGINAL_TITLE);
            final double averageVote = item.getDouble(KEY_AVERAGE_VOTE);

            MovieModel model = new MovieModel.Builder()
                    .id(id)
                    .title(title)
                    .imageUrl(FetchJsonMovieDataUtils.getImageUrlFromRelativePath(imageUrl))
                    .synopsis(synopsis)
                    .releaseDate(FetchJsonMovieDataUtils.getDateInReadableFormat(releaseDate))
                    .averageVote(averageVote)
                    .build();

            models.add(model);

        }

        return models;

    }

    public List<ReviewModel> getReviewModelListFromJson(String reviewString) throws JSONException {

        final JSONObject reviewJson = new JSONObject(reviewString);
        final JSONArray results = reviewJson.getJSONArray(KEY_REVIEW_RESULTS);

        final List<ReviewModel> reviews = new ArrayList<>(results.length());

        for (int i = 0; i < results.length(); i++) {
            final JSONObject item = results.getJSONObject(i);
            final String id = item.getString(KEY_REVIEW_ID);
            final String author = item.getString(KEY_REVIEW_AUTHOR);
            final String content = item.getString(KEY_REVIEW_CONTENT);
            final String url = item.getString(KEY_REVIEW_URL);
            reviews.add(new ReviewModel(id, author, content, url));
        }

        return reviews;
    }

    public List<TrailerModel> getTrailerModelListFromJson(String trailerString) throws JSONException {

        final JSONObject trailerJson = new JSONObject(trailerString);
        final JSONArray youtube = trailerJson.getJSONArray(KEY_TRAILER_YOUTUBE);

        final List<TrailerModel> trailers = new ArrayList<>(youtube.length());

        for (int i = 0; i < youtube.length(); i++) {
            final JSONObject item = youtube.getJSONObject(i);
            final String name = item.getString(KEY_TRAILER_NAME);
            final String source = item.getString(KEY_TRAILER_SOURCE);
            trailers.add(new TrailerModel(name, source));
        }

        return trailers;
    }

}
