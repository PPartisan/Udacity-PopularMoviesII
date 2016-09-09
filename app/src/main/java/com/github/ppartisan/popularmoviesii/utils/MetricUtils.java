package com.github.ppartisan.popularmoviesii.utils;

import android.view.View;

public final class MetricUtils {

    private MetricUtils() { throw new AssertionError(); }

    public static int getColumnCountForViewWidth(View view) {
        final int columnWidth = Integer.parseInt(FetchJsonMovieDataUtils.IMAGE_WIDTH.substring(1));
        return (int)Math.ceil(view.getWidth() / columnWidth);
    }

}
