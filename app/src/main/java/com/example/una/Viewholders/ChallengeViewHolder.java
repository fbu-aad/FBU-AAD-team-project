package com.example.una.Viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChallengeViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tvChallengeTitle) TextView tvChallengeTitle;
    @BindView(R.id.tvChallengeDescription) TextView tvChallengeDescription;
    @BindView(R.id.ivChallenge) ImageView ivChallenge;

    public ChallengeViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new ChallengeClickListener());
    }

    public TextView getTvChallengeTitle() {
        return tvChallengeTitle;
    }

    public TextView getTvChallengeDescription() {
        return tvChallengeDescription;
    }

    public ImageView getChallengeImage() { return ivChallenge; }

    static class ChallengeClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "I am working", Toast.LENGTH_LONG).show();
        }
    }
}