package com.example.una;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.una.models.Charity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CharityLoginActivity extends AppCompatActivity {

    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.etCharityEin) EditText etCharityEin;
    @BindView(R.id.etCharityName) EditText etCharityName;
    @BindView(R.id.loginBtn) Button loginBtn;
    @BindView(R.id.signUpBtn) Button signUpBtn;
    @BindView(R.id.etEmail) EditText etEmail;

    private final String TAG = "CharityLoginActivity";
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_login);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.loginBtn)
    public void logInCharity() {
        String name = etCharityName.getText().toString();
        String ein = etCharityEin.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if(checkInputs(name, ein, email, password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        FirestoreClient client = new FirestoreClient();

                        client.getCharityFromEmail(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                                Charity charity = new Charity(, );
                                startCharityHome(charity);
                            }
                        });
                    } else {
                        Log.w(TAG, "loginUserWithEmail:failure", task.getException());
                        Toast.makeText(CharityLoginActivity.this,
                                "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @OnClick(R.id.signUpBtn)
    public void createCharityUser() {
        String name = etCharityName.getText().toString();
        String ein = etCharityEin.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if(checkInputs(name, ein, email, password)) {
            Charity charityUser = new Charity(ein, name);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // TODO: update the user as a charity in Firestore and start home activity

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

    private void startCharityHome(Charity charity) {
        Intent goToCharityHome = new Intent(this, CharityHomeActivity.class);
        goToCharityHome.putExtra("charity", Parcels.wrap(charity));
        startActivity(goToCharityHome);
    }

    private boolean checkInputs(String name, String ein, String email, String password) {

        if(name.isEmpty()) {
            etCharityName.setError("Required");
            return false;
        } else if (ein.isEmpty()) {
            etCharityEin.setError("Required");
            return false;
        } else if(email.isEmpty()) {
            etEmail.setError("Required");
            return false;
        } else if (password.isEmpty()) {
            etPassword.setError("Required");
            return false;
        }
        return true;
    }
}
