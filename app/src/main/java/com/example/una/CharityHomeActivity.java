package com.example.una;

import androidx.annotation.NonNull;
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

    private final String TAG = "CharityHomeActivity";
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
        client = new FirestoreClient();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvBroadcasts.setLayoutManager(layoutManager);

        getBroadcasts();
        // getCharitySpecificChallenges();
    }

    @OnClick(R.id.signOutBtn)
    public void signOutCharity() {
        Intent goToStartupPage = new Intent(this, UnaStartupActivity.class);
        startActivity(goToStartupPage);
    }

    // TODO create tab view with two screens for the challenges and donations

    // get the charities broadcasts
    private void getBroadcasts() {
        Log.i(TAG, String.format("Getting broadcasts for %s", charity.getEin()));
        client.getCharityBroadcasts(charity.getEin(), new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "completed getting broadcasts");
                    int count = 0;
                    QuerySnapshot result = task.getResult();
                    for (QueryDocumentSnapshot broadcastsDoc : result) {
                        count++;
                        broadcasts.add(0, new Broadcast(broadcastsDoc.getData()));
                    }
                    Log.i(TAG, "" + count);
                    adapter.notifyItemInserted(0);
                } else {
                    Log.d(TAG, "Error getting broadcasts: ", task.getException());
                }
            }
        });
    }
}