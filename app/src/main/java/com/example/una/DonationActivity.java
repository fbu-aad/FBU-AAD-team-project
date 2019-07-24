package com.example.una;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.una.models.Charity;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.cloud.FirestoreClient;

import java.lang.ref.Reference;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DonationActivity extends AppCompatActivity {

    @BindView(R.id.charityName) TextView charityName;
    @BindView(R.id.valueOK) Button valueOKBtn;
    @BindView(R.id.valuePrompt) TextView valuePrompt;
    @BindView(R.id.submitDonation) Button submitDonationBtn;
    @BindView(R.id.amountInput) EditText amountInput;

    private final String TAG = "DonationActivity";

    boolean valueSet;
    private String currentAmount = "";
    private Double amount;
    private static int donationCount = 2;

    private Firestore db = FirestoreClient.getFirestore();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Charity charity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);
        ButterKnife.bind(this);

        // TODO if the user has donated already, get the most recent donation.
        // otherwise, get the default charity
        charity = getIntent().getParcelableExtra("charity");

        amountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(currentAmount)) {
                    // remove the change listener
                    amountInput.removeTextChangedListener(this);

                    // format the string to be in $#,##0.00 format
                    String cleanAmount = s.toString().replaceAll("[,$.]", "");
                    double parsed = Double.parseDouble(cleanAmount);
                    String formatted = NumberFormat.getCurrencyInstance().format(parsed/100);

                    currentAmount = formatted;
                    amountInput.setText(formatted);
                    amountInput.setSelection(formatted.length());

                    // replace the change listener
                    amountInput.addTextChangedListener(this);
                }

            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    @OnClick(R.id.submitDonation)
    public void submitDonation() {
        if (valueSet) {
            DocumentReference donationsRef = db.collection("donations").document();

            Map<String, Object> donationData = new HashMap<>();
            donationData.put("amount", amount);
            donationData.put("donation_id", "donation_" + donationCount);
            donationData.put("donor_id", user.getUid());
            donationData.put("frequency", Frequency.SINGLE_DONATION);
            donationData.put("recipient", charity.getEin());
            donationData.put("time", new Date());

            ApiFuture<WriteResult> result = donationsRef.set(donationData);

            donationCount++;
            Toast.makeText(this, R.string.donation_success, Toast.LENGTH_SHORT).show();
            Intent goHomeIntent = new Intent(this, MainActivity.class);
            startActivity(goHomeIntent);
        } else {
            Toast.makeText(this, R.string.donation_no_input, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.valueOK)
    public void acceptDonationAmount() {
        Editable text = amountInput.getText();
        if (text == null || text.toString().equals("")) {
            noInputToast();
        } else {
            amount = Double.parseDouble(text.toString().replaceAll("[$,]", ""));
            Log.i(TAG, amount.toString());
            if (amount == 0) {
                noInputToast();
            } else {
                valueOKBtn.setEnabled(false);
                amountInput.setEnabled(false);
                valueSet = true;
            }
        }
    }

    private void noInputToast() {
        Toast.makeText(this, R.string.donation_no_input, Toast.LENGTH_SHORT).show();
    }
}
