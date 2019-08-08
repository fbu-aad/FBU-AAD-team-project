package com.example.una.Viewholders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.BroadcastDetailsActivity;
import com.example.una.R;
import com.example.una.models.Broadcast;
import com.like.LikeButton;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BroadcastViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.profileImage) ImageView profileImage;
    @BindView(R.id.charityName) TextView charityName;
    @BindView(R.id.message) TextView charityMessage;
    @BindView(R.id.btnLike) LikeButton btnLike;
    @BindView(R.id.tvNumLikes) TextView tvNumLikes;
    @BindView(R.id.tvNumComments) TextView tvNumComments;
    @BindView(R.id.ibComment) ImageButton ibComment;
    ArrayList<Broadcast> broadcasts;

    public BroadcastViewHolder(@NonNull View itemView, ArrayList<Broadcast> broadcasts, Context context) {
        super(itemView);
        this.broadcasts = broadcasts;
        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get item position
                int position = getAdapterPosition();
                // make sure the position is valid, i.e. actually exists in the view
                if (position != RecyclerView.NO_POSITION) {
                    Broadcast broadcast = broadcasts.get(position);
                    // open detail view of broadcast
                    Intent detailBroadcast = new Intent(context, BroadcastDetailsActivity.class);
                    detailBroadcast.putExtra(Broadcast.class.getSimpleName(), Parcels.wrap(broadcast));
                    context.startActivity(detailBroadcast);
                }
            }
        });
    }

    public ImageView getBroadcastProfileImage() {
        return profileImage;
    }

    public TextView getBroadcastCharityName() {
        return charityName;
    }

    public TextView getBroadcastCharityMessage() {
        return charityMessage;
    }

    public LikeButton getBroadcastLikeButton() { return btnLike; }

    public TextView getBroadcastNumLikes() { return tvNumLikes; }

    public TextView getBroadcastNumComments() { return tvNumComments; }

    public ImageButton getBroadcastCommentButton() { return ibComment; }
}
