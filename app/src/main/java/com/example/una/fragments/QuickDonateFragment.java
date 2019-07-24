package com.example.una.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.fragment.app.Fragment;

import com.example.una.DonationActivity;
import com.example.una.FirestoreClient;
import com.example.una.R;
import com.example.una.models.Charity;


import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuickDonateFragment extends Fragment {
    private static final String TAG = "QuickDonateFragment";
    @BindView(R.id.tvQuickDonate) TextView quickDonateText;
    @BindView(R.id.ibQuickDonate) ImageButton ibQuickDonate;
    @BindView(R.id.tvCharityDescription) TextView tvCharityDescription;
    @BindView(R.id.charityName) TextView tvCharityName;

    FirestoreClient client;
    Charity charity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quick_donate, container, false);
        ButterKnife.bind(this, view);

        client = new FirestoreClient(getContext());

        charity = client.getFavoriteCharity();

        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvCharityName.setText(charity.getName());

        ibQuickDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO pass the charity to the donation activity
                Intent donateIntent = new Intent(getContext(), DonationActivity.class);
                donateIntent.putExtra("charity", Parcels.wrap(charity));
                startActivity(donateIntent);
            }
        });

        tvCharityDescription.setText("Dummy holder description");
    }



}
