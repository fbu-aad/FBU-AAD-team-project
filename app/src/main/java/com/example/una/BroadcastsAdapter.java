package com.example.una;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.una.Viewholders.BroadcastViewHolder;
import com.example.una.models.Broadcast;

import java.util.ArrayList;

public class BroadcastsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    Context context;

    ArrayList<Broadcast> broadcasts;

    public BroadcastsAdapter(ArrayList<Broadcast> broadcasts) {
        this.broadcasts = broadcasts;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View ivBroadcast = inflater.inflate(R.layout.broadcast_layout, parent, false);
        return new BroadcastViewHolder(ivBroadcast);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BroadcastViewHolder broadcastViewHolder = (BroadcastViewHolder) holder;
        configureBroadcastViewHolder(broadcastViewHolder, broadcasts.get(position));
    }

    @Override
    public int getItemCount() {
        return broadcasts.size();
    }

    private void configureBroadcastViewHolder(BroadcastViewHolder broadcastViewHolder, Broadcast broadcast) {
        if (broadcast != null) {
            broadcastViewHolder.getBroadcastCharityName().setText(broadcast.getCharityName());
            broadcastViewHolder.getBroadcastCharityMessage().setText(broadcast.getMessage());

            // set profile image placeholder
            Glide.with(context)
                    .load("https://picsum.photos" + "/48")
                    .apply(RequestOptions.circleCropTransform())
                    .into(broadcastViewHolder.getBroadcastProfileImage());
        }
    }
}
