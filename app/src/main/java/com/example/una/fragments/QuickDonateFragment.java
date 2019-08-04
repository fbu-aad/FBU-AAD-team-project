package com.example.una.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.una.FirestoreClient;
import com.example.una.Frequency;
import com.example.una.PrivacySetting;
import com.example.una.R;
import com.example.una.models.Charity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuickDonateFragment extends Fragment {
    private static final String TAG = "QuickDonateFragment";
    @BindView(R.id.charityName) TextView tvCharityName;
    @BindView(R.id.donateBtn) Button donateBtn;
    @BindView(R.id.ivCharityPicture) ImageView ivCharityPicture;
    @BindView(R.id.tvStatsTitle) TextView tvStatsTitle;
    @BindView(R.id.tvAmountDonated) TextView tvAmountDonated;
    @BindView(R.id.tvAmountDonatedDesc) TextView tvAmountDonatedDesc;
    @BindView(R.id.tvTimesDonated) TextView tvTimesDonated;
    @BindView(R.id.tvTimesDonatedDesc) TextView tvTimesDonatedDesc;
    @BindView(R.id.tvNumChallengesCreated) TextView tvNumChallegesCreated;
    @BindView(R.id.tvNumChallengesDesc) TextView getTvNumChallegesDesc;
    @BindView(R.id.rgSuggestedDonations) RadioGroup rgSuggestedDonations;
    @BindView(R.id.radio1) RadioButton radio1;
    @BindView(R.id.radio3) RadioButton radio3;
    @BindView(R.id.radio10) RadioButton radio10;
    @BindView(R.id.radio50) RadioButton radio50;
    @BindView(R.id.radioTuition) RadioButton radioTuition;
    @BindView(R.id.etCustomAmount) EditText etCustomAmount;
    @BindView(R.id.tvDonationTitle) TextView tvDonationTitle;

    FirestoreClient client;
    Charity charity;

    Double amount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quick_donate, container, false);
        ButterKnife.bind(this, view);

        client = new FirestoreClient();
        amount = 0.00;

        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        client.getFavoriteCharity(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String charityName = (String) documentSnapshot.get("fav_charity_name");
                String charityEin;

                if (documentSnapshot.contains("fav_charity_ein")) {
                    charityEin = documentSnapshot.get("fav_charity_ein").toString();
                } else {
                    charityEin = getString(R.string.red_cross_ein);
                }

                charity = new Charity(charityEin, charityName);
                tvCharityName.setText(charity.getName());



                // TODO: I know this should be outside of the on success but i dont know how to move it
                rgSuggestedDonations.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch(checkedId) {
                            case R.id.radio1:
                                etCustomAmount.setText("24");
                                amount = 0.24;
                                break;
                            case R.id.radio3:
                                etCustomAmount.setText("325");
                                amount = 3.25;
                                break;
                            case R.id.radio5:
                                etCustomAmount.setText("549");
                                amount = 5.49;
                                break;
                            case R.id.radio10:
                                etCustomAmount.setText("1350");
                                amount = 13.50;
                                break;
                            case R.id.radioTuition:
                                etCustomAmount.setText("25,000.00");
                                amount = 25000.00;
                                break;
                            default:
                                etCustomAmount.setText("0.00");
                                amount = 0.00;
                                break;

                        }
                    }
                });

                donateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        client.createNewDonation(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Your donation was received",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Your donation was not received :(",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, amount, Frequency.SINGLE_DONATION, PrivacySetting.PUBLIC, charityEin,
                                null, null,
                                client.getCurrentUser().getDisplayName(), charityName);
                    }
                });
            }

        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "cant get user favorite charity", e);
                Toast.makeText(getContext(), "Can't get favorite charity", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
