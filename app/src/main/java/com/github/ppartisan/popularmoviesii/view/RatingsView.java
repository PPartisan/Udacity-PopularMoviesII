package com.github.ppartisan.popularmoviesii.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import android.util.Log;
import android.view.View;

import com.github.ppartisan.popularmoviesii.R;

public class RatingsView extends View {

    private float score = 0;

    private Paint mFillPaint;

    public RatingsView(Context context) {
        super(context);
        init();
    }

    public RatingsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RatingsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RatingsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        final int accentColor = ContextCompat.getColor(getContext(), R.color.accent);

        mFillPaint = new Paint();
        mFillPaint.setColor(accentColor);
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setAntiAlias(true);

    }

    public void setScore(double score) {
        if (score < 0 || score > 10) {
            throw new IllegalArgumentException("Score must range from 0 to 10");
        }
        this.score = (float) score;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int height = resolveSize(widthSize/20, heightMeasureSpec);

        setMeasuredDimension(widthSize, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        final float barMaskWidth = (getMeasuredWidth()*(score/10));
        canvas.clipRect(barMaskWidth,0,getMeasuredWidth(),getMeasuredHeight(), Region.Op.DIFFERENCE);

        final int percentileWidth = getMeasuredWidth()/10;

        final float y = (getMeasuredHeight()/2);
        final float radius = (percentileWidth/4);

        int count = 0;

        while (count < 10) {
            final float x = ((count*percentileWidth)+(percentileWidth/2));
            canvas.drawCircle(x, y, radius, mFillPaint);
            count++;
        }

    }
}
