package com.example.una.Viewholders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.ChallengeDetailsActivity;
import com.example.una.R;
import com.example.una.models.Challenge;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChallengeViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tvChallengeTitle) TextView tvChallengeTitle;
    @BindView(R.id.tvChallengeOwnerRecipientInfo) TextView tvChallengeOwnerRecipientInfo;
    @BindView(R.id.ivChallenge) ImageView ivChallenge;
    @BindView(R.id.tvNumParticipants) TextView tvNumParticipants;
    @BindView(R.id.btnJoin) ToggleButton btnJoin;
    @BindView(R.id.tvTimeLeft) TextView tvTimeLeft;
    @BindView(R.id.tvProgress) TextView tvProgress;
    @BindView(R.id.pbProgress) ProgressBar pbProgress;

    public ChallengeViewHolder(@NonNull View itemView, ArrayList<Object> challenges, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get item position
                int position = getAdapterPosition();
                // make sure the position is valid, i.e. actually exists in the view
                if (position != RecyclerView.NO_POSITION) {
                    Challenge challenge = (Challenge) challenges.get(position);
                    // open detail view of challenge
                    Intent detailChallenge = new Intent(context, ChallengeDetailsActivity.class);
                    Pair<View, String> p1 = Pair.create((View) ivChallenge, "image");
                    Pair<View, String> p2 = Pair.create((View) tvChallengeTitle, "title");

                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) context, p1, p2);

                    detailChallenge.putExtra(Challenge.class.getSimpleName(), Parcels.wrap(challenge));
                    context.startActivity(detailChallenge, options.toBundle());
                }
            }
        });
    }

    public TextView getTvChallengeTitle() {
        return tvChallengeTitle;
    }

    public TextView getTvChallengeOwnerRecipientInfo() {
        return tvChallengeOwnerRecipientInfo;
    }

    public ImageView getIvChallengeImage() { return ivChallenge; }

    public TextView getTvNumParticipants() { return tvNumParticipants; }

    public ToggleButton getBtnJoin() {
        return btnJoin;
    }

    public TextView getTvTimeLeft() {
        return tvTimeLeft;
    }

    public TextView getTvProgress() {
        return tvProgress;
    }

    public ProgressBar getPbProgress() {
        return pbProgress;
    }
}