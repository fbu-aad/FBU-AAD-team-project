package com.example.una;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.una.models.Challenge;
import com.google.android.material.chip.Chip;
import org.parceler.Parcels;

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

        setJoinBtn(btnJoin, btnDonate, challenge);

        handleClickJoinBtn(btnJoin, btnDonate, challenge, context);
    }

    private void setChip(String sChip, Chip chip) {
        if (sChip != null) {
            chip.setText(sChip);
        } else {
            chip.setVisibility(GONE);
        }
    }
}
