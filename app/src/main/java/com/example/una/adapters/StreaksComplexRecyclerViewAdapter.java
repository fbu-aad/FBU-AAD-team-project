package com.example.una.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.Challenge;
import com.example.una.R;
import com.example.una.Viewholders.ChallengeViewHolder;
import com.example.una.Viewholders.StreaksViewHolder;

import java.util.List;

import butterknife.BindView;

public class StreaksComplexRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    // The items to display in your RecyclerView
    private List<Object> items;
    private final int IMAGE = 0, CHALLENGE = 1;
    public StreaksComplexRecyclerViewAdapter(List<Object> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
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
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (viewType) {
            case CHALLENGE:
                View itemViewChallenge = inflater.inflate(R.layout.challenge_viewholder_layout, viewGroup, false);
                viewHolder = new ChallengeViewHolder(itemViewChallenge);
                break;
            case IMAGE:
                View itemViewStreaks = inflater.inflate(R.layout.streaks_viewholder_layout, viewGroup, false);
                viewHolder = new StreaksViewHolder(itemViewStreaks);
                break;
            default:
                View v = inflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);
                viewHolder = new StreaksViewHolder(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Object challenge = items.get(position);
        if(position == 0) {
            StreaksViewHolder streaksViewHolder = (StreaksViewHolder) viewHolder;
            configureStreakViewHolder(streaksViewHolder);
        } else {
            ChallengeViewHolder challengeViewHolder = (ChallengeViewHolder) viewHolder;
            configureChallengeViewHolder(challengeViewHolder, position);
        }
    }

    private void configureChallengeViewHolder(ChallengeViewHolder vhChallenge, int position) {
        Challenge user = (Challenge) items.get(position);
        if (user != null) {
            vhChallenge.getTvChallengeTitle().setText("Challenge: " + user.getChallengeTitle());
            vhChallenge.getTvChallengeDescription().setText("Description: " + user.getChallengeDescription());
        }
    }

    private void configureStreakViewHolder(StreaksViewHolder vhStreak) {
        vhStreak.getIvCharityImage().setImageResource(R.drawable.ic_cuppa_24dp);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvChallengeDescription) TextView tvChallengeDescription;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
