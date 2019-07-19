package com.example.una.Viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.R;

import butterknife.BindView;

public class ChallengeViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tvChallengeTitle) TextView tvChallengeTitle;
    @BindView(R.id.tvChallengeDescription) TextView tvChallengeDescription;
    public ChallengeViewHolder(@NonNull View itemView) {
        super(itemView);
        tvChallengeTitle = itemView.findViewById(R.id.tvChallengeTitle);
        tvChallengeDescription = itemView.findViewById(R.id.tvChallengeDescription);
    }

    public TextView getTvChallengeTitle() {
        return tvChallengeTitle;
    }

    public void setTvChallengeTitle(TextView tvChallengeTitle) {
        this.tvChallengeTitle = tvChallengeTitle;
    }

    public TextView getTvChallengeDescription() {
        return tvChallengeDescription;
    }

    public void setTvChallengeDescription(TextView tvChallengeDescription) {
        this.tvChallengeDescription = tvChallengeDescription;
    }
}
