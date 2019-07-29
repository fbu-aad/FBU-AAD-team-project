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

    public DonationHistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
