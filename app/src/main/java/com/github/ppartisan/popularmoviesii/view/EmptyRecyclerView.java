package com.github.ppartisan.popularmoviesii.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public final class EmptyRecyclerView extends RecyclerView {

    private View mEmptyView;
    private final AdapterDataObserver mEmptyObserver = new EmptyRecyclerViewAdapterDataObserver();

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mEmptyObserver);
        }
        mEmptyObserver.onChanged();
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
    }

    private final class EmptyRecyclerViewAdapterDataObserver extends AdapterDataObserver {

        @Override
        public void onChanged() {
            RecyclerView.Adapter<?> adapter = getAdapter();
            if (adapter != null && mEmptyView != null) {
                if (adapter.getItemCount() == 0) {
                    mEmptyView.setVisibility(VISIBLE);
                    EmptyRecyclerView.this.setVisibility(GONE);
                } else {
                    mEmptyView.setVisibility(GONE);
                    EmptyRecyclerView.this.setVisibility(VISIBLE);
                }
            }
        }
    }

}
