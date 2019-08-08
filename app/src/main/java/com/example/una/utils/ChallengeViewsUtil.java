package com.example.una.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import com.example.una.ChallengeDetailsActivity;
import com.example.una.FirestoreClient;
import com.example.una.models.Challenge;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import static android.view.View.GONE;

public class ChallengeViewsUtil {
    public static final String TAG = "ChallengeViewsUtil";

    // set text view with donor-recipient information
    public static void setTvOwnerRecipientInfo(TextView tvChallengeOwnerRecipientInfo, Challenge challenge) {
        String associatedCharityName = challenge.getChallengeAssociatedCharityName();
        String userName = challenge.getChallengeCreatorName();

        tvChallengeOwnerRecipientInfo.setText("Challenge for " + associatedCharityName + " by " + userName);
    }

    // set join button depending on whether user already accepted challenge
    public static void setJoinBtn(FirestoreClient client, ToggleButton btnJoin, ToggleButton btnDonate, Challenge challenge) {
        String userId = client.getCurrentUser().getUid();
        String challengeId = challenge.getUid();
        client.getChallengeParticipants(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> usersAccepted = (ArrayList<String>) documentSnapshot.get("users_accepted");
                if (usersAccepted != null && usersAccepted.contains(userId)) {
                    btnJoin.setChecked(true);
                } else {
                    btnJoin.setChecked(false);
                    if (btnDonate != null) {
                        btnDonate.setEnabled(false);
                    }
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }, challengeId);
    }

    // set on click listener for join button
    public static void handleClickJoinBtn(FirestoreClient client, ToggleButton btnJoin, ToggleButton btnDonate, Challenge challenge, Context context) {
        String challengeId = challenge.getUid();
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!btnJoin.isChecked()) {
                    // user already joined; click to leave, removing user from challenge collection
                    client.removeUserFromChallenge(challengeId);
                    if (btnDonate != null) {
                        btnDonate.setEnabled(false);
                    }
                    Toast.makeText(context, "You have left the challenge", Toast.LENGTH_SHORT).show();
                } else {
                    // add user to challenge collection
                    client.addUserToChallenge(challengeId);
                    if (btnDonate != null) {
                        if (!btnDonate.isEnabled() && !btnDonate.isChecked()) {
                            btnDonate.setEnabled(true);
                        }
                    } else {
                        Intent challengeDetails = new Intent(context, ChallengeDetailsActivity.class);
                        challengeDetails.putExtra(Challenge.class.getSimpleName(), Parcels.wrap(challenge));
                        context.startActivity(challengeDetails);
                    }
                    Toast.makeText(context, "You have joined the challenge", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // get number of participants string
    public static String getStrNumParticipants(Challenge challenge) {
        int numParticipants = challenge.getChallengeNumParticipants();
        String sNumParticipants;
        if (numParticipants == 1) {
            sNumParticipants = numParticipants + " participant";
        } else {
            sNumParticipants = numParticipants + " participants";
        }
        return sNumParticipants;
    }

    // get time left string for challenge text view
    public static String getStrTimeLeft(Challenge challenge) {
        Date now = Calendar.getInstance().getTime();
        Date startDate = challenge.getChallengeStartDate();
        Date endDate = challenge.getChallengeEndDate();
        if (startDate.after(now)) {
            return getTimeLeft("Begins in ", now, startDate);
        } else {
            return getTimeLeft("Ends in ", now, endDate);
        }
    }

    // get string for challenge progress information
    public static String getStrProgress(Challenge challenge) {
        double amountRaised = challenge.getChallengeAmountRaised();
        double amountTarget = challenge.getChallengeAmountTarget();
        String sAmountRaised = formatCurrency((long) amountRaised);
        String sAmountTarget = formatCurrency((long) amountTarget);
        String sProgress;
        // check if there is a target goal
        if (amountTarget == 0) {
            sProgress = sAmountRaised + " raised";
        } else {
            sProgress = sAmountRaised + " raised of " + sAmountTarget;
        }
        return sProgress;
    }

    // set progress bar
    public static void setPbProgress(ProgressBar pb, Challenge challenge) {
        double amountRaised = challenge.getChallengeAmountRaised();
        double amountTarget = challenge.getChallengeAmountTarget();
        // check if there is a target goal
        if (amountTarget == 0) {
            // hide progress bar
            pb.setVisibility(GONE);
        } else {
            // set progress bar
            pb.setMax((int) amountTarget);
            pb.setProgress((int) amountRaised);
        }
    }

    public static String formatCurrency(long amount) {
        NumberFormat dollars = NumberFormat.getCurrencyInstance(Locale.US);
        return dollars.format(amount);
    }

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
