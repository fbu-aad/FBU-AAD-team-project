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

import androidx.fragment.app.Fragment;

import com.example.una.DonationActivity;
import com.example.una.R;
import com.example.una.models.Charity;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.cloud.FirestoreClient;

import org.parceler.Parcel;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuickDonateFragment extends Fragment {
    private static final String TAG = "QuickDonateFragment";
    @BindView(R.id.tvQuickDonate) TextView quickDonateText;
    @BindView(R.id.ibQuickDonate) ImageButton ibQuickDonate;
    @BindView(R.id.tvCharityDescription) TextView tvCharityDescription;
    @BindView(R.id.charityName) TextView tvCharityName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quick_donate, container, false);
        ButterKnife.bind(this, view);

        getFavoriteCharity(FirebaseAuth.getInstance().getCurrentUser());
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ibQuickDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO pass the charity to the donation activity
                Intent donateIntent = new Intent(getContext(), DonationActivity.class);
                startActivity(donateIntent);
            }
        });
        tvCharityName.setText("Dummy holder name");
        tvCharityDescription.setText("Dummy holder description");
    }

    public Charity getFavoriteCharity(FirebaseUser user) {
        String uid = user.getUid();
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentSnapshot> favCharityFuture = db.collection("users")
                .document(uid)
                .get();

        DocumentSnapshot userDoc;
        try {
            userDoc = favCharityFuture.get();
            if (userDoc.exists()) {
                Log.i(TAG, userDoc.getData().toString());
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
