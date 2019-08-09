package com.example.una.fragments;

import android.app.ProgressDialog;
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

import com.example.una.CharityNavigatorClient;
import com.example.una.CreateChallengeScreenSlideActivity;
import com.example.una.FirestoreClient;
import com.example.una.PrivacySetting;
import com.example.una.R;
import com.example.una.models.Broadcast;
import com.example.una.models.Charity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

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
    private FirestoreClient fsClient;
    private CharityNavigatorClient cnClient;
    private final String TAG = "CreateChallenge";
    private String userName;
    private String associatedCharityEin;
    private String associatedCharityName;
    private double goalAmount;
    private String endDate;
    private String frequency;
    private boolean matching;
    private boolean validDesc = true;
    private boolean validTitle = true;

    ProgressDialog pd;

    public static final String CHALLENGE_PREFERENCES = "ChallengePreferences";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_create_challenge_story, container, false);
        mOnButtonClickListener = (OnButtonClickListener) getContext();
        ButterKnife.bind(this, rootView);
        fsClient = new FirestoreClient();
        // get donor name
        userName = ((CreateChallengeScreenSlideActivity) getActivity()).getName();

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

                SharedPreferences preferences = getContext().getSharedPreferences(CHALLENGE_PREFERENCES, Context.MODE_PRIVATE);
                if (preferences != null) {
                    associatedCharityEin = preferences.getString("associated_charity_ein", null);
                    goalAmount = preferences.getLong("goal_amount", 0);
                    endDate = preferences.getString("end_date", null);
                    frequency = preferences.getString("frequency", null);
                    matching = preferences.getBoolean("matching", true);
                }

                // get end date object
                Date date = null;
                try {
                    date = getEndDate(endDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                challenge.put("associated_charity", associatedCharityEin);
                if (goalAmount != 0) {
                    challenge.put("target", goalAmount);
                }
                challenge.put("end_date", date);
                challenge.put("user_name", userName);
                challenge.put("frequency", frequency);
                if (matching) {
                    challenge.put("type", "matching");
                }

                String name = etTitle.getText().toString();
                challenge.put("name", name);
                challenge.put("description", etAbout.getText().toString());

                if (etTitle.getText().toString().isEmpty()) {
                    etTitle.setError("Enter a title");
                    validTitle = false;
                }

                if (etAbout.getText().toString().isEmpty()) {
                    etAbout.setError("Enter a description");
                    validDesc = false;
                }

                pd = new ProgressDialog(getContext());
                pd.setTitle("Creating challenge...");
                pd.setMessage("Please wait.");
                pd.setCancelable(false);

                if (validDesc && validTitle) {
                    pd.show();

                    // get associated charity name
                    RequestParams params = new RequestParams();
                    cnClient = new CharityNavigatorClient(getContext());
                    cnClient.getCharityInfo(params, associatedCharityEin, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] header, JSONObject response) {
                            try {
                                Charity charity = new Charity(response, getContext());
                                associatedCharityName = charity.getName();
                                challenge.put("associated_charity_name", associatedCharityName);

                                // write to challenges collection
                                fsClient.createNewChallenge(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Log.i(TAG, "Challenge created successfully!");
                                        Map<String, Object> broadcastFields = new HashMap<>();
                                        broadcastFields.put("charity_ein", associatedCharityEin);
                                        broadcastFields.put("donor", fsClient.getCurrentUser().getUid());
                                        broadcastFields.put("frequency", frequency);
                                        broadcastFields.put("type", Broadcast.NEW_CHALLENGE);
                                        broadcastFields.put("user_name", userName);
                                        SharedPreferences sharedPref = getContext()
                                                .getSharedPreferences(getString(R.string.preference_file_key),
                                                        Context.MODE_PRIVATE);
                                        boolean userType = sharedPref.getBoolean("user_type", getResources().getBoolean(R.bool.is_user));
                                        broadcastFields.put("user_type", userType);
                                        broadcastFields.put("challenge_name", name);
                                        broadcastFields.put("privacy", PrivacySetting.PUBLIC);
                                        broadcastFields.put("charity_name", associatedCharityName);

                                        Broadcast broadcast = new Broadcast(broadcastFields);
                                        fsClient.createNewBroadcast(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, String.format("created broadcast successfully from %s", name));
                                            }}, new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, String.format("create %s broadcast failed", name), e);
                                            }}, broadcast);
                                        // return result to calling activity
                                        pd.dismiss();
                                        Intent resultData = new Intent();
                                        getActivity().setResult(RESULT_OK, resultData);
                                        getActivity().finish();
                                    }}, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i(TAG, "Failed to create challenge!");
                                        pd.dismiss();
                                    }}, challenge);
                            } catch (JSONException e) {
                                Log.d(TAG, e.toString());
                                pd.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            Log.d(TAG, String.format("failed getting the charity for ein %s", associatedCharityEin));
                            pd.dismiss();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            Log.d(TAG, String.format("failed getting the charity for ein %s", associatedCharityEin));
                            pd.dismiss();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Log.d(TAG, String.format("failed getting the charity for ein %s", associatedCharityEin));
                            pd.dismiss();
                        }


                    });
                }
            }
        });
    }

    public Date getEndDate(String endDate) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        Date date = dateFormat.parse(endDate);
        return date;
    }
}
