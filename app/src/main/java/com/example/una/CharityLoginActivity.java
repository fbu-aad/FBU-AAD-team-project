package com.example.una;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.una.models.Charity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CharityLoginActivity extends AppCompatActivity {

    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.etCharityEin) EditText etCharityEin;
    @BindView(R.id.etCharityName) EditText etCharityName;
    @BindView(R.id.signUpBtn) Button signUpBtn;
    @BindView(R.id.loginBtn) Button loginBtn;
    @BindView(R.id.etEmail) EditText etEmail;

    private final String TAG = "CharityLoginActivity";
    FirestoreClient client;
    FirebaseAuth mAuth;
    Context context;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_login);

        ButterKnife.bind(this);
        client = new FirestoreClient();
        mAuth = FirebaseAuth.getInstance();

        context = this;
        pd = new ProgressDialog(context);
        pd.setTitle("Signing In...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);

        if (mAuth.getCurrentUser() != null) {
            pd.show();

            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
            if (sharedPref.getBoolean("user_type", getResources().getBoolean(R.bool.is_user))) {
                Intent goToUserSide = new Intent(this, LoginActivity.class);
                pd.dismiss();
                startActivity(goToUserSide);
                finish();
            }
            String ein = sharedPref.getString("charity_ein", "530196605");
            getCharity(ein);
        }
    }

    @OnClick(R.id.signUpBtn)
    public void signUpCharity() {
        String name = etCharityName.getText().toString();
        String ein = etCharityEin.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if(checkInputs(name, ein, email, password)) {
            Charity charityUser = new Charity(ein, name);

            pd.show();
            // sign up the user with firebase
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // sign up the user with firestore
                                client.setNewCharity(name, ein, email, new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Log.d(TAG, String.format("%s successfully added", name));
                                        startCharityHome(new Charity(ein, name));
                                    }
                                }, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, String.format("%s not added to database", name));
                                        mAuth.signOut();
                                    }
                                });
                                startCharityHome(charityUser);
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(CharityLoginActivity.this,
                                        "Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @OnClick(R.id.loginBtn)
    public void logInCharity() {
        final String name = etCharityName.getText().toString();
        final String ein = etCharityEin.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (checkInputs(name, ein, email, password)) {
            // sign in the charity with firebase
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                getCharity(ein);
                            } else {
                                Log.w(TAG, "loginUserWithEmail:failure", task.getException());
                                Toast.makeText(CharityLoginActivity.this,
                                        "Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private void updateSharedPreferences(String ein) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("user_type", getResources().getBoolean(R.bool.is_charity));
        editor.putString("charity_ein", ein);
        editor.apply();
    }

    private void getCharity(String ein) {
        // make sure the user is in Firestore and check name and ein
        client.getCharityUserFromEin(ein, new OnCompleteListener<Charity>() {
            @Override
            public void onComplete(@NonNull Task<Charity> task) {
                if (task.isSuccessful()) {
                    Charity charity = task.getResult();
                    if (charity != null) {
                        startCharityHome(charity);
                    } else {
                        Log.d(TAG, "user signed in that is not in the database");
                    }
                }
            }
        });
    }

    private void startCharityHome(Charity charity) {
        Intent goToCharityHome = new Intent(this, CharityHomeActivity.class);
        updateSharedPreferences(charity.getEin());
        goToCharityHome.putExtra("charity", Parcels.wrap(charity));
        pd.dismiss();
        startActivity(goToCharityHome);
        finish();
    }

    private boolean checkInputs(String name, String ein, String email, String password) {
        boolean allFilled = true;
        if(name.isEmpty()) {
            etCharityName.setError("Required");
            allFilled = false;
        }
        if (ein.isEmpty()) {
            etCharityEin.setError("Required");
            allFilled = false;
        }
        if(email.isEmpty()) {
            etEmail.setError("Required");
            allFilled = false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Required");
            allFilled = false;
        }
        return allFilled;
    }
}
