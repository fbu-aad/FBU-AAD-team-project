package com.example.una;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.zip.Deflater;

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
    Context context;
    String currentAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);
        context = this;

        ButterKnife.bind(this);

        amountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(currentAmount)) {
                    amountInput.removeTextChangedListener(this);

                    String cleanAmount = s.toString().replaceAll("[,$.]", "");
                    double parsed = Double.parseDouble(cleanAmount);
                    String formatted = NumberFormat.getCurrencyInstance().format(parsed/100);

                    currentAmount = formatted;

                    amountInput.setText(formatted);
                    amountInput.setSelection(formatted.length());

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
            Double amount = Double.parseDouble(text.toString().replaceAll("[$,]", ""));
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
