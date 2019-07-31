package com.example.una.models;

import com.google.firebase.firestore.Query;

import java.util.Map;

public class Broadcast {
    public static final String POST = "post";
    public static final String DONATION = "donation";
    public static final String CHALLENGE_DONATION = "challenge_donation";
    public static final String NEW_CHALLENGE = "new_challenge";

    String charityName;
    String message;

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
    }

    public String getCharityName() {
        return charityName;
    }

    public String getMessage() {
        return message;
    }

}
