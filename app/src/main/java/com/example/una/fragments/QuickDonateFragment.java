package com.example.una.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.una.DonationActivity;
import com.example.una.FirestoreClient;
import com.example.una.R;
import com.example.una.models.Charity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

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

        client = new FirestoreClient();

        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        client.getFavoriteCharity(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String charityName = (String) documentSnapshot.get("fav_charity_name");
                String charityEin;

                if (documentSnapshot.contains("fav_charity_ein")) {
                    charityEin = documentSnapshot.get("fav_charity_ein").toString();
                } else {
                    charityEin = getString(R.string.red_cross_ein);
                }

                charity = new Charity(charityEin, charityName);
                tvCharityName.setText(charity.getName());

                ibQuickDonate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent donateIntent = new Intent(getContext(), DonationActivity.class);
                        donateIntent.putExtra("charity", Parcels.wrap(charity));
                        startActivity(donateIntent);
                    }
                });
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "cant get user favorite charity", e);
                Toast.makeText(getContext(), "Can't get favorite charity", Toast.LENGTH_SHORT).show();
            }
        });

        tvCharityDescription.setText("Dummy holder description");
    }

}
