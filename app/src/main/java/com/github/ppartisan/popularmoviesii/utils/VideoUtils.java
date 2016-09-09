package com.github.ppartisan.popularmoviesii.utils;

import android.net.Uri;

public final class VideoUtils {

    private static final String THUMBNAIL_BASE_URL = "http://img.youtube.com/vi/";
    private static final String THUMBNAIL_SUFFIX = "0.jpg";

    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    private VideoUtils() { throw new AssertionError(); }

    public static String getThumbnailUrlFromId(String videoId) {
        return Uri.parse(THUMBNAIL_BASE_URL).buildUpon()
                .appendPath(videoId)
                .appendPath(THUMBNAIL_SUFFIX).build().toString();
    }

    public static String getYouTubeUrlFromId(String videoId) {
        return YOUTUBE_BASE_URL + videoId;
    }

}
