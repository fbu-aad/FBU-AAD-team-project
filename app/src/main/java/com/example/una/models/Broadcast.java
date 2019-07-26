package com.example.una.models;

import java.util.Map;

public class Broadcast {
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
