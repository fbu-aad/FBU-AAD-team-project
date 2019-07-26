package com.example.una.utils;

import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.una.FirestoreClient;
import com.example.una.models.Challenge;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

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
    static FirestoreClient client = new FirestoreClient();

    // set text view with donor-recipient information
    public static void setTvOwnerRecipientInfo(TextView tvChallengeOwnerRecipientInfo, Challenge challenge) {
        String associatedCharity = challenge.getChallengeAssociatedCharityEin();
        String associatedCharityName = challenge.getChallengeAssociatedCharityName();
        String challengeOwner = challenge.getChallengeOwnerId();
        String ownerRecipientInfo;

        // check if challenge owner and associated charity are the same
        if (associatedCharity.equals(challengeOwner)) {
            ownerRecipientInfo = "Fundraiser by " + associatedCharityName;
            tvChallengeOwnerRecipientInfo.setText(ownerRecipientInfo);
        } else {
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
        long amountRaised = challenge.getChallengeAmountRaised();
        long amountTarget = challenge.getChallengeAmountTarget();
        String sAmountRaised = formatCurrency(amountRaised);
        String sAmountTarget = formatCurrency(amountTarget);
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
        long amountRaised = challenge.getChallengeAmountRaised();
        long amountTarget = challenge.getChallengeAmountTarget();
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