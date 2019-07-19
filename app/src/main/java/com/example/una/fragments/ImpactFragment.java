package com.example.una.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.Challenge;
import com.example.una.LoginActivity;
import com.example.una.R;
import com.example.una.adapters.StreaksComplexRecyclerViewAdapter;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImpactFragment extends Fragment {
    private String curURL;
    @BindView(R.id.signOutButton) Button signOutButton;
    @BindView(R.id.rvChallenges) RecyclerView rvChallenges;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        curURL = getContext().getResources().getString(R.string.challenge_description_dummy_text);
        View view = inflater.inflate(R.layout.fragment_impact, container, false);
        ButterKnife.bind(this, view);
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
                                Intent signOutIntent = new Intent(getContext(), LoginActivity.class);
                                startActivity(signOutIntent);
                                getActivity().finish();
                            }
                        });
            }

        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvChallenges.setLayoutManager(layoutManager);
        bindDataToAdapter();
    }

    // place challenges for user here
    private ArrayList<Object> getSampleArrayList() {
        ArrayList<Object> challenge = new ArrayList<>();
        challenge.add("image");
        challenge.add(new Challenge("1", curURL));
        challenge.add(new Challenge("2", curURL));
        challenge.add(new Challenge("3", curURL));
        challenge.add(new Challenge("4", curURL));
        challenge.add(new Challenge("5", curURL));
        challenge.add(new Challenge("6", curURL));
        challenge.add(new Challenge("7", curURL));
        challenge.add(new Challenge("8", curURL));
        challenge.add(new Challenge("9", curURL));
        return challenge;
    }

    private void bindDataToAdapter() {
        // Bind adapter to recycler view object
        rvChallenges.setAdapter(new StreaksComplexRecyclerViewAdapter(getSampleArrayList()));
    }
}