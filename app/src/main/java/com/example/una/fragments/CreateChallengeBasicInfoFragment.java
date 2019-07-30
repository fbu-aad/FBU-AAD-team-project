package com.example.una.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.una.R;

public class CreateChallengeBasicInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_create_challenge_basic_info, container, false);

        return rootView;
    }

    // https://stackoverflow.com/questions/26821526/how-to-pass-share-object-from-fragment-to-fragment
}
