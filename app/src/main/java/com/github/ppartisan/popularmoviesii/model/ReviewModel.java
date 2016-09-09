package com.github.ppartisan.popularmoviesii.model;

import android.os.Parcel;
import android.os.Parcelable;

public final class ReviewModel implements Parcelable{

    private static final String TAG = ReviewModel.class.getSimpleName();
    public final String id, author, content, url;

    public ReviewModel(String id, String author, String content, String url) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    protected ReviewModel(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }

    public static final Creator<ReviewModel> CREATOR = new Creator<ReviewModel>() {
        @Override
        public ReviewModel createFromParcel(Parcel in) {
            return new ReviewModel(in);
        }

        @Override
        public ReviewModel[] newArray(int size) {
            return new ReviewModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(url);
    }

    @Override
    public String toString() {
        return TAG + ".id:" + this.id + '\n' +
                TAG + ".author: " + this.author + '\n' +
                TAG + ".content: " + this.content + '\n' +
                TAG + ".url: " + this.url;
    }
}
