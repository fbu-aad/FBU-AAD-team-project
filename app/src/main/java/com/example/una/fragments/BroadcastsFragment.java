package com.example.una.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.BroadcastsAdapter;
import com.example.una.FirestoreClient;
import com.example.una.R;
import com.example.una.adapters.NotificationsAdapter;
import com.example.una.models.Broadcast;
import com.example.una.models.Donation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BroadcastsFragment extends Fragment {
    @BindView(R.id.rvNotifications)
    RecyclerView rvNotifications;

    public final static String TAG = "BroadcastsFragment";
    FirestoreClient client;
    ArrayList<Broadcast> broadcasts;
    BroadcastsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        ButterKnife.bind(this, view);
        broadcasts = new ArrayList<>();
        adapter = new BroadcastsAdapter(broadcasts);
        rvNotifications.setAdapter(adapter);
        client = new FirestoreClient();
        fetchBroadcasts();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvNotifications.setLayoutManager(layoutManager);
    }

    // fetch donations for user here
    private void fetchBroadcasts() {
        // get donations from current user from Firestore and create a new Donation object for each one
        client.getBroadcasts(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    broadcasts.clear();
                    QuerySnapshot result = task.getResult();
                    for (QueryDocumentSnapshot broadcastsDoc : result) {
                        broadcasts.add(new Broadcast(broadcastsDoc.getData()));
                    }
                    adapter.notifyItemInserted(broadcasts.size() - 1);
                } else {
                    Log.d(TAG, "error getting user broadcasts");
                }
            }
        });
    }
}