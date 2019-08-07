package com.example.una.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.una.FirestoreClient;
import com.example.una.models.Broadcast;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

public class BroadcastViewsUtil {

    public static void setCharityNameTextView(TextView tvCharityName, Broadcast broadcast) {
        switch (broadcast.getType()) {
            case Broadcast.DONATION:
                tvCharityName.setText(broadcast.getUserName());
                break;
            case Broadcast.NEW_CHALLENGE:
            case Broadcast.CHALLENGE_DONATION:
                tvCharityName.setText(broadcast.getChallengeName());
                break;
            case Broadcast.POST:
                tvCharityName.setText(broadcast.getCharityName());
                break;
            default:
                tvCharityName.setText("null");
                break;
        }
    }

    public static void setLikeButtonAndText(FirestoreClient client, LikeButton likeButton, TextView tvNumLikes, Broadcast broadcast) {
        String userId = client.getCurrentUser().getUid();
        ArrayList<String> usersLiked = broadcast.getLikes();
        setNumberText(usersLiked, tvNumLikes);
        if (usersLiked != null) {
            if (usersLiked.contains(userId)) {
                likeButton.setLiked(true);
            }
        } else {
            likeButton.setLiked(false);
        }
    }

    public static void setNumCommentsText(TextView tvNumComments, Broadcast broadcast) {
        ArrayList<String> comments = broadcast.getComments();
        setNumberText(comments, tvNumComments);
    }

    public static void setNumberText(ArrayList<String> users, TextView tvNum) {
        if (users != null && users.size() != 0) {
            tvNum.setVisibility(View.VISIBLE);
            tvNum.setText(Integer.toString(users.size()));
        }
    }

    public static void setOnLikeListener(FirestoreClient client, LikeButton btnLike, String broadcastId) {
        btnLike.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                client.likeBroadcast(broadcastId);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                client.unlikeBroadcast(broadcastId);
            }
        });
    }

    public static void setProfileImagePlaceholder(Context context, ImageView ivProfile) {
        Glide.with(context)
                .load("https://picsum.photos" + "/64")
                .apply(RequestOptions.circleCropTransform())
                .into(ivProfile);
    }
}
