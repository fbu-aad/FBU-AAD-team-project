package com.example.una.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.una.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateChallengeBasicInfoFragment extends Fragment {

    private OnButtonClickListener mOnButtonClickListener;

    public interface OnButtonClickListener{
        void onButtonClicked(View view);
    }

    @BindView(R.id.etAssociatedCharity) EditText etAssociatedCharity;
    @BindView(R.id.etGoalAmount) EditText etGoalAmount;
    @BindView(R.id.etStartDate) EditText etStartDate;
    @BindView(R.id.etEndDate) EditText etEndDate;

    @BindView(R.id.cgFrequency) ChipGroup cgFrequency;
    @BindView(R.id.cOneTime) Chip cOneTime;
    @BindView(R.id.cDaily) Chip cDaily;
    @BindView(R.id.cWeekly) Chip cWeekly;
    @BindView(R.id.cMonthly) Chip cMonthly;
    @BindView(R.id.cYearly) Chip cYearly;

    @BindView(R.id.cgMatching) ChipGroup cgMatching;
    @BindView(R.id.cYes) Chip cYes;
    @BindView(R.id.cNo) Chip cNo;

    @BindView(R.id.ibNext) ImageButton ibNext;
    @BindView(R.id.btnCancel) Button btnCancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_create_challenge_basic_info, container, false);
        mOnButtonClickListener = (OnButtonClickListener) getContext();
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        ibNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnButtonClickListener.onButtonClicked(v);
            }
        });
    }

    // https://stackoverflow.com/questions/26821526/how-to-pass-share-object-from-fragment-to-fragment
}
