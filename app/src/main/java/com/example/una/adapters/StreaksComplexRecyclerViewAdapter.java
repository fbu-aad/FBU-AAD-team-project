package com.example.una.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.una.FirestoreClient;
import com.example.una.models.Challenge;
import com.example.una.R;
import com.example.una.Viewholders.ChallengeViewHolder;
import com.example.una.Viewholders.StreaksViewHolder;
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

public class StreaksComplexRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // The challenges to display in your RecyclerView
    private ArrayList<Object> challenges;
    private final int IMAGE = 0, CHALLENGE = 1;
    public final static String TAG = "StreaksComplexRVAdapter";
    Context context;

    // Firestore client
    FirestoreClient client;

    public StreaksComplexRecyclerViewAdapter(ArrayList<Object> challenges) {
        this.challenges = challenges;
    }

    @Override
    public int getItemCount() {
        return this.challenges.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return IMAGE;
        } else {
            return CHALLENGE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        client = new FirestoreClient();
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        if (viewType == CHALLENGE) {
            View itemViewChallenge = inflater.inflate(R.layout.challenge_item_layout, viewGroup, false);
            viewHolder = new ChallengeViewHolder(itemViewChallenge, challenges, context);
        }
        else {
            View itemViewStreaks = inflater.inflate(R.layout.user_current_streaks_layout, viewGroup, false);
            viewHolder = new StreaksViewHolder(itemViewStreaks);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == IMAGE) {
            StreaksViewHolder streaksViewHolder = (StreaksViewHolder) viewHolder;
            configureStreakViewHolder(streaksViewHolder);
        } else {
            ChallengeViewHolder challengeViewHolder = (ChallengeViewHolder) viewHolder;
            configureChallengeViewHolder(challengeViewHolder, position);
        }
    }

    private void configureChallengeViewHolder(ChallengeViewHolder vhChallenge, int position) {
        Challenge challenge = (Challenge) challenges.get(position);
        if (challenge != null) {
            // set challenge title text view
            vhChallenge.getTvChallengeTitle().setText(challenge.getChallengeName());
            // set challenge owner and associated charity text view
            setTvOwnerRecipientInfo(vhChallenge, challenge);
            // set number of participants text view
            setTvNumParticipants(vhChallenge, challenge);
            // set text view for time left to start or end date of challenge
            setTvTimeLeft(vhChallenge, challenge);
            // set challenge progress text view and progress bar
            setTvProgress(vhChallenge, challenge);

            // set challenge image placeholder
            int random = (int) (Math.random() * 100 + 1);
            String url = "https://picsum.photos/id/" + random + "/400/200";
            challenge.setChallengeImageURl(url);
            Glide.with(context)
                    .load(url)
                    .into(vhChallenge.getIvChallengeImage());
        }
    }

    private void setTvTimeLeft(ChallengeViewHolder vhChallenge, Challenge challenge) {
        // set time left in challenge text view
        Date now = Calendar.getInstance().getTime();
        Date startDate = challenge.getChallengeStartDate();
        Date endDate = challenge.getChallengeEndDate();
        if (startDate.after(now)) {
            vhChallenge.getTvTimeLeft().setText(getTimeLeft("Begins in ", now, startDate));
        } else {
            vhChallenge.getTvTimeLeft().setText(getTimeLeft("Ends in ", now, endDate));
        }
    }

    // set text view with donor-recipient information
    private void setTvOwnerRecipientInfo(ChallengeViewHolder vhChallenge, Challenge challenge) {
        String associatedCharity = challenge.getChallengeAssociatedCharityEin();
        String associatedCharityName = challenge.getChallengeAssociatedCharityName();
        String challengeOwner = challenge.getChallengeOwnerId();
        String ownerRecipientInfo;

        // check if challenge owner and associated charity are the same
        if (associatedCharity.equals(challengeOwner)) {
            ownerRecipientInfo = "Fundraiser by " + associatedCharityName;
            vhChallenge.getTvChallengeOwnerRecipientInfo().setText(ownerRecipientInfo);
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
                            String tvChallengeOwnerRecipientInfo = "Fundraiser for "
                                    + associatedCharityName + " by " + ownerName;
                            vhChallenge.getTvChallengeOwnerRecipientInfo()
                                    .setText(tvChallengeOwnerRecipientInfo);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            }, challengeOwner);
        }
    }

    // set text view with number of participants
    private void setTvNumParticipants(ChallengeViewHolder vhChallenge, Challenge challenge) {
        int numParticipants = challenge.getChallengeNumParticipants();
        String sNumParticipants;
        if (numParticipants == 1) {
            sNumParticipants = numParticipants + " participant";
        } else {
            sNumParticipants = numParticipants + " participants";
        }
        vhChallenge.getTvNumParticipants().setText(sNumParticipants);
    }

    // set text view with challenge progress information
    private void setTvProgress(ChallengeViewHolder vhChallenge, Challenge challenge) {
        long amountRaised = challenge.getChallengeAmountRaised();
        long amountTarget = challenge.getChallengeAmountTarget();
        String sAmountRaised = formatCurrency(amountRaised);
        String sAmountTarget = formatCurrency(amountTarget);
        String tvProgress;
        // check if there is a target goal
        if (amountTarget == 0) {
            tvProgress = sAmountRaised + " raised";
            // hide progress bar
            vhChallenge.getPbProgress().setVisibility(GONE);
        } else {
            tvProgress = sAmountRaised + " raised of " + sAmountTarget;
            // set progress bar
            vhChallenge.getPbProgress().setMax((int) amountTarget);
            vhChallenge.getPbProgress().setProgress((int) amountRaised);
        }
        vhChallenge.getTvProgress().setText(tvProgress);
    }

    private String formatCurrency(long amount) {
        NumberFormat dollars = NumberFormat.getCurrencyInstance(Locale.US);
        return dollars.format(amount);
    }

    private void configureStreakViewHolder(StreaksViewHolder vhStreak) {
        vhStreak.getIvCharityImage().setImageResource(R.drawable.ic_streak_black_24dp);
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