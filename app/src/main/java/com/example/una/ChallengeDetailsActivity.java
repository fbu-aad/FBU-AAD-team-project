package com.example.una;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.una.Viewholders.ChallengeViewHolder;
import com.example.una.models.Challenge;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.DocumentSnapshot;

import org.parceler.Parcels;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class ChallengeDetailsActivity extends AppCompatActivity {

    Challenge challenge;
    Context context;
    FirestoreClient client;
    public final static String TAG = "ChallengeDetailsActivity";

    // the view objects
    @BindView(R.id.ivChallenge) ImageView ivChallenge;
    @BindView(R.id.tvChallengeTitle) TextView tvChallengeTitle;
    @BindView(R.id.tvChallengeOwnerRecipientInfo) TextView tvChallengeOwnerRecipientInfo;
    @BindView(R.id.tvNumParticipants) TextView tvNumParticipants;
    @BindView(R.id.btnJoin) Button btnJoin;
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
        // TODO factor out repeated code from StreaksComplexRecyclerViewAdapter
        tvChallengeTitle.setText(challenge.getChallengeName());
        setTvOwnerRecipientInfo(challenge);
        setTvNumParticipants(challenge);
        setTvTimeLeft(challenge);
        setTvProgress(challenge);
        tvDescription.setText(challenge.getChallengeDescription());

        String sFrequency = challenge.getChallengeFrequency();
        if (sFrequency != null) {
            chipFrequency.setText(sFrequency);
        } else {
            chipFrequency.setVisibility(GONE);
        }

        String sType = challenge.getChallengeType();
        if (sType != null) {
            chipType.setText(sType);
        } else {
            chipType.setVisibility(GONE);
        }

        // set challenge image placeholder
        Glide.with(context)
                .load(challenge.getChallengeImageUrl())
                .into(ivChallenge);
    }

    // set text view with donor-recipient information
    private void setTvOwnerRecipientInfo(Challenge challenge) {
        String associatedCharity = challenge.getChallengeAssociatedCharityEin();
        String associatedCharityName = challenge.getChallengeAssociatedCharityName();
        String challengeOwner = challenge.getChallengeOwnerId();
        String ownerRecipientInfo;

        // check if challenge owner and associated charity are the same
        if (associatedCharity.equals(challengeOwner)) {
            ownerRecipientInfo = "Fundraiser by " + associatedCharityName;
            tvChallengeOwnerRecipientInfo.setText(ownerRecipientInfo);
        } else {
            // TODO remove if only charities can create challenges
            // owner is a user; get his or her name
            client.getChallengeUserCreator(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String ownerName = document.get("first_name") + " " + document.get("last_name");
                            String sChallengeOwnerRecipientInfo = "Fundraiser for "
                                    + associatedCharityName + " by " + ownerName;
                            tvChallengeOwnerRecipientInfo.setText(sChallengeOwnerRecipientInfo);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            }, challengeOwner);
        }
    }

    private void setTvTimeLeft(Challenge challenge) {
        // set time left in challenge text view
        Date now = Calendar.getInstance().getTime();
        Date startDate = challenge.getChallengeStartDate();
        Date endDate = challenge.getChallengeEndDate();
        if (startDate.after(now)) {
            tvTimeLeft.setText(getTimeLeft("Begins in ", now, startDate));
        } else {
            tvTimeLeft.setText(getTimeLeft("Ends in ", now, endDate));
        }
    }

    // set text view with number of participants
    private void setTvNumParticipants(Challenge challenge) {
        int numParticipants = challenge.getChallengeNumParticipants();
        String sNumParticipants;
        if (numParticipants == 1) {
            sNumParticipants = numParticipants + " participant";
        } else {
            sNumParticipants = numParticipants + " participants";
        }
        tvNumParticipants.setText(sNumParticipants);
    }

    // set text view with challenge progress information
    private void setTvProgress(Challenge challenge) {
        long amountRaised = challenge.getChallengeAmountRaised();
        long amountTarget = challenge.getChallengeAmountTarget();
        String sAmountRaised = formatCurrency(amountRaised);
        String sAmountTarget = formatCurrency(amountTarget);
        String sProgress;
        // check if there is a target goal
        if (amountTarget == 0) {
            sProgress = sAmountRaised + " raised";
            // hide progress bar
            pbProgress.setVisibility(GONE);
        } else {
            sProgress = sAmountRaised + " raised of " + sAmountTarget;
            // set progress bar
            pbProgress.setMax((int) amountTarget);
            pbProgress.setProgress((int) amountRaised);
        }
        tvProgress.setText(sProgress);
    }

    private String formatCurrency(long amount) {
        NumberFormat dollars = NumberFormat.getCurrencyInstance(Locale.US);
        return dollars.format(amount);
    }

    // https://stackoverflow.com/questions/42610657/how-to-calculate-the-time-left-untill-some-date
    public static String getTimeLeft(String beginOrEnd, Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();
        List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
        Collections.reverse(units);
        Map<TimeUnit, Long> result = new LinkedHashMap<TimeUnit, Long>();
        long milliesRest = diffInMillies;
        for ( TimeUnit unit : units ) {
            long diff = unit.convert(milliesRest, TimeUnit.MILLISECONDS);
            long diffInMilliesForUnit = unit.toMillis(diff);
            milliesRest = milliesRest - diffInMilliesForUnit;
            result.put(unit, diff);
        }

        if (result.get(TimeUnit.DAYS) > 1) {
            return beginOrEnd + result.get(TimeUnit.DAYS) + " days";
        } else if (result.get(TimeUnit.DAYS) == 1) {
            return beginOrEnd + result.get(TimeUnit.DAYS) + " day";
        } else if (result.get(TimeUnit.HOURS) > 1) {
            return beginOrEnd + result.get(TimeUnit.HOURS) + " hours";
        } else if (result.get(TimeUnit.HOURS) == 1) {
            return beginOrEnd + result.get(TimeUnit.HOURS) + " hour";
        } else if (result.get(TimeUnit.MINUTES) > 1) {
            return beginOrEnd + result.get(TimeUnit.MINUTES) + " minutes";
        } else if (result.get(TimeUnit.MINUTES) == 1) {
            return beginOrEnd + result.get(TimeUnit.MINUTES) + " minute";
        } else {
            return "Fundraiser ended";
        }
    }
}
