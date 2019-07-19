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

    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
    }

    public String getChallengeDescription() {
        return challengeDescription;
    }

    public void setChallengeDescription(String challengeDescription) {
        this.challengeDescription = challengeDescription;
    }
}
