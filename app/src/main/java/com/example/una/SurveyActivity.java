package com.example.una;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SurveyActivity extends AppCompatActivity {

    // find fields
    @BindView(R.id.tvCompleteProfile) TextView tvCompleteProfile;
    @BindView(R.id.tvInfo) TextView tvInfo;
    @BindView(R.id.tvFirstName) TextView tvFirstName;
    @BindView(R.id.tvLastName) TextView tvLastName;
    @BindView(R.id.tvCauseAreas) TextView tvCauseAreas;
    @BindView(R.id.tvCauseAreaInfo) TextView tvCauseAreaInfo;

    @BindView(R.id.etFirstName) EditText etFirstName;
    @BindView(R.id.etLastName) EditText etLastName;

    @BindView(R.id.cbAnimals) CheckBox cbAnimals;
    @BindView(R.id.cbArts) CheckBox cbArts;
    @BindView(R.id.cbCommunity) CheckBox cbCommunity;
    @BindView(R.id.cbEducation) CheckBox cbEducation;
    @BindView(R.id.cbEnvironment) CheckBox cbEnvironment;
    @BindView(R.id.cbHealth) CheckBox cbHealth;
    @BindView(R.id.cbHumanServices) CheckBox cbHumanServices;
    @BindView(R.id.cbInternational) CheckBox cbInternational;
    @BindView(R.id.cbReligion) CheckBox cbReligion;
    @BindView(R.id.cbResearch) CheckBox cbResearch;
    @BindView(R.id.cbRights) CheckBox cbRights;

    @BindView(R.id.btnSubmit) Button btnSubmit;

    String userId;

    // Access a Cloud Firestore instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference users = db.collection("users");
    DocumentReference userRef;

    boolean firstLoad = true;
    ArrayList<CheckBox> cbCategories = new ArrayList<>();

    // map of category names to their ids
    Map<String, Integer> categories = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        ButterKnife.bind(this);

        cbCategories.addAll(Arrays.asList(cbAnimals, cbArts, cbEducation, cbEnvironment,
                cbHealth, cbHumanServices, cbInternational, cbRights, cbReligion, cbCommunity, cbResearch));

        // set categories where the key is the category name and value is its id
        for (int i = 0; i < cbCategories.size(); i++) {
            categories.put(cbCategories.get(i).getText().toString(), i+1);
        }

        // handle user interaction with checkboxes
        for (CheckBox cbCategory : cbCategories) {
            setCheckedState(cbCategory);
        }
        firstLoad = false;

        // get user_id from intent
        userId = getIntent().getStringExtra("user_id");
        // get Firestore user document
        userRef = users.document(userId);

        // set on click listener for submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add user's interest areas to database
                for (CheckBox cbCategory : cbCategories) {
                    String categoryName = cbCategory.getText().toString();
                    int categoryId = categories.get(categoryName);
                    updateInterestAreas(cbCategory, categoryId);
                }

                // get user inputs
                final String firstName = etFirstName.getText().toString();
                final String lastName = etLastName.getText().toString();

                // first and last name fields are required
                if (firstName.isEmpty()) {
                    etFirstName.setError("Required");
                } else if (lastName.isEmpty()) {
                    etLastName.setError("Required");
                } else {
                    // add user's name to database
                    Map<String, Object> user = new HashMap<>();
                    user.put("first_name", firstName);
                    user.put("last_name", lastName);
                    userRef.update(user);

                    // launch MainActivity
                    Intent homeActivity = new Intent(SurveyActivity.this, MainActivity.class);
                    startActivity(homeActivity);
                }
            }
        });
    }

    private void setCheckedState(CheckBox cb) {
        if (cb.isChecked()) {
            cb.setChecked(false);
        } else if (!firstLoad) {
            cb.setChecked(true);
        }
    }

    private void updateInterestAreas(CheckBox cb, int categoryId) {
        if (cb.isChecked()) {
            userRef.update("interest_areas", FieldValue.arrayUnion(categoryId));
        }
    }
}
