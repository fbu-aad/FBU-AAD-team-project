package com.example.una;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.Viewholders.BroadcastViewHolder;
import com.example.una.models.Broadcast;

import org.parceler.Parcels;

import java.util.ArrayList;

import static com.example.una.utils.BroadcastViewsUtil.setLikeButtonAndText;
import static com.example.una.utils.BroadcastViewsUtil.setNumCommentsText;
import static com.example.una.utils.BroadcastViewsUtil.setOnLikeListener;
import static com.example.una.utils.BroadcastViewsUtil.setProfileImagePlaceholder;

public class BroadcastsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<Broadcast> broadcasts;
    FirestoreClient client;

    public BroadcastsAdapter(ArrayList<Broadcast> broadcasts) {
        this.broadcasts = broadcasts;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        client = new FirestoreClient();
        LayoutInflater inflater = LayoutInflater.from(context);
        View ivBroadcast = inflater.inflate(R.layout.broadcast_layout, parent, false);
        return new BroadcastViewHolder(ivBroadcast, broadcasts, context);
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
            switch (broadcast.getType()) {
                case Broadcast.DONATION:
                    broadcastViewHolder.getBroadcastCharityName().setText(broadcast.getUserName());
                    break;
                case Broadcast.NEW_CHALLENGE:
                case Broadcast.CHALLENGE_DONATION:
                    broadcastViewHolder.getBroadcastCharityName().setText(broadcast.getChallengeName());
                    break;
                case Broadcast.POST:
                    broadcastViewHolder.getBroadcastCharityName().setText(broadcast.getCharityName());
                    break;
                default:
                    broadcastViewHolder.getBroadcastCharityName().setText("null");
                    break;
            }

            broadcastViewHolder.getBroadcastCharityMessage().setText(broadcast.getMessage());

            // set profile image placeholder
            setProfileImagePlaceholder(context, broadcastViewHolder.getBroadcastProfileImage());

            // set like button depending on whether broadcast is already liked and number of likes text view
            setLikeButtonAndText(client, broadcastViewHolder.getBroadcastLikeButton(),
                    broadcastViewHolder.getBroadcastNumLikes(), broadcast);

            // set number of comments text view
            setNumCommentsText(broadcastViewHolder.getBroadcastNumComments(), broadcast);

            // on like listener for like button
            setOnLikeListener(client, broadcastViewHolder.getBroadcastLikeButton(), broadcast.getUid());

            // on click listener for comment button
            broadcastViewHolder.getBroadcastCommentButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // open detail view of broadcast
                    Intent detailBroadcast = new Intent(context, BroadcastDetailsActivity.class);
                    detailBroadcast.putExtra(Broadcast.class.getSimpleName(), Parcels.wrap(broadcast));
                    context.startActivity(detailBroadcast);
                }
            });
        }
    }

    // getItemId and getItemViewType ensure that the recycler view items are recycled properly
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
