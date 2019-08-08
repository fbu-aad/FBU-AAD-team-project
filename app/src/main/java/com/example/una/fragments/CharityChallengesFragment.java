package com.example.una.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.una.CreateChallengeScreenSlideActivity;
import com.example.una.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class CharityChallengesFragment extends Fragment {

    @BindView(R.id.fabCreateChallenge) FloatingActionButton fabCreateChallenge;
    static final int CREATE_CHALLENGE = 111;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    @OnClick(R.id.fabCreateChallenge)
    public void createChallenge() {
        Intent createChallengeIntent = new Intent(this, CreateChallengeScreenSlideActivity.class);
        startActivityForResult(createChallengeIntent, CREATE_CHALLENGE);
    }
}
