package com.example.una.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.una.CharityNavigatorClient;
import com.example.una.CurrencyTextWatcher;
import com.example.una.FirestoreClient;
import com.example.una.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class CreateChallengeBasicInfoFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private OnButtonClickListener mOnButtonClickListener;
    private String currentAmount = "";
    private CharityNavigatorClient cnClient;
    private FirestoreClient fsClient;
    private boolean validEin = true;
    public static final String CHALLENGE_PREFERENCES = "ChallengePreferences";

    public interface OnButtonClickListener{
        void onButtonClicked(View view);
    }

    @BindView(R.id.etAssociatedCharity) EditText etAssociatedCharity;
    @BindView(R.id.etGoalAmount) EditText etGoalAmount;
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
        cnClient = new CharityNavigatorClient(this.getContext());
        fsClient = new FirestoreClient();
        ButterKnife.bind(this, rootView);

        CurrencyTextWatcher watcher = new CurrencyTextWatcher(etGoalAmount, currentAmount);
        etGoalAmount.addTextChangedListener(watcher);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // if logged in user is a charity, set and disable EIN edit text
        SharedPreferences sharedPref = getContext().getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        if (!sharedPref.getBoolean("user_type", getResources().getBoolean(R.bool.is_user))) {
            etAssociatedCharity.setText(sharedPref.getString("charity_ein", fsClient.getCurrentUser().getUid()));
            etAssociatedCharity.setEnabled(false);
            etAssociatedCharity.setFocusable(false);
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CreateChallengeBasicInfoFragment.this,
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );
                // user cannot select dates before today as end date
                dpd.setMinDate(now);
                dpd.show(getFragmentManager(), "Datepickerdialog");
                dpd.setVersion(DatePickerDialog.Version.VERSION_2);
            }
        });

        // ensure that one chip is selected
        setChipGroupListener(cgFrequency);
        setChipGroupListener(cgMatching);

        ibNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> challenge = new HashMap<>();

                // TODO validate charity ein; for now, assumes user enters valid ein
                // charityExists(etAssociatedCharity.getText().toString(), etAssociatedCharity);
                String ein = etAssociatedCharity.getText().toString();

                // check if valid goal amount
                double goalAmount = 0;
                boolean validGoalAmount = true;
                if (etGoalAmount.getText().toString().isEmpty()) {
                    etGoalAmount.setError("Enter an amount");
                    validGoalAmount = false;
                } else if (getGoalAmount(etGoalAmount) == 0) {
                    etGoalAmount.setError("Enter an amount greater than $0.00");
                    validGoalAmount = false;
                } else {
                    goalAmount = getGoalAmount(etGoalAmount);
                }

                boolean validDate = true;
                if (etEndDate.getText().toString().isEmpty()) {
                    etEndDate.setError("Select an end date");
                    validDate = false;
                }

                // get frequency
                Chip cFrequency = cgFrequency.findViewById(cgFrequency.getCheckedChipId());
                String frequency = cFrequency.getText().toString();

                // get matching boolean
                Chip cMatching = cgMatching.findViewById(cgMatching.getCheckedChipId());
                String sMatching = cMatching.getText().toString();
                boolean bMatching;
                if (sMatching.equals("yes")) {
                    bMatching = true;
                } else {
                    bMatching = false;
                }

                if (validGoalAmount && validEin && validDate) {
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(CHALLENGE_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("associated_charity_ein", ein);
                    editor.putLong("goal_amount", (long) goalAmount);
                    editor.putString("end_date", etEndDate.getText().toString());
                    editor.putString("frequency", frequency);
                    editor.putBoolean("matching", bMatching);
                    editor.apply();
                    mOnButtonClickListener.onButtonClicked(v);
                }
            }
        });
    }

    private void charityExists(String ein, EditText etAssociatedCharity) {
        RequestParams params = new RequestParams();
        cnClient.getCharityInfo(params, ein, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] header, JSONObject response) {
                if (statusCode != 200) {
                    etAssociatedCharity.setError("Invalid EIN");
                    validEin = false;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        });
    }

    public double getGoalAmount(EditText etGoalAmount) {
        Editable text = etGoalAmount.getText();
        return Double.parseDouble(text.toString().replaceAll("[$,]", ""));
    }

    private void setChipGroupListener(ChipGroup cg) {
        cg.setOnCheckedChangeListener((chipGroup, id) -> {
            Chip chip = chipGroup.findViewById(chipGroup.getCheckedChipId());
            if (chip != null) {
                for (int i = 0; i < chipGroup.getChildCount(); i++) {
                    chipGroup.getChildAt(i).setClickable(true);
                }
                chip.setClickable(false);
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = (monthOfYear+1)+"/"+dayOfMonth+"/"+year;
        etEndDate.setText(date);
    }
}
