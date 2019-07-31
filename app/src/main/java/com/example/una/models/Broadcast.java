package com.example.una.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import java.util.Map;

public class Broadcast {
    public static final String POST = "post";
    public static final String DONATION = "donation";
    public static final String CHALLENGE_DONATION = "challenge_donation";
    public static final String NEW_CHALLENGE = "new_challenge";

    String charityName;
    String challengeName;
    String message;
    String userName;
    String type;
    String privacy;
    Timestamp timestamp;

    public Broadcast(String charityName, String message) {
        this.charityName = charityName;
        this.message = message;
    }

    public Broadcast(Map<String, Object> broadcastFields) {
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

        if (broadcastFields.containsKey("username")) {
            userName = (String) broadcastFields.get("username");
        }
    }

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
}
