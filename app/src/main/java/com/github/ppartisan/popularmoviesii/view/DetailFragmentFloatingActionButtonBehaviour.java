package com.github.ppartisan.popularmoviesii.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

@SuppressWarnings("unused")
public class DetailFragmentFloatingActionButtonBehaviour extends FloatingActionButton.Behavior {

    public DetailFragmentFloatingActionButtonBehaviour(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onNestedScroll(
            CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target,
            int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

        super.onNestedScroll(
                coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed
        );

        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            child.hide();
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.show();
        }

    }

}
