package com.example.una.models;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class Challenge {
    String name;
    String description;
    String imageUrl;
    int numParticipants = 0;
    Date startDate;
    Date endDate;
    long amountRaised;
    long amountTarget;
    String associatedCharityEin;
    // TODO remove after getting charity name from API
    String associatedCharityName;
    // TODO change to type Frequency
    String frequency;
    String ownerId;
    String type;
    ArrayList<String> usersAccepted;


    public Challenge(Map<String, Object> challengeFields) {
        if (challengeFields.containsKey("name")) {
            name = (String) challengeFields.get("name");
        }

        if (challengeFields.containsKey("description")) {
            description = (String) challengeFields.get("description");
        }

        if (challengeFields.containsKey("image")) {
            imageUrl = (String) challengeFields.get("image");
        }

        if (challengeFields.containsKey("users_accepted")) {
            usersAccepted = (ArrayList<String>) challengeFields.get("users_accepted");
            numParticipants = usersAccepted.size();
        }

        if (challengeFields.containsKey("start_date")) {
            Timestamp timestamp = (Timestamp) challengeFields.get("start_date");
            startDate = (Date) timestamp.toDate();
        }

        if (challengeFields.containsKey("end_date")) {
            Timestamp timestamp = (Timestamp) challengeFields.get("end_date");
            endDate = (Date) timestamp.toDate();
        }

        if (challengeFields.containsKey("amount_raised")) {
            amountRaised = (long) challengeFields.get("amount_raised");
        }

        if (challengeFields.containsKey("target")) {
            amountTarget = (long) challengeFields.get("target");
        }

        if (challengeFields.containsKey("associated_charity")) {
            associatedCharityEin = (String) challengeFields.get("associated_charity");
        }

        if (challengeFields.containsKey("associated_charity_name")) {
            associatedCharityName = (String) challengeFields.get("associated_charity_name");
        }

        // TODO change to type Frequency
        if (challengeFields.containsKey("frequency")) {
            frequency = (String) challengeFields.get("frequency");
        }

        if (challengeFields.containsKey("owner")) {
            ownerId = (String) challengeFields.get("owner");
        }

        if (challengeFields.containsKey("type")) {
            type = (String) challengeFields.get("type");
        }
    }

    public String getChallengeName() {
        return name;
    }

    public String getChallengeDescription() {
        return description;
    }

    public String getChallengeImageUrl() {
        return imageUrl;
    }

    public int getChallengeNumParticipants() {
        return numParticipants;
    }

    public Date getChallengeStartDate() {
        return startDate;
    }

    public Date getChallengeEndDate() {
        return endDate;
    }

    public long getChallengeAmountRaised() {
        return amountRaised;
    }

    public long getChallengeAmountTarget() {
        return amountTarget;
    }

    public String getChallengeAssociatedCharityEin() {
        return associatedCharityEin;
    }

    public String getChallengeAssociatedCharityName() {
        return associatedCharityName;
    }

    public String getChallengeFrequency() {
        return frequency;
    }

    public String getChallengeOwnerId() {
        return ownerId;
    }

    public String getChallengeType() {
        return type;
    }

    public ArrayList<String> getChallengeUsersAccepted() {
        return usersAccepted;
    }
}
