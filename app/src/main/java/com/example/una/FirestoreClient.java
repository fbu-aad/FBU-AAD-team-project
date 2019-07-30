package com.example.una;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirestoreClient {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference users = db.collection("users");
    private CollectionReference challenges = db.collection("challenges");
    private CollectionReference donations = db.collection("donations");
    private CollectionReference charities = db.collection("charity_users");
    private CollectionReference broadcasts = db.collection("broadcasts");
    private CollectionReference charityUsers = db.collection("charity_users");
    private FirebaseUser user;
    private final String TAG = "FirestoreClient";

    public FirestoreClient() {
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void getFavoriteCharity(OnSuccessListener onSuccessListener, OnFailureListener onFailureListener) {
        DocumentReference docRef = users.document(user.getUid());
        docRef.get().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void getCharityUserFromEin(String ein, OnCompleteListener onCompleteListener) {
        if (ein == null) {
            Log.d(TAG, "EIN is null");
        } else {
            charityUsers.document(ein).get().addOnCompleteListener(onCompleteListener);
        }
    }

    // TODO remove if only charities can create challenges
    public void getChallengeUserCreator(OnCompleteListener onCompleteListener, String challengeOwner) {
        DocumentReference docRef = users.document(challengeOwner);
        docRef.get().addOnCompleteListener(onCompleteListener);
    }

    public void getChallengeParticipants(OnSuccessListener onSuccessListener, OnFailureListener onFailureListener, String challengeId) {
        DocumentReference docRef = challenges.document(challengeId);
        docRef.get().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void addUserToChallenge(String challengeId, String userId) {
        challenges.document(challengeId).update("users_accepted", FieldValue.arrayUnion(userId));
    }

    public void removeUserFromChallenge(String challengeId, String userId) {
        challenges.document(challengeId).update("users_accepted", FieldValue.arrayRemove(userId));
    }

    public void getChallenges(OnCompleteListener onCompleteListener) {
        challenges.get().addOnCompleteListener(onCompleteListener);
    }

    public void getCharity(String charityKey, OnCompleteListener onCompleteListener) {
        DocumentReference docRef = charities.document(charityKey);
        docRef.get().addOnCompleteListener(onCompleteListener);
    }

    public void findCharityByEIN(String ein, OnCompleteListener onCompleteListener) {
        // findCharityByEIN compares current Donation's EIN to charity's EIN
        charities.whereEqualTo("ein", ein).get().addOnCompleteListener(onCompleteListener);
    }

    public void findDonationsByUserId(String userId, OnCompleteListener onCompleteListener) {
        donations.whereEqualTo("donor_id", userId).get().addOnCompleteListener(onCompleteListener);
    }

    public void getCharitySpecificChallenges(String ein, OnCompleteListener onCompleteListener) {
        challenges.whereEqualTo("charity_ein", ein).get().addOnCompleteListener(onCompleteListener);
    }

    public void getCharityBroadcasts(String ein, OnCompleteListener onCompleteListener) {
        broadcasts.whereEqualTo("charity_ein", ein).get().addOnCompleteListener(onCompleteListener);
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

        donations.document().set(donation)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public FirebaseUser getCurrentUser() {
        return user;
    }

    public void setNewCharity(String name, String ein, String email, OnSuccessListener onSuccessListener,
                              OnFailureListener onFailureListener) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("ein", ein);
        data.put("email", email);

        charityUsers.document(ein).set(data).addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }
}
