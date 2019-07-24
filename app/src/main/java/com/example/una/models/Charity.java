package com.example.una.models;

import android.location.Address;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import org.parceler.Parcel;

@Parcel
public class Charity {

    // attributes
    private String ein; // federal Employer Identification Number
    private String name;
    private String description; // tag line describing the charity
    private String mission; // organization's mission statement
    private String category;
    private String cause;
    private Address donationAddress;
    private String link;
    private boolean isFeatured;

    public Charity() {}

    // deserialize the JSON
    public Charity(JSONObject jsonObject) throws JSONException {
        // extract the values from JSON
        ein = jsonObject.getString("ein");
        name = jsonObject.getString("charityName");
        ein = jsonObject.getString("ein");
        name = jsonObject.getString("charityName");
        if (jsonObject.has("tagLine")) {
            description = jsonObject.getString("tagLine");
        }
        if (jsonObject.has("mission")) {
            mission = jsonObject.getString("mission");
        }
        if (jsonObject.has("category")) {
            category = jsonObject.getJSONObject("category").getString("categoryName");
        }
        if (jsonObject.has("cause")) {
            cause = jsonObject.getJSONObject("cause").getString("causeName");
        }
        if (jsonObject.has("websiteURL")) {
            link = jsonObject.getString("websiteURL");
        }
        isFeatured = false;

        // set charity location
        if (jsonObject.has("donationAddress")) {
            JSONObject address = jsonObject.getJSONObject("donationAddress");
            donationAddress = new Address(Locale.ENGLISH);
            donationAddress.setAddressLine(0, address.getString("streetAddress1"));
            donationAddress.setAddressLine(1, address.getString("streetAddress2"));
            donationAddress.setLocality(address.getString("city"));
            donationAddress.setAdminArea(address.getString("stateOrProvince"));
            donationAddress.setPostalCode(address.getString("postalCode"));
            donationAddress.setCountryName(address.getString("country"));
        }
    }

    public String getEin() {
        return ein;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getMission() {
        return mission;
    }

    public String getCategory() {
        return category;
    }

    public String getCause() {
        return cause;
    }

    public Address getDonationAddress() {
        return donationAddress;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public String getLink() { return link; }

}
