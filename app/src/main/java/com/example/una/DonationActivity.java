package com.example.una;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.una.models.Broadcast;
import com.example.una.models.Charity;
import com.example.una.models.Donation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import org.parceler.Parcels;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DonationActivity extends AppCompatActivity {

    @BindView(R.id.charityName)
    TextView charityName;
    @BindView(R.id.valueOK)
    Button valueOKBtn;
    @BindView(R.id.valuePrompt)
    TextView valuePrompt;
    @BindView(R.id.submitDonation)
    Button submitDonationBtn;
    @BindView(R.id.amountInput)
    EditText amountInput;

    private final String TAG = "DonationActivity";

    boolean valueSet;
    private String currentAmount = "";
    private Double amount;
    private Context context;
    private String userName;

    FirestoreClient firestoreClient = new FirestoreClient(this);
    private Charity charity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);
        ButterKnife.bind(this);
        context = this;

        charity = Parcels.unwrap(getIntent().getParcelableExtra("charity"));
        charityName.setText(charity.getName());

        CurrencyTextWatcher watcher = new CurrencyTextWatcher(amountInput, currentAmount);
        amountInput.addTextChangedListener(watcher);
    }

    @OnClick(R.id.submitDonation)
    public void submitDonation() {
        if (valueSet) {
            firestoreClient.createNewDonation(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    DecimalFormat df = new DecimalFormat("#0.00");
                    Toast.makeText(context, String.format("You donated $%s!", df.format(amount)),
                            Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Donation Success!");

                    Map<String, Object> broadcast = new HashMap<>();

                    // get donor name
                    firestoreClient.getCurrentUserName(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() ) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    userName = document.get("first_name") + " "
                                            + document.get("last_name");
                                } else {
                                    Log.d(TAG, "using default username in broadcast");
                                    userName = "Mark Zuckerberg";
                                }
                            } else {
                                Log.d(TAG, "using default username in broadcast");
                                userName = "Mark Zuckerberg";
                            }

                            String message = userName + " donated to " + charity.getName();
                            broadcast.put("message", message);
                            broadcast.put("username", userName);

                            firestoreClient.createNewBroadcast(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i(TAG, "success adding donation to broadcasts");
                                }
                            }, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "failure adding donation to broadcasts");
                                }
                            }, Broadcast.DONATION, PrivacySetting.PUBLIC, broadcast);
                        }
                    });
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Donation failure :(", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Donation Failure", e);
                }
            }, amount, Frequency.SINGLE_DONATION, charity.getEin(), null);

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
