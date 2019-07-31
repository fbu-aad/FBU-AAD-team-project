package com.example.una;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.una.models.Broadcast;
import com.example.una.models.Charity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CharityCreateBroadcastActivity extends AppCompatActivity {

    @BindView(R.id.tvCharityName) TextView tvCharityName;
    @BindView(R.id.tvMessagePrompt) TextView tvMessagePrompt;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.etMessage) EditText etMessage;
    @BindView(R.id.postBtn) Button postBtn;

    FirestoreClient client;
    Charity charity;
    private final String TAG = "CharityCreateBroadcast";
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_create_broadcast);

        ButterKnife.bind(this);

        client = new FirestoreClient();
        charity = Parcels.unwrap(getIntent().getParcelableExtra("charity"));
        context = this;

        // TODO: allow the user to pick their profile picture
        tvCharityName.setText(charity.getName());
        tvMessagePrompt.setText(getString(R.string.broadcast_message_prompt));
        etMessage.setHint(getString(R.string.broadcast_message_hint));

        Glide.with(context)
                .load("https://picsum.photos" + "/120")
                .apply(RequestOptions.circleCropTransform())
                .into(ivProfileImage);
    }

    @OnClick(R.id.postBtn)
    public void createBroadcast() {
        String message = etMessage.getText().toString();

        if (message.isEmpty()) {
            etMessage.setError("Required");
        } else {
            ProgressDialog pd = new ProgressDialog(context);
            pd.setTitle("Posting...");
            pd.setMessage("Please wait.");
            pd.setCancelable(false);
            pd.show();

            Map<String, Object> broadcast = new HashMap<>();

            broadcast.put(getString(R.string.firestore_broadcast_message_field), message);
            broadcast.put(getString(R.string.firestore_charity_ein_field), charity.getEin());
            broadcast.put(getString(R.string.firestore_charity_name_field), charity.getName());

            client.createNewBroadcast(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i(TAG, String.format("Post by %s succeeded", charity.getName()));
                    pd.dismiss();

                    Intent goBackHomeIntent = new Intent(context, CharityHomeActivity.class);
                    goBackHomeIntent.putExtra("charity", Parcels.wrap(charity));
                    startActivity(goBackHomeIntent);
                    finish();
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG, String.format("Post by %s failed", charity.getName()));
                    pd.dismiss();
                    Toast.makeText(context, "Post failed", Toast.LENGTH_SHORT).show();
                }
            }, Broadcast.POST, PrivacySetting.PUBLIC, broadcast);
        }
    }
}
