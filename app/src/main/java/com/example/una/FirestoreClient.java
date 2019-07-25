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

    public FirestoreClient() {
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void getFavoriteCharity(OnSuccessListener onSuccessListener, OnFailureListener onFailureListener) {
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void createNewDonation(OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener,
                                  Double amount, String frequency, String recipientEin) {
        Date timeOfDonation = new Date();
        Map<String, Object> donation = new HashMap<>();
        donation.put("amount", amount);
        donation.put("donor_id", user.getUid());
        donation.put("frequency", frequency);
        donation.put("recipient", recipientEin);
        donation.put("time", new Timestamp(timeOfDonation));

        db.collection("donations").document().set(donation)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public FirebaseUser getCurrentUser() {
        return user;
    }
}
