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
    @BindView(R.id.signOutButton) Button signOutButton;
    @BindView(R.id.rvChallenges) RecyclerView rvChallenges;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        ArrayList<Object> items = new ArrayList<>();
        items.add("image");
        items.add(new Challenge("Challenge 1", "Valyria"));
        items.add(new Challenge("Challenge 2", "Winterfell"));
        items.add(new Challenge("Challenge 3", "Castle Black"));
        items.add(new Challenge("Challenge 4", "King's Landing"));
        items.add(new Challenge("Challenge 5", "King's Landing"));
        items.add(new Challenge("Challenge 6", "King's Landing"));
        items.add(new Challenge("Challenge 7", "King's Landing"));
        items.add(new Challenge("Challenge 8", "King's Landing"));
        items.add(new Challenge("Challenge 9", "King's Landing"));
        return items;
    }

    private void bindDataToAdapter() {
        // Bind adapter to recycler view object
        rvChallenges.setAdapter(new StreaksComplexRecyclerViewAdapter(getSampleArrayList()));
    }
}