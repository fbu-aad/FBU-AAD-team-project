package com.example.una;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.una.models.Broadcast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.like.LikeButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.una.utils.BroadcastViewsUtil.setLikeButtonAndText;
import static com.example.una.utils.BroadcastViewsUtil.setNumCommentsText;
import static com.example.una.utils.BroadcastViewsUtil.setOnLikeListener;
import static com.example.una.utils.BroadcastViewsUtil.setProfileImagePlaceholder;

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
    @BindView(R.id.profileImage) ImageView ivProfile;

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

        // set profile image placeholder
        setProfileImagePlaceholder(this, ivProfile);

        // set like button depending on whether broadcast is already liked and number of likes text view
        setLikeButtonAndText(client, btnLike, tvNumLikes, broadcastId);

        // set number of comments text view
        setNumCommentsText(client, tvNumComments, broadcastId);

        // on like listener for like button
        setOnLikeListener(client, btnLike, broadcastId);

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
