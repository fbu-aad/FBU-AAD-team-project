package com.example.una;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.service.autofill.SaveCallback;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.una.models.Broadcast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BroadcastDetailsActivity extends AppCompatActivity {

    FirestoreClient client;
    Broadcast broadcast;
    @BindView(R.id.btnLike) LikeButton btnLike;
    @BindView(R.id.ibComment) ImageButton ibComment;
    @BindView(R.id.btnComment) Button btnComment;
    @BindView(R.id.tvNumLikes) TextView tvNumLikes;
    @BindView(R.id.tvNumComments) TextView tvNumComments;
    @BindView(R.id.charityName) TextView tvCharityName;
    @BindView(R.id.message) TextView tvMessage;
    @BindView(R.id.tvComments) TextView tvComments;
    @BindView(R.id.etComment) TextView etComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_details);
        client = new FirestoreClient(this);
        ButterKnife.bind(this);


        String broadcastId = getIntent().getStringExtra("id");
        client.getBroadcast(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                broadcast = new Broadcast(documentSnapshot.getData(), broadcastId);

                // set views
                tvCharityName.setText(broadcast.getCharityName());
                tvMessage.setText(broadcast.getMessage());
                ArrayList<String> comments = broadcast.getComments();

                // set comments text view
                setTvComments(comments, tvComments);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }, broadcastId);

        // set like button depending on whether broadcast is already liked and number of likes text view
        setLikeButtonAndText(btnLike, tvNumLikes, broadcastId);

        // set number of comments text view
        setNumCommentsText(tvNumComments, broadcastId);

        // on like listener for like button
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

        // comment on a post
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String comment = etComment.getText().toString();

                if (!comment.isEmpty() && !comment.replaceAll(" ", "").isEmpty()) {
                    client.commentOnBroadcast(broadcastId, comment);
                }

                etComment.setText("");
            }
        });
    }

    private void setLikeButtonAndText(LikeButton likeButton, TextView tvNumLikes, String broadcastId) {
        String userId = client.getCurrentUser().getUid();
        client.getBroadcast(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> usersLiked = (ArrayList<String>) documentSnapshot.get("liked_by");
                if (usersLiked != null) {
                    setNumberText(usersLiked, tvNumLikes);
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

            }
        }, broadcastId);
    }

    private void setNumCommentsText(TextView tvNumComments, String broadcastId) {
        client.getBroadcast(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> comments = (ArrayList<String>) documentSnapshot.get("comments");
                if (comments != null) {
                    setNumberText(comments, tvNumComments);
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }, broadcastId);
    }

    private void setNumberText(ArrayList<String> users, TextView tvNum) {
        if (users != null && users.size() != 0) {
            tvNum.setVisibility(View.VISIBLE);
            tvNum.setText(Integer.toString(users.size()));
        }
    }

    private void setTvComments(ArrayList<String> comments, TextView tvComments) {
        if (comments != null) {
            // set comments text view
            tvComments.setVisibility(View.VISIBLE);

            String text = "";
            for (int i = 0; i < comments.size()-1; i++) {
                text = text + comments.get(i) + "\n";
            }
            text = text + comments.get(comments.size()-1);
            tvComments.setText(text);
        }
    }
}
