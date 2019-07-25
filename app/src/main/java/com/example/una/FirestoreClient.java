package com.example.una;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirestoreClient {

    private final String TAG = "FirestoreClient";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference users = db.collection("users");
    private CollectionReference challenges = db.collection("challenges");
    private FirebaseUser user;

    public FirestoreClient() {
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void getFavoriteCharity(OnSuccessListener onSuccessListener, OnFailureListener onFailureListener) {
        DocumentReference docRef = users.document(user.getUid());
        docRef.get().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    // TODO remove if only charities can create challenges
    public void getChallengeUserCreator(OnCompleteListener onCompleteListener, String challengeOwner) {
        DocumentReference docRef = users.document(challengeOwner);
        docRef.get().addOnCompleteListener(onCompleteListener);
    }

    public void getChallenges(OnCompleteListener onCompleteListener) {
        challenges.get().addOnCompleteListener(onCompleteListener);
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
