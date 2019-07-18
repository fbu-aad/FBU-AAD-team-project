package com.example.una;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    protected static final int RC_SIGN_IN = 123;
    private final String TAG = this.getClass().getSimpleName();

    private FirebaseAuth auth;

    final List<AuthUI.IdpConfig> PROVIDERS = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.FacebookBuilder().build());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
    }

    // start the login process
    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.i(TAG, user.getEmail() + " is already signed in -- updatingUI");
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
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "starting home activity from activity result");
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
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        Log.i(TAG, "updating UI from onStart");
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }
}
