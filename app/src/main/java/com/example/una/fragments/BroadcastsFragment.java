package com.example.una.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.una.BroadcastsAdapter;
import com.example.una.FirestoreClient;
import com.example.una.R;
import com.example.una.ScrollListener.EndlessRecyclerViewScrollListener;
import com.example.una.models.Broadcast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BroadcastsFragment extends Fragment {
    private final static int BROADCASTS_PER_PAGE_QUERY = 10;

    private EndlessRecyclerViewScrollListener scrollListener;

    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @BindView(R.id.rvNotifications) RecyclerView rvNotifications;
    DocumentSnapshot lastVisibleBroadcast;
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

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                fetchBroadcasts();
                swipeContainer.setRefreshing(false);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvNotifications.setLayoutManager(layoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(lastVisibleBroadcast != null) {
                    client.getBroadcastsAfterFirstTime(lastVisibleBroadcast, BROADCASTS_PER_PAGE_QUERY, new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            loadMoreData(documentSnapshots);
                        }
                    });
                }
            }
        };
        rvNotifications.addOnScrollListener(scrollListener);
    }

    // fetch broadcasts for user here
    private void fetchBroadcasts() {
        // get broadcasts from Firestore and create a new Broadcast object for each one
        client.getBroadcastsFirstTime(BROADCASTS_PER_PAGE_QUERY, new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                loadMoreData(queryDocumentSnapshots);
            }
        });
    }

    private void loadMoreData(QuerySnapshot documentSnapshots) {
        for(QueryDocumentSnapshot broadcastDoc : documentSnapshots) {
            broadcasts.add(new Broadcast(broadcastDoc.getData(), broadcastDoc.getId()));
        }
        adapter.notifyDataSetChanged();
        if(documentSnapshots.size() > 0) {
            lastVisibleBroadcast = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
        }
        else {
            lastVisibleBroadcast = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.clear();
        fetchBroadcasts();
        swipeContainer.setRefreshing(false);
    }
}