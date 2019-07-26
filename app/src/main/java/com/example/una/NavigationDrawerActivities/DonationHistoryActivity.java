package com.example.una.NavigationDrawerActivities;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.FirestoreClient;
import com.example.una.R;
import com.example.una.adapters.DonationsHistoryAdapter;
import com.example.una.models.Donation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DonationHistoryActivity extends AppCompatActivity {

    @BindView(R.id.rvDonations)
    RecyclerView rvDonations;

    public final static String TAG = "DonationHistoryActivity";
    FirestoreClient client;

    List<Donation> donations; // passes to my adapter class
    DonationsHistoryAdapter adapter; // what handles the item in the RecyclerView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_history);
        ButterKnife.bind(this);

        donations = new ArrayList<>(); // currently creating new donation list

        adapter = new DonationsHistoryAdapter(donations); // stop and go to adapter

        // sets up the rows in the recycler view
        // taking all item XML's and organize them in a linear fashion
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // telling recyclerView to use linearLayout
        rvDonations.setLayoutManager(linearLayoutManager);
        // giving an adapter to the recyclerView
        rvDonations.setAdapter(adapter);

        // TODO -- endless scrolling here using endless recycler view holder

        // FETCH DONATIONS FROM FIREBASE
        client = new FirestoreClient();
        // not needed to notify adapter because it's currently empty
        fetchDonations();
    }

    // fetch donations for user here
    private void fetchDonations() {
        // get all donations from Firestore and create a new Donation object for each one
        client.findDonationsByUserId(client.getCurrentUser().getUid(), new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot donationDoc : task.getResult()) {
                        donations.add(new Donation(donationDoc.getData()));
                        Log.d(TAG, donationDoc.getId() + " => " + donationDoc.getData());
                    }
                    // notifies that adapter has been changed
                    // need to notify here because at this point, the data has been added to donations list
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error getting challengeDocs: ", task.getException());
                }
            }
        });
    }
}
