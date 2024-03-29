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

import com.example.una.CharityHomeActivity;
import com.example.una.CreateChallengeScreenSlideActivity;
import com.example.una.FirestoreClient;
import com.example.una.R;
import com.example.una.adapters.ChallengesAdapter;
import com.example.una.models.Challenge;
import com.example.una.models.Charity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class CharityChallengesFragment extends Fragment {

    @BindView(R.id.fabCreateChallenge) FloatingActionButton fabCreateChallenge;
    @BindView(R.id.rvChallenges) RecyclerView rvChallenges;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    static final int CREATE_CHALLENGE = 111;
    private static final String TAG = "CharityChallengesPage";

    ChallengesAdapter adapter;
    Charity charity;
    FirestoreClient client;
    ArrayList<Challenge> challenges;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charity_challenges, container, false);
        ButterKnife.bind(this, view);

        client = new FirestoreClient();
        charity = ((CharityHomeActivity) getActivity()).getCharity();

        challenges = new ArrayList<>();
        adapter = new ChallengesAdapter(challenges);
        rvChallenges.setAdapter(adapter);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getChallenges();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvChallenges.setLayoutManager(layoutManager);
        getChallenges();
    }

    @OnClick(R.id.fabCreateChallenge)
    public void createChallenge() {
        Intent createChallengeIntent = new Intent(getContext(), CreateChallengeScreenSlideActivity.class);
        createChallengeIntent.putExtra("name", charity.getName());
        startActivityForResult(createChallengeIntent, CREATE_CHALLENGE);
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

    private void getChallenges() {
        client.getCharityChallenges(charity.getEin(), new OnCompleteListener<ArrayList<Challenge>>() {
            @Override
            public void onComplete(@NonNull Task<ArrayList<Challenge>> task) {
                if (task.isSuccessful()) {
                    adapter.clear();
                    challenges = task.getResult();
                    adapter.addAll(challenges);
                    adapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                } else {
                    Log.d(TAG, "failed getting challenges");
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getChallenges();
    }
}
