package com.example.una.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.una.R;

public class QuickDonateFragment extends Fragment {
    ImageButton ibQuickDonate;
    TextView tvCharityName;
    TextView tvCharityDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quick_donate, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ibQuickDonate = view.findViewById(R.id.ibQuickDonate);
        tvCharityName = view.findViewById(R.id.tvCharityName);
        tvCharityDescription = view.findViewById(R.id.tvCharityDescription);
        ibQuickDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "ImageButton is working", Toast.LENGTH_LONG).show();
            }
        });
        tvCharityName.setText("Dummy holder name");
        tvCharityDescription.setText("Dummy holder description");
    }
}
