package com.example.una;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.una.models.Charity;

import org.parceler.Parcels;

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

    FirestoreClient firestoreClient = new FirestoreClient(this);
    private Charity charity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);
        ButterKnife.bind(this);

        charity = Parcels.unwrap(getIntent().getParcelableExtra("charity"));
        charityName.setText(charity.getName());

        CurrencyTextWatcher watcher = new CurrencyTextWatcher(amountInput, currentAmount);
        amountInput.addTextChangedListener(watcher);
    }

    @OnClick(R.id.submitDonation)
    public void submitDonation() {
        if (valueSet) {
            firestoreClient.createNewDonation(amount, Frequency.SINGLE_DONATION, charity.getEin());

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
