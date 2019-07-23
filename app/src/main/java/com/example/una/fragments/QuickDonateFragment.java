package com.example.una.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.una.DonationActivity;
import com.example.una.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuickDonateFragment extends Fragment {
    @BindView(R.id.tvQuickDonate) TextView quickDonateText;
    @BindView(R.id.ibQuickDonate) ImageButton ibQuickDonate;
    @BindView(R.id.tvCharityDescription) TextView tvCharityDescription;
    @BindView(R.id.charityName) TextView tvCharityName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quick_donate, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ibQuickDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent donateIntent = new Intent(getContext(), DonationActivity.class);
                startActivity(donateIntent);
            }
        });
        tvCharityName.setText("Dummy holder name");
        tvCharityDescription.setText("Dummy holder description");
    }
}
