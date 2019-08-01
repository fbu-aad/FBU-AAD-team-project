package com.example.una.NavigationDrawerActivities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DonationHistoryActivity extends AppCompatActivity {

    @BindView(R.id.rvDonations)
    RecyclerView rvDonations;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private EndlessRecyclerViewScrollListener scrollListener;
    public final static String TAG = "DonationHistoryActivity";
    FirestoreClient client;
    List<Donation> donations; // passes to my adapter class
    DonationsHistoryAdapter adapter; // what handles the item in the RecyclerView
    DocumentSnapshot lastVisible;
    Query next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set action bar title
        setTitle(R.string.navigation_drawer_donation_history);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_history);
        ButterKnife.bind(this);
        client = new FirestoreClient();
        donations = new ArrayList<>(); // currently creating new donation list
        adapter = new DonationsHistoryAdapter(donations); // stop and go to adapter
        setSupportActionBar(toolbar);
        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // sets up the rows in the recycler view
        // taking all item XML's and organize them in a linear fashion
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // telling recyclerView to use linearLayout
        rvDonations.setLayoutManager(linearLayoutManager);
        rvDonations.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvDonations.getContext(), linearLayoutManager.getOrientation());
        rvDonations.addItemDecoration(dividerItemDecoration);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                next.get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot documentSnapshots) {
                                loadMoreData(documentSnapshots);
                            }
                        });
            }
        };
        rvDonations.addOnScrollListener(scrollListener);
        // FETCH DONATIONS FROM FIREBASE
        fetchDonations();
    }

    private void loadMoreData(QuerySnapshot documentSnapshots) {
        for (QueryDocumentSnapshot donationDoc : documentSnapshots) {
            if(donationDoc.get("donor_id").toString().equals(client.getCurrentUser().getUid())) {
                donations.add(new Donation(donationDoc.getData()));
            }
        }
        adapter.notifyDataSetChanged();
        // Get the last visible document
        lastVisible = documentSnapshots.getDocuments()
                .get(documentSnapshots.size() - 1);

        next = client.getDonationsCollection()
                .orderBy("time", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(10);
    }

    // fetch donations for user here
    private void fetchDonations() {
        // Construct query for first 25 cities, ordered by population
        Query first = client.getDonationsCollection()
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(10);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        loadMoreData(documentSnapshots);
                    }
                });
    }
}