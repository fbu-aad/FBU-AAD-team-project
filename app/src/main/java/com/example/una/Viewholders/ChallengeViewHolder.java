package com.example.una.Viewholders;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
        itemView.setOnClickListener(new ChallengeClickListener());
    }
    public TextView getTvChallengeTitle() {
        return tvChallengeTitle;
    }
    public TextView getTvChallengeDescription() {
        return tvChallengeDescription;
    }
    static class ChallengeClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "I am working", Toast.LENGTH_LONG).show();
        }
    }
}