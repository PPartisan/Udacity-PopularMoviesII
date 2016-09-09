package com.github.ppartisan.popularmoviesii.model;

import android.os.Parcel;
import android.os.Parcelable;

public final class TrailerModel implements Parcelable {

    private static final String TAG = TrailerModel.class.getSimpleName();
    public final String title, source;

    public TrailerModel(String title, String source) {
        this.title = title;
        this.source = source;
    }

    protected TrailerModel(Parcel in) {
        title = in.readString();
        source = in.readString();
    }

    public static final Creator<TrailerModel> CREATOR = new Creator<TrailerModel>() {
        @Override
        public TrailerModel createFromParcel(Parcel in) {
            return new TrailerModel(in);
        }

        @Override
        public TrailerModel[] newArray(int size) {
            return new TrailerModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(source);
    }

    @Override
    public String toString() {
        return TAG + ".title: " + this.title + '\n' +
                TAG + ".source: " + this.source;
    }

}
