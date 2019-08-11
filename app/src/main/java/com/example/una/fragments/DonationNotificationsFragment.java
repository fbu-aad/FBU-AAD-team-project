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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.una.FirestoreClient;
import com.example.una.R;
import com.example.una.adapters.NotificationsAdapter;
import com.example.una.models.Donation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DonationNotificationsFragment extends Fragment {
    @BindView(R.id.rvNotifications) RecyclerView rvNotifications;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    public final static String TAG = "NotificationsFragment";
    FirestoreClient client;
    ArrayList<Donation> notifications;
    NotificationsAdapter adapter;

    public static DonationNotificationsFragment newInstance() {
        DonationNotificationsFragment yourDonations = new DonationNotificationsFragment();
        return yourDonations;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        ButterKnife.bind(this, view);
        notifications = new ArrayList<>();
        adapter = new NotificationsAdapter(notifications);
        rvNotifications.setAdapter(adapter);
        client = new FirestoreClient();
        fetchUsers();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                fetchUsers();
                adapter.addAll(notifications);
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
        // divides each item within in the recyclerView with a horizontal line
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvNotifications.getContext(), layoutManager.getOrientation());
        rvNotifications.addItemDecoration(dividerItemDecoration);
    }

    // fetch donations for user here
    private void fetchUsers() {
        // get donations from current user from Firestore and create a new Donation object for each one
        client.getDonations(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot donationDoc : task.getResult()) {
                        if ((!client.getCurrentUser().getUid().equals(donationDoc.getData().get("donor_id"))) && (client.getCurrentUser().getDisplayName() != null) ) {
                            notifications.add(new Donation(donationDoc.getData()));
                            Log.i("Donation ID", donationDoc.getData().get("donor_id").toString());
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting donor ID", task.getException());
                        }
                    }
                    Log.i("Current_logged_in_ID", client.getCurrentUser().getUid());
                }
            }
        });
    }
}