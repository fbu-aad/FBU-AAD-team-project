package com.example.una;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.una.models.Challenge;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.DocumentSnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static com.example.una.utils.ChallengeViewsUtil.getStrNumParticipants;
import static com.example.una.utils.ChallengeViewsUtil.getStrProgress;
import static com.example.una.utils.ChallengeViewsUtil.getStrTimeLeft;
import static com.example.una.utils.ChallengeViewsUtil.handleClickJoinBtn;
import static com.example.una.utils.ChallengeViewsUtil.setJoinBtn;
import static com.example.una.utils.ChallengeViewsUtil.setPbProgress;
import static com.example.una.utils.ChallengeViewsUtil.setTvOwnerRecipientInfo;

public class ChallengeDetailsActivity extends AppCompatActivity {

    Challenge challenge;
    Context context;
    FirestoreClient client;
    static final int CHALLENGE_DONATION = 100;

    // the view objects
    @BindView(R.id.ivChallenge) ImageView ivChallenge;
    @BindView(R.id.tvChallengeTitle) TextView tvChallengeTitle;
    @BindView(R.id.tvChallengeOwnerRecipientInfo) TextView tvChallengeOwnerRecipientInfo;
    @BindView(R.id.tvNumParticipants) TextView tvNumParticipants;
    @BindView(R.id.btnJoin) ToggleButton btnJoin;
    @BindView(R.id.btnDonate) ToggleButton btnDonate;
    @BindView(R.id.tvTimeLeft) TextView tvTimeLeft;
    @BindView(R.id.pbProgress) ProgressBar pbProgress;
    @BindView(R.id.tvProgress) TextView tvProgress;
    @BindView(R.id.tvDescription) TextView tvDescription;
    @BindView(R.id.chipType) Chip chipType;
    @BindView(R.id.chipFrequency) Chip chipFrequency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_details);

        ButterKnife.bind(this);
        context = ChallengeDetailsActivity.this;

        client = new FirestoreClient();
        challenge = Parcels.unwrap(getIntent().getParcelableExtra(Challenge.class.getSimpleName()));

        // populate the views
        tvChallengeTitle.setText(challenge.getChallengeName());
        setTvOwnerRecipientInfo(tvChallengeOwnerRecipientInfo, challenge);

        tvNumParticipants.setText(getStrNumParticipants(challenge));

        tvTimeLeft.setText(getStrTimeLeft(challenge));

        tvProgress.setText(getStrProgress(challenge));

        setPbProgress(pbProgress, challenge);

        tvDescription.setText(challenge.getChallengeDescription());

        setChip(challenge.getChallengeFrequency(), chipFrequency);
        setChip(challenge.getChallengeType(), chipType);

        // set challenge image placeholder
        Glide.with(context)
                .load(challenge.getChallengeImageUrl())
                .into(ivChallenge);

        // toggle donate button depending on whether user has already donated through this challenge
        setDonateBtn(btnDonate, challenge);

        // toggle join button and enable donate button depending on user's acceptance of challenge
        setJoinBtn(btnJoin, btnDonate, challenge);

        handleClickJoinBtn(btnJoin, btnDonate, challenge, context);

        // set on click listener for donate button
        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDonate.setChecked(false);
                Intent donate = new Intent(context, ChallengeDonationActivity.class);
                // pass challenge id
                donate.putExtra("challenge_id", challenge.getUid());
                // start activity for result
                ((Activity) context).startActivityForResult(donate, CHALLENGE_DONATION);
            }
        });
    }

    private void setChip(String sChip, Chip chip) {
        if (sChip != null) {
            chip.setText(sChip);
        } else {
            chip.setVisibility(GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // check which request we are responding to
        if (requestCode == CHALLENGE_DONATION) {
            // make sure request was successful
            if (resultCode == RESULT_OK) {
                // if user donated, disable and toggle donate button
                btnDonate.setChecked(true);
                btnDonate.setEnabled(false);
            }
        }
    }

    private void setDonateBtn(ToggleButton btnDonate, Challenge challenge) {
        String userId = client.getCurrentUser().getUid();
        String challengeId = challenge.getUid();
        client.getChallengeParticipants(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> usersDonated = (ArrayList<String>) documentSnapshot.get("users_donated");
                if (usersDonated != null && usersDonated.contains(userId)) {
                    btnDonate.setEnabled(false);
                    btnDonate.setChecked(true);
                } else {
                    btnDonate.setEnabled(true);
                    btnDonate.setChecked(false);
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }, challengeId);
    }
}
