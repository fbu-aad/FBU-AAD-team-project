package com.example.una.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.FirestoreClient;
import com.example.una.R;
import com.example.una.TimeFormatHelper;
import com.example.una.models.Donation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DonationsHistoryAdapter extends RecyclerView.Adapter<DonationsHistoryAdapter.ViewHolder> {

    private List<Donation> mDonations;
    Context context;
    FirestoreClient client;
    private String TAG = "DonationsHistoryAdapter";

    // create ViewHolder class

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivCharityImage) ImageView ivCharityImage;
        @BindView(R.id.tvCharityName) TextView tvCharityName;
        @BindView(R.id.tvDonationAmount) TextView tvDonationAmount;
        @BindView(R.id.tvTimestamp) TextView tvTimestamp;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // pass in the Donations array in the constructor
    public DonationsHistoryAdapter(List<Donation> donations) {
        // passing in donations array from DonationHistoryActivity
        mDonations = donations;
        client = new FirestoreClient();
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        // this will inflate each row with our XML layout for that item
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View donationItemView = inflater.inflate(R.layout.item_donation_history, parent, false);
        final ViewHolder viewHolder = new ViewHolder(donationItemView);
        // TODO -- implement onclicklisteners here
        return viewHolder;
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        // gets the viewHolder from onCreateViewHolder and populates it with data for each UI element
        // get the data according to position
        Donation donation = mDonations.get(position);
        // populate the views according to this data
        viewHolder.tvDonationAmount.setText("$" + donation.getDonationAmount().toString());
        String donationCreationTime = TimeFormatHelper.getDateStringFromDate(donation.getTimestamp().toDate());
        viewHolder.tvTimestamp.setText(donationCreationTime);
        client.findCharityByEIN(donation.getRecipient(), new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot charityDoc : task.getResult()) {
                        // querying to find which ein fields match in charity_users collection
                        viewHolder.tvCharityName.setText((String) charityDoc.get("name"));
                    }
                } else {
                    Log.d(TAG, "Error getting challengeDocs: ", task.getException());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDonations.size();
    }
}