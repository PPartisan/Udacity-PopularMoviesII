package com.github.ppartisan.popularmoviesii.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

public final class MovieModel implements Parcelable {

    public final String id, title, releaseDate, imageUrl, synopsis;
    public final double averageVote;

    private List<ReviewModel> reviews;
    private List<TrailerModel> trailers;

    private MovieModel(String id, String title, String releaseDate, String imageUrl, double averageVote, String synopsis) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.imageUrl = imageUrl;
        this.averageVote = averageVote;
        this.synopsis = synopsis;
    }

    protected MovieModel(Parcel in) {
        id = in.readString();
        title = in.readString();
        releaseDate = in.readString();
        imageUrl = in.readString();
        synopsis = in.readString();
        averageVote = in.readDouble();
        reviews = in.createTypedArrayList(ReviewModel.CREATOR);
        trailers = in.createTypedArrayList(TrailerModel.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(imageUrl);
        dest.writeString(synopsis);
        dest.writeDouble(averageVote);
        dest.writeTypedList(reviews);
        dest.writeTypedList(trailers);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieModel> CREATOR = new Creator<MovieModel>() {
        @Override
        public MovieModel createFromParcel(Parcel in) {
            return new MovieModel(in);
        }

        @Override
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };

    public List<ReviewModel> getReviews() {
        return reviews;
    }

    public List<TrailerModel> getTrailers() {
        return trailers;
    }

    public void setReviews(List<ReviewModel> reviews) {
        this.reviews = reviews;
    }

    public void setTrailers(List<TrailerModel> trailers) {
        this.trailers = trailers;
    }

    @Override
    public String toString() {

        final String rev = (reviews == null) ? "" : Arrays.toString(reviews.toArray());
        final String tra = (trailers == null) ? "" : Arrays.toString(trailers.toArray());

        return "Title: " + title +
                "\nRelease Date: " + releaseDate +
                "\nImage Url: " + imageUrl +
                "\nAve. Vote: " + averageVote +
                "\nSynopsis: " + synopsis +
                "\nReview: " + rev +
                "\nTrailer: " + tra;
    }

    public final static class Builder {

        private String id, title, releaseDate, imageUrl, synopsis;
        private double averageVote;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder releaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder synopsis(String synopsis) {
            this.synopsis = synopsis;
            return this;
        }

        public Builder averageVote(double averageVote) {
            this.averageVote = averageVote;
            return this;
        }

        public MovieModel build() {
            return new MovieModel(id, title, releaseDate, imageUrl, averageVote, synopsis);
        }

        public String getId() {
            return this.id;
        }

    }

}
