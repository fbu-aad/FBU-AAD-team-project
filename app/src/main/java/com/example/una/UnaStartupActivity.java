package com.example.una;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.una.models.Charity;

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

        FirestoreClient client = new FirestoreClient();
        if (client.getCurrentUser() != null) {
            startUserLogin();
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
        finish();
    }
}
