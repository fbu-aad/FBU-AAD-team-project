package com.example.una;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.una.utils.ChallengeViewsUtil.formatCurrency;

public class ChallengeDonationActivity extends AppCompatActivity {

    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvOwnerRecipientInfo) TextView tvOwnerRecipientInfo;
    @BindView(R.id.rgSuggestedDonations) RadioGroup rgSuggestedDonations;
    @BindView(R.id.radio5) RadioButton radio5;
    @BindView(R.id.radio10) RadioButton radio10;
    @BindView(R.id.radio20) RadioButton radio20;
    @BindView(R.id.radio50) RadioButton radio50;
    @BindView(R.id.radio100) RadioButton radio100;
    @BindView(R.id.etCustomAmount) EditText etCustomAmount;
    @BindView(R.id.sPrivacy) Spinner sPrivacy;
    @BindView(R.id.btnDonate) Button btnDonate;

    FirestoreClient client;
    String challengeId;
    private String currentAmount = "";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_donation);
        ButterKnife.bind(this);
        client = new FirestoreClient();

        // get challenge id from intent
        challengeId = getIntent().getStringExtra("challenge_id");
        // check if there is a default donation amount
        client.getChallengeDefaultAmount(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                long defaultAmount = 0;
                if (documentSnapshot.contains("default_amount")) {
                    defaultAmount = (long) documentSnapshot.get("default_amount");
                }
                // set default amount radio button as checked and disable edit text
                if (defaultAmount == 5) {
                    radio5.setChecked(true);
                } else if (defaultAmount == 10) {
                    radio10.setChecked(true);
                } else if (defaultAmount == 20) {
                    radio20.setChecked(true);
                } else if (defaultAmount == 50) {
                    radio50.setChecked(true);
                } else if (defaultAmount == 100) {
                    radio100.setChecked(true);
                } else if (defaultAmount != 0) {
                    etCustomAmount.setText("$" + formatCurrency(defaultAmount));
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }, challengeId);

        CurrencyTextWatcher watcher = new CurrencyTextWatcher(etCustomAmount, currentAmount);
        etCustomAmount.addTextChangedListener(watcher);

        // on custom edit focus change, deselect all radio buttons
        etCustomAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    radio5.setChecked(false);
                    radio10.setChecked(false);
                    radio20.setChecked(false);
                    radio50.setChecked(false);
                    radio100.setChecked(false);
                }
            }
        });

        rgSuggestedDonations.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!etCustomAmount.getText().toString().equals("")) {
                    etCustomAmount.getText().clear();
                    etCustomAmount.clearFocus();
                }
            }
        });

        // spinner to provide option of keeping participation private
        String[] privacyOptions = new String[]{"Public", "Friends", "Private"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, privacyOptions);
        sPrivacy.setAdapter(adapter);

        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get amount

                // get spinner selection

                // add to challenge progress

                // write to broadcast collection regarding challenge participation if not private

                // write to donations collection, including a challenge id

                // toast to confirm donation
                // TODO show donation impact alert dialog and thank you message

                // return result to calling activity
                Intent resultData = new Intent();
                setResult(RESULT_OK, resultData);
                finish();
            }
        });
    }


}
