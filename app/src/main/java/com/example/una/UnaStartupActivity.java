package com.example.una;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UnaStartupActivity extends AppCompatActivity {

    @BindView(R.id.tvAppTitle) TextView tvAppTitle;
    @BindView(R.id.userLoginBtn) TextView userLoginBtn;
    @BindView(R.id.charityLoginBtn) TextView charityLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_una_startup);

        ButterKnife.bind(this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            if (sharedPref.getBoolean("user_is_not_charity", true)) {
                startUserLogin();
            } else {
                startCharityLogin();
            }
        }
    }

    @OnClick(R.id.userLoginBtn)
    public void startUserLogin() {
        Intent userLoginIntent = new Intent(this, LoginActivity.class);
        startActivity(userLoginIntent);
        finish();
    }

    @OnClick(R.id.charityLoginBtn)
    public void startCharityLogin() {
        Intent charityLoginIntent = new Intent(this, CharityLoginActivity.class);
        startActivity(charityLoginIntent);
    }
}
