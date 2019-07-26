package com.example.una.Viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DonationHistoryViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tvCharityName)
    TextView tvDonationName;
    @BindView(R.id.tvDonationAmount)
    TextView tvDonationAmount;
//    @BindView(R.id.tvTimeStamp)
//    Timestamp tvTimeStamp;

    public DonationHistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public TextView getTvDonationName() {
        return tvDonationName;
    }

    public TextView getTvDonationAmount() {
        return tvDonationAmount;
    }

//    public Timestamp getTvTimeStamp() {
//        return tvTimeStamp;
//    }
}
