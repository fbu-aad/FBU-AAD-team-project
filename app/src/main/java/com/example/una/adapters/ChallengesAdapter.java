package com.example.una.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestOptions;
import com.example.una.FirestoreClient;
import com.example.una.GlideApp;
import com.example.una.models.Challenge;
import com.example.una.R;
import com.example.una.Viewholders.ChallengeViewHolder;
import com.example.una.Viewholders.StreaksViewHolder;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.ArrayList;

import static com.example.una.utils.ChallengeViewsUtil.getStrNumParticipants;
import static com.example.una.utils.ChallengeViewsUtil.getStrProgress;
import static com.example.una.utils.ChallengeViewsUtil.getStrTimeLeft;
import static com.example.una.utils.ChallengeViewsUtil.handleClickJoinBtn;
import static com.example.una.utils.ChallengeViewsUtil.setJoinBtn;
import static com.example.una.utils.ChallengeViewsUtil.setPbProgress;
import static com.example.una.utils.ChallengeViewsUtil.setTvOwnerRecipientInfo;

public class ChallengesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // The challenges to display in your RecyclerView
    private ArrayList<Challenge> challenges;
    public final static String TAG = "StreaksComplexRVAdapter";
    Context context;

    // Firestore client
    FirestoreClient client;
    StorageReference storageReference;

    public ChallengesAdapter(ArrayList<Challenge> challenges) {
        this.challenges = challenges;
    }

    @Override
    public int getItemCount() {
        return this.challenges.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        client = new FirestoreClient();
        storageReference = FirebaseStorage.getInstance().getReference();
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View itemViewChallenge = inflater.inflate(R.layout.challenge_item_layout, viewGroup, false);
        viewHolder = new ChallengeViewHolder(itemViewChallenge, challenges, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ChallengeViewHolder challengeViewHolder = (ChallengeViewHolder) viewHolder;
        configureChallengeViewHolder(challengeViewHolder, position);
    }

    private void configureChallengeViewHolder(ChallengeViewHolder vhChallenge, int position) {
        Challenge challenge = (Challenge) challenges.get(position);
        if (challenge != null) {
            // set challenge title text view
            vhChallenge.getTvChallengeTitle().setText(challenge.getChallengeName());
            // set challenge owner and associated charity text view
            setTvOwnerRecipientInfo(vhChallenge.getTvChallengeOwnerRecipientInfo(), challenge);

            // set participants text view
            vhChallenge.getTvNumParticipants().setText(getStrNumParticipants(challenge));

            // set text view for time left to start or end date of challenge
            vhChallenge.getTvTimeLeft().setText(getStrTimeLeft(challenge));

            // set challenge progress text view
            vhChallenge.getTvProgress().setText(getStrProgress(challenge));

            // set progress bar
            setPbProgress(vhChallenge.getPbProgress(), challenge);

            String photoFile = challenge.getPictureFilepath();
            ImageView ivChallengeImage = vhChallenge.getIvChallengeImage();

            if (photoFile != null && !photoFile.isEmpty()) {
                StorageReference pathReference = storageReference.child(photoFile);
                GlideApp.with(context)
                        .applyDefaultRequestOptions(RequestOptions.placeholderOf(R.color.una_grey))
                        .load(pathReference)
                        .centerCrop()
                        .into(ivChallengeImage);
            } else {
                Glide.with(context)
                        .load("https://picsum.photos" + "/400/300")
                        .apply(RequestOptions.placeholderOf(R.color.una_grey))
                        .apply(RequestOptions.centerCropTransform())
                        .into(vhChallenge.getIvChallengeImage());

            }

            ToggleButton btnJoin = vhChallenge.getBtnJoin();
            setJoinBtn(client, btnJoin, null, challenge);

            handleClickJoinBtn(client, btnJoin, null, challenge, vhChallenge.getTvNumParticipants(),
                    context);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        challenges.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(ArrayList<Challenge> list) {
        challenges.addAll(list);
        notifyDataSetChanged();
    }

}