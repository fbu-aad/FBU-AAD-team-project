package com.example.una.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.una.CreateChallengeScreenSlideActivity;
import com.example.una.FirestoreClient;
import com.example.una.R;
import com.example.una.adapters.ChallengesAdapter;
import com.example.una.models.Challenge;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class ChallengesFragment extends Fragment {
    @BindString(R.string.challenge_description_dummy_text) String challengeDescription;
    @BindView(R.id.rvChallenges) RecyclerView rvChallenges;
    @BindView(R.id.fabCreateChallenge) FloatingActionButton fabCreateChallenge;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    public final static String TAG = "ChallengesFragment";
    static final int CREATE_CHALLENGE = 111;
    FirestoreClient client;

    ArrayList<Challenge> challenges;
    ChallengesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        challengeDescription = getContext().getResources().getString(R.string.challenge_description_dummy_text);
        View view = inflater.inflate(R.layout.fragment_impact, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvChallenges.setLayoutManager(layoutManager);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getChallenges();
            }
        });

        challenges = new ArrayList<>();
        adapter = new ChallengesAdapter(challenges);
        rvChallenges.setAdapter(adapter);
        client = new FirestoreClient();
        getChallenges();

        fabCreateChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createChallenge = new Intent(getContext(), CreateChallengeScreenSlideActivity.class);
                createChallenge.putExtra("name", client.getCurrentUser().getDisplayName());
                startActivityForResult(createChallenge, CREATE_CHALLENGE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // check which request we are responding to
        if (requestCode == CREATE_CHALLENGE) {
            // make sure request was successful
            if (resultCode == RESULT_OK) {
                // user successfully created challenge
                adapter.notifyDataSetChanged();
            }
        }
    }

    // place challenges for user here
    private ArrayList<Challenge> getChallenges() {
        // get all challenges from Firestore and create a new Challenge object for each one
        client.getChallenges(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        adapter.clear();
                        for (QueryDocumentSnapshot challengeDoc : task.getResult()) {
                            challenges.add(new Challenge(challengeDoc.getData(), challengeDoc.getId()));
                        }
                        adapter.addAll(challenges);
                        adapter.notifyDataSetChanged();
                        swipeContainer.setRefreshing(false);
                    } else {
                        Log.d(TAG, "Error getting challengeDocs: ", task.getException());
                    }
                }
            });

        return challenges;
    }
}