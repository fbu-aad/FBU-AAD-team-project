package com.example.una;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.una.models.Charity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.parceler.Parcels;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CharityHomeActivity extends AppCompatActivity {

    Charity charity;

    @BindView(R.id.signOutBtn) Button signOutBtn;

    private final String TAG = "CharityHomeActivity";
    FirestoreClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_home);
        ButterKnife.bind(this);

        charity = Parcels.unwrap(getIntent().getParcelableExtra("charity"));
    }

    public Charity getCharity() {
        return charity;
    }
}