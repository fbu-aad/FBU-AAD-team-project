package com.example.una;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ChallengeDonationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_donation);

        // get challenge id from intent
        // check if there is a default donation amount
        // if there is, set amount input and disable edit text

        // option to keep participation private
    }

    // add to challenge progress

    // broadcast challenge participation if not private

    // write to donations collection, including a challenge id

    // if user actually donates, show donation impact alert dialog and thank you message
    // return result to calling activity
//    Intent resultData = new Intent();
//    setResult(RESULT_OK, resultData);
//    finish();
}
