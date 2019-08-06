package com.example.una.Viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.R;
import com.like.LikeButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BroadcastViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.profileImage) ImageView profileImage;
    @BindView(R.id.charityName) TextView charityName;
    @BindView(R.id.message) TextView charityMessage;
    @BindView(R.id.btnLike) LikeButton btnLike;
    @BindView(R.id.tvNumLikes) TextView tvNumLikes;

    public BroadcastViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
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
}
