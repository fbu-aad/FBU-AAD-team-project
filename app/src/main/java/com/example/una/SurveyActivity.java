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

    String user_id;

    // Access a Cloud Firestore instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference users = db.collection("users");
    DocumentReference userRef;

    boolean firstLoad = true;

    // map of category names to their ids
    Map<String, Integer> categories = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        ButterKnife.bind(this);

        // set categories where the key is the category name and value is its id
        categories.put(Integer.toString(R.string.text_survey_cb_animals), 1);
        categories.put(Integer.toString(R.string.text_survey_cb_arts), 2);
        categories.put(Integer.toString(R.string.text_survey_cb_education), 3);
        categories.put(Integer.toString(R.string.text_survey_cb_environment), 4);
        categories.put(Integer.toString(R.string.text_survey_cb_health), 5);
        categories.put(Integer.toString(R.string.text_survey_cb_human_services), 6);
        categories.put(Integer.toString(R.string.text_survey_cb_international), 7);
        categories.put(Integer.toString(R.string.text_survey_cb_rights), 8);
        categories.put(Integer.toString(R.string.text_survey_cb_religion), 9);
        categories.put(Integer.toString(R.string.text_survey_cb_community), 10);
        categories.put(Integer.toString(R.string.text_survey_cb_research), 11);

        // TODO make first and last name fields required

        // handle user interaction with checkboxes
        setCheckedState(cbAnimals);
        setCheckedState(cbArts);
        setCheckedState(cbCommunity);
        setCheckedState(cbEducation);
        setCheckedState(cbEnvironment);
        setCheckedState(cbHealth);
        setCheckedState(cbHumanServices);
        setCheckedState(cbInternational);
        setCheckedState(cbReligion);
        setCheckedState(cbResearch);
        setCheckedState(cbRights);
        firstLoad = false;

        // get user_id from intent
        user_id = getIntent().getStringExtra("user_id");
        // get Firestore user document
        userRef = users.document(user_id);

        // set on click listener for submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get user inputs
                final String first_name = etFirstName.getText().toString();
                final String last_name = etLastName.getText().toString();

                // add user's name to database
                Map<String, Object> user = new HashMap<>();
                user.put("first_name", first_name);
                user.put("last_name", last_name);
                userRef.update(user);

                // add user's interest areas to database
                updateInterestAreas(cbAnimals, categories.get(Integer.toString(R.string.text_survey_cb_animals)));
                updateInterestAreas(cbArts, categories.get(Integer.toString(R.string.text_survey_cb_arts)));
                updateInterestAreas(cbCommunity, categories.get(Integer.toString(R.string.text_survey_cb_community)));
                updateInterestAreas(cbEducation, categories.get(Integer.toString(R.string.text_survey_cb_education)));
                updateInterestAreas(cbEnvironment, categories.get(Integer.toString(R.string.text_survey_cb_environment)));
                updateInterestAreas(cbHealth, categories.get(Integer.toString(R.string.text_survey_cb_health)));
                updateInterestAreas(cbHumanServices, categories.get(Integer.toString(R.string.text_survey_cb_human_services)));
                updateInterestAreas(cbInternational, categories.get(Integer.toString(R.string.text_survey_cb_international)));
                updateInterestAreas(cbReligion, categories.get(Integer.toString(R.string.text_survey_cb_religion)));
                updateInterestAreas(cbResearch, categories.get(Integer.toString(R.string.text_survey_cb_research)));
                updateInterestAreas(cbRights, categories.get(Integer.toString(R.string.text_survey_cb_rights)));

                // launch MainActivity
                Intent homeActivity = new Intent(SurveyActivity.this, MainActivity.class);
                startActivity(homeActivity);
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

    private void updateInterestAreas(CheckBox cb, int category_id) {
        if (cb.isChecked()) {
            userRef.update("interest_areas", FieldValue.arrayUnion(category_id));
        }
    }
}
