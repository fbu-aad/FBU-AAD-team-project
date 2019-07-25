package com.example.una;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.una.models.Charity;

import org.parceler.Parcels;

public class CharityHomeActivity extends AppCompatActivity {

    Charity charity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_home);

        charity = Parcels.unwrap(getIntent().getParcelableExtra("charity"));
    }

    // TODO: recyclerview of all the charities posts (challenges and notifications)
}
