package com.example.una;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.una.models.Charity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirestoreClient {

    private final String TAG = "FirestoreClient";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;
    Context context;

    public FirestoreClient(Context context) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        this.context = context;
    }

    public Charity getFavoriteCharity() {
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // populate the charity here
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "get favorite charity failed");
            }
        });

        // TODO: populate this with the charity from the user data, not this default charity
        return new Charity(context.getString(R.string.red_cross_ein), "American Red Cross");
    }

    public void createNewDonation(Double amount, String frequency, String recipientEin) {
        Date timeOfDonation = new Date();
        Map<String, Object> donation = new HashMap<>();
        donation.put("amount", amount);
        donation.put("donor_id", user.getUid());
        donation.put("frequency", frequency);
        donation.put("recipient", recipientEin);
        donation.put("time", new Timestamp(timeOfDonation));

        db.collection("donations").document().set(donation).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DecimalFormat df = new DecimalFormat("#0.00");
                Toast.makeText(context, String.format("You donated $%s!", df.format(amount)), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Donation Success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Donation failure :(", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Donation Failure", e);
            }
        });
    }

    public FirebaseUser getCurrentUser() {
        return user;
    }
}
