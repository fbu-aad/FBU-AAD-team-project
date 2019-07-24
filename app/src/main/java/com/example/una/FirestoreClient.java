package com.example.una;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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


    public String getFavoriteCharity() {
        DocumentReference docRef = db.collection("users").document(user.getUid());
        Log.i(TAG, docRef.get().toString());
        return context.getString(R.string.red_cross_ein);
    }

    public void createNewDonation(Double amount, String frequency, String recipientEin) {
        Date timeOfDonation = new Date();
        String donation_id = "donation_" + timeOfDonation;

        Map<String, Object> donation = new HashMap<>();
        donation.put("amount", amount);
        donation.put("donor_id", user.getUid());
        donation.put("frequency", frequency);
        donation.put("recipient", recipientEin);
        donation.put("time", new Timestamp(timeOfDonation));

        db.collection("donations").document(donation_id).set(donation).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Donation Success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing donation document", e);
            }
        });
    }

    public FirebaseUser getCurrentUser() {
        return user;
    }

}
