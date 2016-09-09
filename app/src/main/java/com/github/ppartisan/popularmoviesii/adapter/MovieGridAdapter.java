package com.github.ppartisan.popularmoviesii.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ppartisan.popularmoviesii.R;
import com.github.ppartisan.popularmoviesii.model.MovieModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.ViewHolder> {

    private List<MovieModel> movieModels;
    private final OnMovieAdapterItemClickListener mCallback;

    public MovieGridAdapter(OnMovieAdapterItemClickListener callback) {
        this(callback, null);
    }

    public MovieGridAdapter(OnMovieAdapterItemClickListener callback, List<MovieModel> movieModels) {
        this.movieModels = movieModels;
        mCallback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final MovieModel model = movieModels.get(position);

        final String imageUrl = model.imageUrl;
        final String title = model.title;

        if (imageUrl.endsWith("null")) {
            holder.text.setText(title);
            holder.text.setVisibility(View.VISIBLE);
            Picasso.with(holder.itemView.getContext())
                    .load(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .into(holder.image);
        } else {
            holder.text.setText(null);
            holder.text.setVisibility(View.GONE);
            Picasso.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .fit()
                    .into(holder.image);
        }

        holder.itemView.setOnClickListener(new MovieGridAdapterClickListener(mCallback, model));

    }

    @Override
    public int getItemCount() {
        if (movieModels == null) {
            return 0;
        }
        return movieModels.size();
    }

    public void setMovieModels(List<MovieModel> movieModels) {
        this.movieModels = movieModels;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.fm_grid_item_thumbnail);
            text = (TextView) itemView.findViewById(R.id.fm_grid_item_text);
        }

    }

    private static class MovieGridAdapterClickListener implements View.OnClickListener {

        private final OnMovieAdapterItemClickListener mCallback;
        private final MovieModel mModel;

        private MovieGridAdapterClickListener(OnMovieAdapterItemClickListener callback, MovieModel model) {
            mCallback = callback;
            mModel = model;
        }

        @Override
        public void onClick(View view) {
            mCallback.onMovieAdapterItemClick(mModel);
        }

    }

    public interface OnMovieAdapterItemClickListener {
        void onMovieAdapterItemClick(MovieModel model);
    }

}
