package com.example.una;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    protected static final int RC_SIGN_IN = 123;
    private final String TAG = this.getClass().getSimpleName();
    // Access a Cloud Firestore instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference users = db.collection("users");
    Context context;

    final List<AuthUI.IdpConfig> PROVIDERS = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.FacebookBuilder().build());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
    }

    // start the login process
    // TODO asks for first and last name upon login but the fields don't get stored in Firebase authentication
    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.i(TAG, user.getEmail() + " is already signed in -- updatingUI");
            // add to Firestore database if user is not already in it and launch survey activity
            addToDatabase(user);
            startHomeActivity(user);
        } else {
            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(PROVIDERS)
                            .setLogo(R.drawable.ic_cuppa_24dp)
                            .setTosAndPrivacyPolicyUrls(
                                    "https://una.co/terms.html",
                                    "https://una.co/privacy.html")
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // TODO never enters if block upon login - sign-in error
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "starting home activity from activity result");
                SharedPreferences sharedPref = context.getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("user_is_not_charity", true);
                editor.apply();

                // user is signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                startHomeActivity(user);
            } else {
                // Sign in failed
                if (response == null) {
                    Toast.makeText(this, "Sign in Cancelled", Toast.LENGTH_SHORT).show();
                    return;
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Unable to Complete Sign In", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void startHomeActivity(FirebaseUser user) {
        Log.i(TAG, "starting home activity");
        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.putExtra("user", user);
        startActivity(homeIntent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        Log.i(TAG, "updating UI from onStart");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        updateUI(currentUser);
    }

    private void addToDatabase(FirebaseUser user) {
        String uid = user.getUid();
        String email = user.getEmail();

        DocumentReference userDocRef = users.document(uid);
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Document exists!");
                    } else {
                        Log.d(TAG, "Document does not exist!");
                        // create a new user with id and email address
                        Map<String, Object> newUser = new HashMap<>();
                        if (email != null) {
                            newUser.put("email", email);
                        }

                        // add a new document with a generated id
                        Intent surveyIntent = new Intent(getApplicationContext(), SurveyActivity.class);
                        surveyIntent.putExtra("user_id", uid);
                        users.document(uid).set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });

                        // launch survey activity
                        startActivity(surveyIntent);
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });
    }
}
