package com.github.ppartisan.popularmoviesii.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ppartisan.popularmoviesii.R;
import com.github.ppartisan.popularmoviesii.model.TrailerModel;
import com.github.ppartisan.popularmoviesii.utils.VideoUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private List<TrailerModel> trailers;

    public TrailerAdapter(List<TrailerModel> trailers) {
        this.trailers = trailers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TrailerModel trailer = trailers.get(position);

        holder.title.setText(trailer.title);
        Picasso.with(holder.itemView.getContext())
                .load(VideoUtils.getThumbnailUrlFromId(trailer.source))
                .placeholder(R.drawable.trailer_placeholder)
                .error(R.drawable.trailer_error)
                .into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchVideoIntent = new Intent(Intent.ACTION_VIEW);
                launchVideoIntent.setData(Uri.parse(VideoUtils.getYouTubeUrlFromId(trailer.source)));
                view.getContext().startActivity(launchVideoIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (trailers == null) {
            return 0;
        }
        return trailers.size();
    }

    public void setTrailers(List<TrailerModel> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.trailer_row_thumbnail);
            title = (TextView) itemView.findViewById(R.id.trailer_row_title);
        }
    }

}
