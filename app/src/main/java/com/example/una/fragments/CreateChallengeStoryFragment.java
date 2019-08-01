package com.example.una.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.una.Broadcast;
import com.example.una.FirestoreClient;
import com.example.una.PrivacySetting;
import com.example.una.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class CreateChallengeStoryFragment extends Fragment {

    private OnButtonClickListener mOnButtonClickListener;

    public interface OnButtonClickListener{
        void onButtonClicked(View view);
    }

    @BindView(R.id.ibBack) ImageButton ibBack;
    @BindView(R.id.etTitle) EditText etTitle;
    @BindView(R.id.etAbout) EditText etAbout;
    @BindView(R.id.btnCreateChallenge) Button btnCreateChallenge;

    HashMap<String, Object> challenge = new HashMap<>();
    HashMap<String, Object> broadcast = new HashMap<>();
    private FirestoreClient client;
    private final String TAG = "CreateChallenge";
    private String userName;
    public static final String PREFERENCES = "ChallengePreferences";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_create_challenge_story, container, false);
        mOnButtonClickListener = (OnButtonClickListener) getContext();
        ButterKnife.bind(this, rootView);
        client = new FirestoreClient();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnButtonClickListener.onButtonClicked(v);
            }
        });

        // write to challenges collection and broadcasts collection
        btnCreateChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //        Bundle bundle = getArguments();
                SharedPreferences preferences = getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

//        if (bundle != null) {
                if (preferences != null) {
//            String associated_charity_ein = bundle.getString("associated_charity_ein");
//            double goalAmount = bundle.getDouble("goal_amount");
//            String endDate = bundle.getString("end_date");
//            String frequency = bundle.getString("frequency");
//            boolean matching = bundle.getBoolean("matching");

                    String associated_charity_ein = preferences.getString("associated_charity_ein", null);
                    double goalAmount = preferences.getLong("goal_amount", 0);
                    String endDate = preferences.getString("end_date", null);
                    String frequency = preferences.getString("frequency", null);
                    boolean matching = preferences.getBoolean("matching", true);

                    // get end date object
                    Date date = null;
                    try {
                        date = getEndDate(endDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    challenge.put("associated_charity", associated_charity_ein);
                    challenge.put("target", goalAmount);
                    challenge.put("end_date", date);
                    challenge.put("frequency", frequency);
                    if (matching) {
                        challenge.put("type", "matching");
                    }

                    // get donor name
                    client.getCurrentUserName(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    userName = document.get("first_name") + " " + document.get("last_name");
                                }
                            }
                        }
                    });
                }

                challenge.put("name", etTitle.getText().toString());
                challenge.put("description", etAbout.getText().toString());

                boolean validTitle = true;
                if (etTitle.getText().toString().isEmpty()) {
                    etTitle.setError("Enter a title");
                    validTitle = false;
                }

                boolean validDesc = true;
                if (etAbout.getText().toString().isEmpty()) {
                    etAbout.setError("Enter a description");
                    validDesc = false;
                }

                if (validDesc && validTitle) {
                    // write to challenges collection
                    client.createNewChallenge(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            Log.i(TAG, "Challenge created successfully!");

                            // return result to calling activity
                            Intent resultData = new Intent();
                            getActivity().setResult(RESULT_OK, resultData);
                            getActivity().finish();
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "Failed to create challenge!");
                        }
                    }, challenge);

                    // write to broadcasts collection
                    broadcast.put("user_name", userName);
                    broadcast.put("challenge_name", etTitle.getText().toString());
                    client.createNewBroadcast(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }, Broadcast.NEW_CHALLENGE, PrivacySetting.PUBLIC, broadcast);
                }
            }
        });
    }

    public Date getEndDate(String endDate) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = dateFormat.parse(endDate);
        return date;
    }
}
