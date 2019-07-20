package com.example.una;

public class Challenge {
    String challengeTitle;
    String challengeDescription;

    public Challenge(String challengeTitle, String challengeDescription) {
        this.challengeTitle = challengeTitle;
        this.challengeDescription = challengeDescription;
    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public String getChallengeDescription() {
        return challengeDescription;
    }
}
