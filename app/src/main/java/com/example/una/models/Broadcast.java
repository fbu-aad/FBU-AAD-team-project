package com.example.una.models;

import com.google.firebase.Timestamp;

import org.parceler.Parcel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

@Parcel
public class Broadcast {
    public static final String POST = "post";
    public static final String DONATION = "donation";
    public static final String CHALLENGE_DONATION = "challenge_donation";
    public static final String NEW_CHALLENGE = "new_challenge";

    private String charityName;
    private String challengeName;
    private String message;
    private String userName;
    private String donor;
    private String type;
    private String privacy;
    private Timestamp timestamp;
    private String frequency;
    private String challengeId;
    private String charityEin;
    private boolean userType;
    String uid;
    ArrayList<String> comments;
    ArrayList<String> likes;

    public Broadcast() {}

    public Broadcast(String charityName, String message) {
        this.charityName = charityName;
        this.message = message;
    }

    public Broadcast(Map<String, Object> broadcastFields, String uid) {
        this.uid = uid;

        if (broadcastFields.containsKey("charity_name")) {
            charityName = (String) broadcastFields.get("charity_name");
        }

        if (broadcastFields.containsKey("message")) {
            message = (String) broadcastFields.get("message");
        }

        if (broadcastFields.containsKey("type")) {
            type = (String) broadcastFields.get("type");
        }

        if (broadcastFields.containsKey("privacy")) {
            privacy = (String) broadcastFields.get("privacy");
        }

        if (broadcastFields.containsKey("time")) {
            timestamp = (Timestamp) broadcastFields.get("time");
        }

        if (broadcastFields.containsKey("challenge_name")) {
            challengeName = (String) broadcastFields.get("challenge_name");
        }

        if (broadcastFields.containsKey("user_name")) {
            userName = (String) broadcastFields.get("user_name");
        }

        if (broadcastFields.containsKey("challenge_id")) {
            challengeId = (String) broadcastFields.get("challenge_id");
        }

        if (broadcastFields.containsKey("donor")) {
            donor = (String) broadcastFields.get("donor");
        }

        if (broadcastFields.containsKey("frequency")) {
            frequency = (String) broadcastFields.get("frequency");
        }

        if(broadcastFields.containsKey("charity_ein")) {
            charityEin = (String) broadcastFields.get("charity_ein");
        }

        if (broadcastFields.containsKey("user_type")) {
            userType = (boolean) broadcastFields.get("user_type");
        }

        if (broadcastFields.containsKey("comments")) {
            comments = (ArrayList<String>) broadcastFields.get("comments");
        }

        if (broadcastFields.containsKey("liked_by")) {
            likes = (ArrayList<String>) broadcastFields.get("liked_by");
        }
    }

    public String getUid() { return uid; }

    public String getCharityName() {
        return charityName;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getPrivacy() {
        return privacy;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public String getUserName() {
        return userName;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public String getDonor() {
        return donor;
    }

    public String getCharityEin() {
        return charityEin;
    }

    public void setDonor(String donor) {
        this.donor = donor;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean getUserType() {
        return userType;
    }

    public ArrayList<String> getComments() { return comments; }

    public ArrayList<String> getLikes() { return likes; }
}
