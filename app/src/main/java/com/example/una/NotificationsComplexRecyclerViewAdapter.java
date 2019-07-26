package com.example.una;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.una.Viewholders.BroadcastViewHolder;
import com.example.una.Viewholders.ChallengeViewHolder;
import com.example.una.adapters.StreaksComplexRecyclerViewAdapter;
import com.example.una.models.Broadcast;
import com.example.una.models.Challenge;

import java.util.List;

public class NotificationsComplexRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Object> items;
    private final int BROADCAST = 0, CHALLENGE = 1;
    private final String TAG = this.getClass().getSimpleName();
    Context context;

    FirestoreClient client;

    public NotificationsComplexRecyclerViewAdapter(List<Object> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        client = new FirestoreClient();

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == BROADCAST) {
            View ivBroadcast = inflater.inflate(R.layout.broadcast_layout, parent, false);
            viewHolder = new BroadcastViewHolder(ivBroadcast);
        } else {
            View ivChallenge = inflater.inflate(R.layout.challenge_item_layout, parent, false);
            viewHolder = new ChallengeViewHolder(ivChallenge);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == BROADCAST) {
            BroadcastViewHolder broadcastViewHolder = (BroadcastViewHolder) holder;
            configureBroadcastViewHolder(broadcastViewHolder, items.get(position));
        } else {
            ChallengeViewHolder challengeViewHolder = (ChallengeViewHolder) holder;
            StreaksComplexRecyclerViewAdapter.configureChallengeViewHolder(challengeViewHolder,
                    context, items.get(position), client);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Broadcast) {
            return BROADCAST;
        } else if (items.get(position) instanceof Challenge) {
            return CHALLENGE;
        } else {
            Log.e(TAG, "Invalid view type in notifications recycler view");
            return super.getItemViewType(position);
        }
    }

    private void configureBroadcastViewHolder(BroadcastViewHolder broadcastViewHolder, Object item) {
        Broadcast broadcast = (Broadcast) item;
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
