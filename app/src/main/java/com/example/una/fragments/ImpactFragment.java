package com.example.una.fragments;


import android.content.Intent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.LoginActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.example.una.Challenge;
import com.example.una.R;
import com.example.una.adapters.StreaksComplexRecyclerViewAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImpactFragment extends Fragment {
    @BindView(R.id.signOutButton) Button signOutButton;
    @BindView(R.id.rvChallenges) RecyclerView rvChallenges;
    @BindView(R.id.tvChallengeDescription) TextView tvChallengeDescription;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_impact, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Nullable
    @OnClick(R.id.tvChallengeDescription)
    public void onClick() {
        Toast.makeText(getContext(), "I am working!", Toast.LENGTH_LONG).show();
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

    private ArrayList<Object> getSampleArrayList() {
        ArrayList<Object> items = new ArrayList<>();
        items.add("image");
        items.add(new Challenge("Dany Targaryen", "Valyria"));
        items.add(new Challenge("Rob Stark", "Winterfell"));
        items.add(new Challenge("Jon Snow", "Castle Black"));
        items.add(new Challenge("Tyrion Lanister", "King's Landing"));
        items.add(new Challenge("Tyrion Lanister", "King's Landing"));
        items.add(new Challenge("Tyrion Lanister", "King's Landing"));
        items.add(new Challenge("Tyrion Lanister", "King's Landing"));
        items.add(new Challenge("Tyrion Lanister", "King's Landing"));
        return items;
    }

    private void bindDataToAdapter() {
        // Bind adapter to recycler view object
        rvChallenges.setAdapter(new StreaksComplexRecyclerViewAdapter(getSampleArrayList()));
    }
}