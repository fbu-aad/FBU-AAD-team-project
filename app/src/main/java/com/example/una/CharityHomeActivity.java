package com.example.una;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.una.models.Broadcast;
import com.example.una.models.Charity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CharityHomeActivity extends AppCompatActivity {

    Charity charity;
    @BindView(R.id.rvBroadcasts) RecyclerView rvBroadcasts;
    @BindView(R.id.signOutBtn) Button signOutBtn;
    @BindView(R.id.fabCreate) FloatingActionButton fabCreate;
    @BindView(R.id.fabCreateChallenge) FloatingActionButton fabCreateChallenge;

    private final String TAG = "CharityHomeActivity";
    static final int CREATE_CHALLENGE = 111;
    FirestoreClient client;

    ArrayList<Broadcast> broadcasts;
    BroadcastsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_home);
        ButterKnife.bind(this);

        charity = Parcels.unwrap(getIntent().getParcelableExtra("charity"));
        broadcasts = new ArrayList<>();
        adapter = new BroadcastsAdapter(broadcasts);

        rvBroadcasts.setAdapter(adapter);
        client = new FirestoreClient(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvBroadcasts.setLayoutManager(layoutManager);

        getBroadcasts();
        // getCharitySpecificChallenges();
    }

    @OnClick(R.id.fabCreate)
    public void createBroadcast() {
        Intent createBroadcastIntent = new Intent(this, CharityCreateBroadcastActivity.class);
        createBroadcastIntent.putExtra("charity", Parcels.wrap(charity));
        startActivity(createBroadcastIntent);
    }

    @OnClick(R.id.fabCreateChallenge)
    public void createChallenge() {
        Intent createChallengeIntent = new Intent(this, CreateChallengeScreenSlideActivity.class);
        startActivityForResult(createChallengeIntent, CREATE_CHALLENGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // check which request we are responding to
        if (requestCode == CREATE_CHALLENGE) {
            // make sure request was successful
            if (resultCode == RESULT_OK) {
                // user successfully created challenge
                adapter.notifyDataSetChanged();
            }
        }
    }

    @OnClick(R.id.signOutBtn)
    public void signOutCharity() {
        FirebaseAuth.getInstance().signOut();
        Intent goToStartupPage = new Intent(this, UnaStartupActivity.class);
        startActivity(goToStartupPage);
        finish();
    }

    // TODO create tab view with two screens for the challenges and donations

    // get the charities broadcasts
    private void getBroadcasts() {
        Log.i(TAG, String.format("Getting broadcasts for %s", charity.getEin()));
        Log.d(TAG, charity.getEin());
        client.getCharityBroadcasts(charity.getEin(), new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    broadcasts.clear();
                    Log.i(TAG, "completed getting broadcasts");
                    QuerySnapshot result = task.getResult();
                    for (QueryDocumentSnapshot broadcastsDoc : result) {
                        broadcasts.add(new Broadcast(broadcastsDoc.getData()));
                    }
                    adapter.notifyItemInserted(broadcasts.size() - 1);
                } else {
                    Log.d(TAG, "Error getting broadcasts: ", task.getException());
                }
            }
        });
    }
}