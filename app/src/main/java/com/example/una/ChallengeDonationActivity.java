package com.example.una;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.example.una.models.Broadcast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.conn.routing.BasicRouteDirector;

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
    private String userName;
    private static final String TAG = "ChallengeDonation";
    boolean rgWasSelected = false;

    // radio button ids
    private static final int radio5Id = 5;
    private static final int radio10Id = 10;
    private static final int radio20Id = 20;
    private static final int radio50Id = 50;
    private static final int radio100Id = 100;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_donation);
        ButterKnife.bind(this);
        client = new FirestoreClient();

        // get extras from intent
        challengeId = getIntent().getStringExtra("challenge_id");
        String challengeName = getIntent().getStringExtra("challenge_name");
        String challengeInfo = getIntent().getStringExtra("challenge_info");
        String charityName = getIntent().getStringExtra("charity_name");
        String charityEin = getIntent().getStringExtra("charity_ein");

        // set challenge title
        tvName.setText(challengeName);

        // set challenge owner recipient info
        tvOwnerRecipientInfo.setText(challengeInfo);

        // set radio button ids
        radio5.setId(radio5Id);
        radio10.setId(radio10Id);
        radio20.setId(radio20Id);
        radio50.setId(radio50Id);
        radio100.setId(radio100Id);

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

        rgSuggestedDonations.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rgSuggestedDonations.getCheckedRadioButtonId() != -1 && !rgWasSelected) {
                    etCustomAmount.getText().clear();
                    etCustomAmount.clearFocus();
                }
                if (rgWasSelected) {
                    rgWasSelected = false;
                }
            }
        });

        // on custom edit focus change, deselect all radio buttons
        etCustomAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (rgSuggestedDonations.getCheckedRadioButtonId() != -1) {
                        rgWasSelected = true;
                    }
                    rgSuggestedDonations.clearCheck();
                    radio5.setChecked(false);
                    radio10.setChecked(false);
                    radio20.setChecked(false);
                    radio50.setChecked(false);
                    radio100.setChecked(false);
                }
            }
        });

        // spinner to provide option of keeping participation private
        String[] privacyOptions = new String[]{"Public", "Friends", "Private"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, privacyOptions);
        sPrivacy.setAdapter(adapter);

        // get donor name
        client.getCurrentUserName(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userName = document.get("first_name") + " " + document.get("last_name");
                    }
                }
            }
        });

        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get amount
                double amount = 0;
                if (rgSuggestedDonations.getCheckedRadioButtonId() != -1) {
                    amount = rgSuggestedDonations.getCheckedRadioButtonId();
                } else if (!etCustomAmount.getText().toString().isEmpty()) {
                    amount = getCustomDonationAmount(etCustomAmount);
                } else {
                    etCustomAmount.setError("Please enter an amount");
                }

                // get spinner selection
                String privacy;
                if (sPrivacy.getSelectedItemPosition() == 0) {
                    privacy = PrivacySetting.PUBLIC;
                } else if (sPrivacy.getSelectedItemPosition() == 1) {
                    privacy = PrivacySetting.FRIENDS;
                } else {
                    privacy = PrivacySetting.PRIVATE;
                }

                // update challenge progress
                client.updateChallengeProgress(challengeId, amount);

                Map<String, Object> mBroadcast = new HashMap<>();
                mBroadcast.put("challenge_id", challengeId);
                mBroadcast.put("challenge_name", challengeName);
                mBroadcast.put("charity_name", charityName);
                mBroadcast.put("type", Broadcast.CHALLENGE_DONATION);
                mBroadcast.put("privacy", privacy);
                mBroadcast.put("user_name", userName);
                mBroadcast.put("donor", client.getCurrentUser().getUid());
                mBroadcast.put("charity_ein", charityEin);

                Broadcast broadcast = new Broadcast(mBroadcast);

                // write to donations collection, including a challenge id
                client.createNewDonation(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Donation challenge success :)");
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Donation challenge failure :(");
                    }
                }, broadcast, amount);

                client.addDonorToChallenge(challengeId);

                // toast to confirm donation
                Toast.makeText(getApplicationContext(), R.string.donation_success, Toast.LENGTH_SHORT).show();

                // TODO show donation impact alert dialog and thank you message

                // return result to calling activity
                Intent resultData = new Intent();
                setResult(RESULT_OK, resultData);
                finish();
            }
        });
    }

    public double getCustomDonationAmount(EditText etCustomAmount) {
        Editable text = etCustomAmount.getText();
        double amount = 0;
        if (text == null || text.toString().equals("")) {
            noInputToast();
        } else {
            amount = Double.parseDouble(text.toString().replaceAll("[$,]", ""));
            if (amount == 0) {
                noInputToast();
            }
        }
        return amount;
    }

    private void noInputToast() {
        Toast.makeText(this, R.string.donation_no_input, Toast.LENGTH_SHORT).show();
    }
}
