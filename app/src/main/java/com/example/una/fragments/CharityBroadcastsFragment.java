package com.example.una.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.una.BroadcastsAdapter;
import com.example.una.CharityCreateBroadcastActivity;
import com.example.una.CharityHomeActivity;
import com.example.una.FirestoreClient;
import com.example.una.R;
import com.example.una.models.Broadcast;
import com.example.una.models.Charity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CharityBroadcastsFragment extends Fragment {
    @BindView(R.id.rvBroadcasts) RecyclerView rvBroadcasts;
    @BindView(R.id.fabCreate) FloatingActionButton fabCreateBroadcast;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    FirestoreClient client;
    private final String TAG = "CharityBroadcastPage";

    ArrayList<Broadcast> broadcasts;
    BroadcastsAdapter adapter;

    Charity charity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charity_broadcasts, container, false);

        ButterKnife.bind(this, view);

        charity = ((CharityHomeActivity) getActivity()).getCharity();

        broadcasts = new ArrayList<>();
        adapter = new BroadcastsAdapter(broadcasts);
        rvBroadcasts.setAdapter(adapter);
        client = new FirestoreClient();

        getBroadcasts();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBroadcasts();
                swipeContainer.setRefreshing(false);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvBroadcasts.setLayoutManager(layoutManager);
    }

    @OnClick(R.id.fabCreate)
    public void createBroadcast() {
        Intent createBroadcastIntent = new Intent(getContext(), CharityCreateBroadcastActivity.class);
        createBroadcastIntent.putExtra("charity", Parcels.wrap(charity));
        startActivity(createBroadcastIntent);
    }

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
                        broadcasts.add(new Broadcast(broadcastsDoc.getData(), broadcastsDoc.getId()));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error getting broadcasts: ", task.getException());
                }
            }
        });
    }
}
