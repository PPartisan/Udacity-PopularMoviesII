package com.github.ppartisan.popularmoviesii.adapter;

import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.ppartisan.popularmoviesii.R;
import com.github.ppartisan.popularmoviesii.model.ReviewModel;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<ReviewModel> mReviews;
    private final OnReviewAdapterItemClickListener mListener;

    public ReviewAdapter(List<ReviewModel> reviews, OnReviewAdapterItemClickListener listener) {
        this.mReviews = reviews;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final ReviewModel review = mReviews.get(position);

        holder.author.setText(review.author);
        holder.content.setText(review.content);

        ReviewAdapterClickListener clickListener = new ReviewAdapterClickListener(
                mListener, review.url, holder.getAdapterPosition()
        );

        holder.link.setOnClickListener(clickListener);
        holder.expand.setOnClickListener(clickListener);

    }

    public void setReviews(List<ReviewModel> mReviews) {
        this.mReviews = mReviews;
        notifyDataSetChanged();
    }

    public List<ReviewModel> getReviews() {
        return mReviews;
    }

    @Override
    public int getItemCount() {
        if (mReviews == null) {
            return 0;
        }
        return mReviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView author, content;
        ImageButton link, expand;

        public ViewHolder(final View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.rr_author);
            content = (TextView) itemView.findViewById(R.id.rr_content);

            link = (ImageButton) itemView.findViewById(R.id.rr_link_btn);
            expand = (ImageButton) itemView.findViewById(R.id.rr_open_btn);

            final int accent = ContextCompat.getColor(itemView.getContext(), R.color.accent);
            link.setColorFilter(accent, PorterDuff.Mode.MULTIPLY);
            expand.setColorFilter(accent, PorterDuff.Mode.MULTIPLY);
        }

    }

    private static class ReviewAdapterClickListener implements View.OnClickListener {

        private final OnReviewAdapterItemClickListener mCallback;
        private final String mTargetUrl;
        private final int position;

        private ReviewAdapterClickListener(OnReviewAdapterItemClickListener callback, String targetUrl, int position) {
            mCallback = callback;
            mTargetUrl = targetUrl;
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rr_link_btn:
                mCallback.onAdapterItemLinkClick(mTargetUrl);
                    break;
                case R.id.rr_open_btn:
                    mCallback.onAdapterItemExpandClick(position);
                    break;
            }
        }

    }

    public interface OnReviewAdapterItemClickListener {
        void onAdapterItemLinkClick(String targetUrl);
        void onAdapterItemExpandClick(int position);
    }

}
