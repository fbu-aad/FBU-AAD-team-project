package com.example.una.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.una.FirestoreClient;
import com.example.una.models.Challenge;
import com.example.una.R;
import com.example.una.Viewholders.ChallengeViewHolder;
import com.example.una.Viewholders.StreaksViewHolder;

import java.util.ArrayList;

import static com.example.una.utils.ChallengeViewsUtil.getStrNumParticipants;
import static com.example.una.utils.ChallengeViewsUtil.getStrProgress;
import static com.example.una.utils.ChallengeViewsUtil.getStrTimeLeft;
import static com.example.una.utils.ChallengeViewsUtil.handleClickJoinBtn;
import static com.example.una.utils.ChallengeViewsUtil.setJoinBtn;
import static com.example.una.utils.ChallengeViewsUtil.setPbProgress;
import static com.example.una.utils.ChallengeViewsUtil.setTvOwnerRecipientInfo;

public class ChallengesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // The challenges to display in your RecyclerView
    private ArrayList<Challenge> challenges;
    public final static String TAG = "StreaksComplexRVAdapter";
    Context context;

    // Firestore client
    FirestoreClient client;

    public ChallengesAdapter(ArrayList<Challenge> challenges) {
        this.challenges = challenges;
    }

    @Override
    public int getItemCount() {
        return this.challenges.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        client = new FirestoreClient();
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View itemViewChallenge = inflater.inflate(R.layout.challenge_item_layout, viewGroup, false);
        viewHolder = new ChallengeViewHolder(itemViewChallenge, challenges, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ChallengeViewHolder challengeViewHolder = (ChallengeViewHolder) viewHolder;
        configureChallengeViewHolder(challengeViewHolder, position);
    }

    private void configureChallengeViewHolder(ChallengeViewHolder vhChallenge, int position) {
        Challenge challenge = (Challenge) challenges.get(position);
        if (challenge != null) {
            // set challenge title text view
            vhChallenge.getTvChallengeTitle().setText(challenge.getChallengeName());
            // set challenge owner and associated charity text view
            setTvOwnerRecipientInfo(client, vhChallenge.getTvChallengeOwnerRecipientInfo(), challenge);

            // set participants text view
            vhChallenge.getTvNumParticipants().setText(getStrNumParticipants(challenge));

            // set text view for time left to start or end date of challenge
            vhChallenge.getTvTimeLeft().setText(getStrTimeLeft(challenge));

            // set challenge progress text view
            vhChallenge.getTvProgress().setText(getStrProgress(challenge));

            // set progress bar
            setPbProgress(vhChallenge.getPbProgress(), challenge);

            // set challenge image placeholder
            int random = (int) (Math.random() * 100 + 1);
            String url = "https://picsum.photos/id/" + random + "/400/200";
            challenge.setChallengeImageUrl(url);
            Glide.with(context)
                    .load(url)
                    .into(vhChallenge.getIvChallengeImage());

            // set join button depending on whether user already accepted challenge
            ToggleButton btnJoin = vhChallenge.getBtnJoin();
            setJoinBtn(client, btnJoin, null, challenge);

            handleClickJoinBtn(client, btnJoin, null, challenge, context);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        challenges.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(ArrayList<Challenge> list) {
        challenges.addAll(list);
        notifyDataSetChanged();
    }
}