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
import com.example.una.models.Donation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private static final String TAG = "NotificationsAdapter";
    private List<Donation> mOtherUserDonations;
    FirestoreClient client;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivFriend)
        ImageView ivFriend;
        @BindView(R.id.tvFriendName)
        TextView tvFriendName;
        @BindView(R.id.tvCharityName)
        TextView tvCharityName;
        @BindView(R.id.tvTimeStamp)
        TextView tvTimestamp;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public NotificationsAdapter(List<Donation> donations) {
        mOtherUserDonations = donations;
        client = new FirestoreClient();
    }

    @NonNull
    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View notificationItemView = inflater.inflate(R.layout.notification_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(notificationItemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.ViewHolder holder, int position) {
        Donation donation = mOtherUserDonations.get(position);
        SimpleDateFormat sfd = new SimpleDateFormat("MM/dd/yy");
        holder.tvTimestamp.setText(sfd.format(donation.getTimestamp().toDate()));
        client.findCharityByEIN(donation.getRecipient(), new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot charityDoc : task.getResult()) {
                        // querying to find which ein fields match in charity_users collection
                        holder.tvCharityName.setText((String) charityDoc.get("name"));
                    }
                } else {
                    Log.d(TAG, "Error getting challengeDocs: ", task.getException());
                }
            }
        });

        client.findUserWhereIDEquals(donation.getDonorId(), new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String fullName = documentSnapshot.get("first_name") + " " + documentSnapshot.get("last_name");
                holder.tvFriendName.setText(fullName);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "cant get user's full name", e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOtherUserDonations.size();
    }
}