package com.example.una.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.FirestoreClient;
import com.example.una.R;
import com.example.una.ScrollListener.EndlessRecyclerViewScrollListener;
import com.example.una.adapters.DonationsHistoryAdapter;
import com.example.una.models.Donation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YourDonationHistoryFragment extends Fragment {

    @BindView(R.id.rvDonations) RecyclerView rvDonations;
    private final static int ITEMS_PER_PAGE_QUERY = 10;
    private EndlessRecyclerViewScrollListener scrollListener;
    FirestoreClient client;
    List<Donation> donations; // passes to my adapter class
    DonationsHistoryAdapter adapter; // what handles the item in the RecyclerView
    DocumentSnapshot lastVisible;
    private final static String TAG = "YourDonationHistory";

    public static YourDonationHistoryFragment newInstance() {
        YourDonationHistoryFragment yourDonations = new YourDonationHistoryFragment();
        return yourDonations;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donation_history, container, false);
        ButterKnife.bind(this, view);

        client = new FirestoreClient();
        donations = new ArrayList<>(); // currently creating new donation list
        adapter = new DonationsHistoryAdapter(donations); // stop and go to adapter

        // sets up the rows in the recycler view
        // taking all item XML's and organize them in a linear fashion
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        // telling recyclerView to use linearLayout
        rvDonations.setLayoutManager(linearLayoutManager);
        rvDonations.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvDonations.getContext(), linearLayoutManager.getOrientation());
        rvDonations.addItemDecoration(dividerItemDecoration);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (lastVisible != null) {
                    client.fetchDonationsAfterFirstTime(client.getCurrentUser().getUid(), lastVisible, ITEMS_PER_PAGE_QUERY, new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            loadMoreData(documentSnapshots);
                        }
                    });
                }
            }
        };
        rvDonations.addOnScrollListener(scrollListener);
        // FETCH DONATIONS FROM FIREBASE
        fetchDonations();

        return view;
    }

    private void loadMoreData(QuerySnapshot documentSnapshots) {
        for (QueryDocumentSnapshot donationDoc : documentSnapshots) {
            donations.add(new Donation(donationDoc.getData()));
            Log.i("client user uid", client.getCurrentUser().getUid());
        }
        adapter.notifyDataSetChanged();

        // Get the last visible document
        Log.i(TAG, String.valueOf(documentSnapshots.size()));
        if(documentSnapshots.size() > 0) {
            lastVisible = documentSnapshots.getDocuments()
                    .get(documentSnapshots.size() - 1);
        } else {
            lastVisible = null;
        }
    }

    // fetch donations for user here
    private void fetchDonations() {
        // Construct query for first 10 donations
        client.fetchDonationsFirstTime(client.getCurrentUser().getUid(), ITEMS_PER_PAGE_QUERY, new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                loadMoreData(documentSnapshots);
            }
        });
    }
}
