package com.example.una;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.una.adapters.CommentAdapter;
import com.example.una.models.Broadcast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.like.LikeButton;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.una.utils.BroadcastViewsUtil.setCharityNameTextView;
import static com.example.una.utils.BroadcastViewsUtil.setLikeButtonAndText;
import static com.example.una.utils.BroadcastViewsUtil.setNumCommentsText;
import static com.example.una.utils.BroadcastViewsUtil.setOnLikeListener;
import static com.example.una.utils.BroadcastViewsUtil.setProfileImagePlaceholder;
import static com.example.una.utils.BroadcastViewsUtil.updateNumberText;

public class BroadcastDetailsActivity extends AppCompatActivity {

    FirestoreClient client;
    Broadcast broadcast;
    @BindView(R.id.btnLike)
    LikeButton btnLike;
    @BindView(R.id.ibComment) ImageButton ibComment;
    @BindView(R.id.btnComment) Button btnComment;
    @BindView(R.id.tvNumLikes) TextView tvNumLikes;
    @BindView(R.id.tvNumComments) TextView tvNumComments;
    @BindView(R.id.charityName) TextView tvCharityName;
    @BindView(R.id.message) TextView tvMessage;
    @BindView(R.id.etComment) TextView etComment;
    @BindView(R.id.profileImage) ImageView ivProfile;

    @BindView(R.id.rvComments) RecyclerView rvComments;
    ArrayList<String> comments;
    CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_details);
        client = new FirestoreClient();
        ButterKnife.bind(this);

        broadcast = Parcels.unwrap(getIntent().getParcelableExtra(Broadcast.class.getSimpleName()));
        String broadcastId = broadcast.getUid();

        // set views
        setCharityNameTextView(tvCharityName, broadcast);
        tvMessage.setText(broadcast.getMessage());
        comments = broadcast.getComments();
        if (comments == null) {
            comments = new ArrayList<>();
        }

        adapter = new CommentAdapter(comments);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setAdapter(adapter);

        // set profile image placeholder
        setProfileImagePlaceholder(this, ivProfile);

        // set like button depending on whether broadcast is already liked and number of likes text view
        setLikeButtonAndText(client, btnLike, tvNumLikes, broadcast);

        // set number of comments text view
        setNumCommentsText(tvNumComments, broadcast);

        // on like listener for like button
        setOnLikeListener(client, tvNumLikes, btnLike, broadcast);

        // comment on a post
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String comment = etComment.getText().toString();

                if (!comment.isEmpty() && !comment.replaceAll(" ", "").isEmpty()) {
                    client.commentOnBroadcast(broadcastId, comment, new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            updateNumberText(tvNumComments, true);
                            String name = client.getCurrentUser().getDisplayName();
                            if (name == null || name.isEmpty()) {
                                name = client.getCurrentUser().getEmail();
                            }
                            comments.add(name + ": " + comment);
                            adapter.notifyDataSetChanged();
                            etComment.setText("");
                        }
                    });
                }
            }
        });
    }
}
