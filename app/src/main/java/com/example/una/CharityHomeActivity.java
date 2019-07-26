package com.example.una;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.os.Bundle;
import android.util.Log;

import com.example.una.models.Broadcast;
import com.example.una.models.Challenge;
import com.example.una.models.Charity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CharityHomeActivity extends AppCompatActivity {

    Charity charity;
    @BindView(R.id.rvNotifications) RecyclerView rvNotifications;

    private final String TAG = "CharityHomeActivity";
    FirestoreClient client;

    ArrayList<Object> notifications;
    ArrayList<Broadcast> broadcasts;
    ArrayList<Challenge> challenges;
    NotificationsComplexRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_home);
        ButterKnife.bind(this);

        charity = Parcels.unwrap(getIntent().getParcelableExtra("charity"));
        notifications = new ArrayList<>();
        challenges = new ArrayList<>();
        broadcasts = new ArrayList<>();
        adapter = new NotificationsComplexRecyclerViewAdapter(notifications);
        rvNotifications.setAdapter(adapter);
        client = new FirestoreClient();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvNotifications.setLayoutManager(layoutManager);

        getBroadcasts();
        getCharitySpecificChallenges();
    }

    // get the charities broadcasts
    private void getBroadcasts() {
        client.getCharityBroadcasts(charity.getEin(), new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot broadcastsDoc : task.getResult()) {
                        broadcasts.add(new Broadcast(broadcastsDoc.getData()));
                    }
                    notifications.addAll(broadcasts);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error getting notifications: ", task.getException());
                }
            }
        });
    }

    // query the charity's challenges
    private void getCharitySpecificChallenges() {
        client.getCharitySpecificChallenges(charity.getEin(), new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        challenges.add(new Challenge(document.getData()));
                    }
                    notifications.addAll(challenges);
                    // TODO sort the arrays by time so that the broadcasts arent always above the challenges
                    adapter.notifyDataSetChanged();
                }
             }
        });
    }
}
