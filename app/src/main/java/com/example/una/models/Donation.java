package com.example.una.models;

import com.google.firebase.Timestamp;

import java.util.Map;

public class Donation {
    String id; // Donation's document ID
    Timestamp timestamp;
    Number donationAmount;
    String recipient;
    String frequency;
    String donorId;


    public Donation() {
        // Default constructor required for calls to DataSnapshot.getValue(Donation.class)
    }

    public Donation(Map<String, Object> donationFields) {
        if (donationFields.containsKey("amount")) {
            donationAmount = (Number) donationFields.get("amount");
        }

        if (donationFields.containsKey("time")) {
            timestamp = (Timestamp) donationFields.get("time");
        }

        if (donationFields.containsKey("recipient")) {
            recipient = (String) donationFields.get("recipient");
        }

        if (donationFields.containsKey("donor_id")) {
            donorId = (String) donationFields.get("donor_id");
        }

        if (donationFields.containsKey("frequency")) {
            frequency = (String) donationFields.get("frequency");
        }
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Number getDonationAmount() {
        return donationAmount;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getDonorId() {
        return donorId;
    }
}
