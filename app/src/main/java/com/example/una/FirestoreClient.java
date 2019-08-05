package com.example.una;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.una.models.Broadcast;
import com.example.una.models.Charity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

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
    private Context context;

    public FirestoreClient(Context context) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        this.context = context;
    }

    public void getBroadcasts(OnCompleteListener onCompleteListener) {
        broadcasts.orderBy("time", Query.Direction.DESCENDING).limit(20).get()
                .addOnCompleteListener(onCompleteListener);
    }

    public void getFavoriteCharity(OnSuccessListener onSuccessListener, OnFailureListener onFailureListener) {
        DocumentReference docRef = users.document(user.getUid());
        docRef.get().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void getCharityUserFromEin(String ein, final OnCompleteListener<Charity> onCompleteListener) {
        if (ein == null) {
            Log.d(TAG, "EIN is null");
        } else {
            charityUsers.document(ein).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (!task.isSuccessful()) {
                        failTask(onCompleteListener);
                        return;
                    }
                    DocumentSnapshot doc = task.getResult();
                    if (!doc.exists()) {
                        failTask(onCompleteListener);
                        return;
                    }
                    String storedName;
                    Map<String, Object> fields = doc.getData();
                    if (fields.containsKey("name")) {
                        storedName = (String) fields.get("name");
                    } else {
                        Log.d(TAG, "charity name not stored");
                        storedName = "";
                    }

                    final Charity charity = new Charity(ein, storedName);
                    onCompleteListener.onComplete(new SimpleTask<Charity>() {
                        @Override
                        public boolean isSuccessful() {
                            return true;
                        }

                        @Nullable
                        @Override
                        public Charity getResult() {
                            return charity;
                        }
                    });
                }

                private void failTask(OnCompleteListener<Charity> onCompleteListener) {
                    onCompleteListener.onComplete(new SimpleTask<Charity>() {
                        @Override
                        public boolean isSuccessful() {
                            return false;
                        }
                    });
                }
            });
        }
    }

    public void getCurrentUserName(OnCompleteListener onCompleteListener) {
        DocumentReference docRef = users.document(getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(onCompleteListener);
    }

    public void getChallengeUserCreator(OnCompleteListener onCompleteListener, String challengeOwner) {
        DocumentReference docRef = users.document(challengeOwner);
        docRef.get().addOnCompleteListener(onCompleteListener);
    }

    public void getChallengeParticipants(OnSuccessListener onSuccessListener, OnFailureListener onFailureListener,
                                         String challengeId) {
        DocumentReference docRef = challenges.document(challengeId);
        docRef.get().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void addUserToChallenge(String challengeId) {
        challenges.document(challengeId).update("users_accepted", FieldValue.arrayUnion(user.getUid()));
    }

    public void removeUserFromChallenge(String challengeId) {
        challenges.document(challengeId).update("users_accepted", FieldValue.arrayRemove(user.getUid()));
    }

    public void addDonorToChallenge(String challengeId) {
        challenges.document(challengeId).update("users_donated", FieldValue.arrayUnion(user.getUid()));
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
        broadcasts.whereEqualTo("charity_ein", ein).orderBy("time", Query.Direction.DESCENDING)
                .limit(20).get().addOnCompleteListener(onCompleteListener);
    }

    public void getChallengeDefaultAmount(OnSuccessListener onSuccessListener, OnFailureListener onFailureListener,
                                          String challengeId) {
        DocumentReference docRef = challenges.document(challengeId);
        docRef.get().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void updateChallengeProgress(String challengeId, double amount) {
        DocumentReference docRef = challenges.document(challengeId);
        docRef.update("amount_raised", FieldValue.increment(amount));
    }

    public void createNewBroadcast(OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener, Broadcast body) {
        Map<String, Object> broadcast = new HashMap<>();

        Date timeOfBroadcast = new Date();
        broadcast.put("time", new Timestamp(timeOfBroadcast));
        broadcast.put("type", body.getType());
        broadcast.put("privacy", body.getPrivacy());


        if (body.getType().equals(Broadcast.CHALLENGE_DONATION)) {
            broadcast.put("donor", user.getUid());
            broadcast.put("user_name", body.getUserName());
            broadcast.put("charity_name", body.getCharityName());
            broadcast.put("challenge_name", body.getChallengeName());
            broadcast.put("challenge_id", body.getChallengeName());

            String message = String.format("%s participated in %s\'s %s challenge", body.getUserName(),
                    body.getCharityName(), body.getChallengeName());
            broadcast.put("message", message);

        } else if (body.getType().equals(Broadcast.DONATION)) {
            broadcast.put("donor", user.getUid());
            broadcast.put("charity_ein", body.getCharityEin());
            broadcast.put("user_name", body.getUserName());
            broadcast.put("charity_name", body.getCharityName());
            String message = "";

            if (body.getFrequency().equals(Frequency.SINGLE_DONATION)) {
                message = String.format("%s donated to %s.", body.getUserName(),
                        body.getCharityName());
            } else {
                message = String.format("%s will donate to %s %s", body.getUserName(), body.getCharityName(), body.getFrequency());
            }
        } else if (body.getType().equals(Broadcast.NEW_CHALLENGE)) {
            String message;
            SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE);
            if (sharedPref.getBoolean("user_type", context.getResources().getBoolean(R.bool.is_user))) {
                // donor-created challenge
                message = broadcast.get("user_name") + " created a new challenge " + "\""
                        + broadcast.get("challenge_name") + ".\" Check it out!";
                broadcast.put("message", message);
            } else {
                // charity-created challenge
                message = broadcast.get("associated_charity_name") + " created a new challenge " + "\""
                        + broadcast.get("challenge_name") + ".\" Check it out!";
                broadcast.put("message", message);
            }
        } else if (body.getType().equals(Broadcast.POST)) {
            // TODO broadcast post
            // put charity user, message
        }

        broadcasts.document().set(broadcast)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);

    }

    public void findUserWhereIDEquals(String uid, OnSuccessListener onSuccessListener, OnFailureListener onFailureListener) {
        // pass in the users UID's & passes back the user's name
        DocumentReference docRef = users.document(uid);
        docRef.get().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void getDonations(OnCompleteListener onCompleteListener) {
        donations.get().addOnCompleteListener(onCompleteListener);
    }

    /*
    Double amount, String frequency, String privacy, String recipientEin,
                                  String challengeId, String challengeName,
                                  String userName, String charityName
     */
    public void createNewDonation(OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener,
                                  Broadcast body, Double amount) {
        Date timeOfDonation = new Date();
        Map<String, Object> donation = new HashMap<>();

        donation.put("amount", amount);
        donation.put("donor_id", user.getUid());
        donation.put("frequency", body.getFrequency());
        donation.put("recipient", body.getCharityEin());
        donation.put("time", new Timestamp(timeOfDonation));

        String type;
        // if donation was part of a challenge, add challenge_id field
        if (body.getChallengeId() != null) {
            donation.put("challenge_id", body.getChallengeId());
            type = Broadcast.CHALLENGE_DONATION;
        } else {
            type = Broadcast.DONATION;
        }
        // the donation is not a challenge and can be created automatically
        donations.document().set(donation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (body.getPrivacy() != PrivacySetting.PRIVATE) {
                            body.setDonor(user.getUid());
                            body.setTimestamp(new Timestamp(timeOfDonation));

                            createNewBroadcast(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    onSuccessListener.onSuccess(aVoid);
                                }}, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    onFailureListener.onFailure(e);
                                }}, body);
                        }
                    }
                })
                .addOnFailureListener(onFailureListener);
    }

    public void createNewChallenge(OnSuccessListener onSuccessListener, OnFailureListener onFailureListener,
                                   HashMap<String, Object> challenge) {
        Date timeOfChallengeCreation = new Date();
        challenge.put("start_date", timeOfChallengeCreation);
        challenge.put("owner", getCurrentUser().getUid());

        db.collection("challenges").document().set(challenge)
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

    public CollectionReference getDonationsCollection() {
        return donations;
    }

    public void fetchDonationsAfterFirstTime(String uid, DocumentSnapshot documentSnapshot, int itemsPerPage, OnSuccessListener<QuerySnapshot> onSuccessListener) {
        donations.whereEqualTo("donor_id", uid)
                 .orderBy("time", Query.Direction.DESCENDING)
                 .startAfter(documentSnapshot)
                 .limit(itemsPerPage)
                 .get().addOnSuccessListener(onSuccessListener);;
    }

    public void fetchDonationsFirstTime(String uid, int itemsPerPage, OnSuccessListener<QuerySnapshot> onSuccessListener) {
        donations.whereEqualTo("donor_id", uid)
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(itemsPerPage)
                .get().addOnSuccessListener(onSuccessListener);
    }

    public void getBroadcastsFirstTime(int loadedBroadcastsPerPage, OnSuccessListener<QuerySnapshot> onSuccessListener) {
        broadcasts.orderBy("time", Query.Direction.DESCENDING)
                .limit(loadedBroadcastsPerPage)
                .get().addOnSuccessListener(onSuccessListener);
    }

    public void getBroadcastsAfterFirstTime(DocumentSnapshot documentSnapshot, int loadedBroadcastsPerPage, OnSuccessListener<QuerySnapshot> onSuccessListener) {
        broadcasts.orderBy("time", Query.Direction.DESCENDING)
                .limit(loadedBroadcastsPerPage)
                .startAfter(documentSnapshot)
                .get().addOnSuccessListener(onSuccessListener);
    }

    private static abstract class SimpleTask<TResult> extends Task<TResult> {
        @Override
        public boolean isComplete() {
            return false;
        }

        @Override
        public boolean isSuccessful() {
            return false;
        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Nullable
        @Override
        public TResult getResult() {
            return null;
        }

        @Nullable
        @Override
        public <X extends Throwable> TResult getResult(@NonNull Class<X> aClass) throws X {
            return null;
        }

        @Nullable
        @Override
        public Exception getException() {
            return null;
        }

        @NonNull
        @Override
        public Task<TResult> addOnSuccessListener(@NonNull OnSuccessListener<? super TResult> onSuccessListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<TResult> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<TResult> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<TResult> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<TResult> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<TResult> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
            return null;
        }
    }
}