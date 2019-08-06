package com.example.una.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.una.FirestoreClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

public class BroadcastViewsUtil {

    public static void setLikeButtonAndText(FirestoreClient client, LikeButton likeButton, TextView tvNumLikes, String broadcastId) {
        String userId = client.getCurrentUser().getUid();
        client.getBroadcast(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> usersLiked = (ArrayList<String>) documentSnapshot.get("liked_by");
                setNumberText(usersLiked, tvNumLikes);
                if (usersLiked != null) {
                    if (usersLiked.contains(userId)) {
                        likeButton.setLiked(true);
                    }
                } else {
                    likeButton.setLiked(false);
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        }, broadcastId);
    }

    public static void setNumCommentsText(FirestoreClient client, TextView tvNumComments, String broadcastId) {
        client.getBroadcast(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> comments = (ArrayList<String>) documentSnapshot.get("comments");
                setNumberText(comments, tvNumComments);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }, broadcastId);
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
