package com.example.una.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.FirestoreClient;
import com.example.una.UnaStartupActivity;
import com.example.una.models.Challenge;
import com.example.una.R;
import com.example.una.adapters.StreaksComplexRecyclerViewAdapter;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ImpactFragment extends Fragment {
    @BindString(R.string.challenge_description_dummy_text) String challengeDescription;
    @BindView(R.id.signOutButton) Button signOutButton;
    @BindView(R.id.rvChallenges) RecyclerView rvChallenges;

    public final static String TAG = "ImpactFragment";
    FirestoreClient client;

    ArrayList<Object> challenges;
    StreaksComplexRecyclerViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        challengeDescription = getContext().getResources().getString(R.string.challenge_description_dummy_text);
        View view = inflater.inflate(R.layout.fragment_impact, container, false);
        ButterKnife.bind(this, view);
        challenges = new ArrayList<>();
        adapter = new StreaksComplexRecyclerViewAdapter(challenges);
        rvChallenges.setAdapter(adapter);
        client = new FirestoreClient();
        getChallenges();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance()
                        .signOut(getContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent signOutIntent = new Intent(getContext(), UnaStartupActivity.class);
                                startActivity(signOutIntent);
                                getActivity().finish();
                            }
                        });
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvChallenges.setLayoutManager(layoutManager);
    }

    // place challenges for user here
    private ArrayList<Object> getChallenges() {
        challenges.add("image");
        // get all challenges from Firestore and create a new Challenge object for each one
        client.getChallenges(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot challengeDoc : task.getResult()) {
                            challenges.add(new Challenge(challengeDoc.getData()));
                            Log.d(TAG, challengeDoc.getId() + " => " + challengeDoc.getData());
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Error getting challengeDocs: ", task.getException());
                    }
                }
            });

        return challenges;
    }
}