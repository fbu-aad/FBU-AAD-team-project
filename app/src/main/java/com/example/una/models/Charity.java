package com.example.una.models;

import android.location.Address;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class Charity {

    // attributes
    public String ein; // federal Employer Identification Number
    public String name;
    public String description; // tag line describing the charity
    public String mission; // organization's mission statement
    public String category;
    public String cause;
    public Address donationAddress;
    public boolean isFeatured;
    // TODO - add donorList, photoUrl, verified

    // deserialize the JSON
    public static Charity fromJSON(JSONObject jsonObject) throws JSONException {
        Charity charity = new Charity();

        // extract the values from JSON
        charity.ein = jsonObject.getString("ein");
        charity.name = jsonObject.getString("charityName");
        charity.description = jsonObject.getString("tagLine");
        charity.mission = jsonObject.getString("mission");
        charity.category = jsonObject.getJSONObject("category").getString("categoryName");
        charity.cause = jsonObject.getJSONObject("cause").getString("causeName");
        charity.isFeatured = false;

        // set charity location
        if (jsonObject.has("donationAddress")) {
            JSONObject address = jsonObject.getJSONObject("donationAddress");
            charity.donationAddress = new Address(Locale.ENGLISH);
            charity.donationAddress.setAddressLine(0, address.getString("streetAddress1"));
            charity.donationAddress.setAddressLine(1, address.getString("streetAddress2"));
            charity.donationAddress.setLocality(address.getString("city"));
            charity.donationAddress.setAdminArea(address.getString("stateOrProvince"));
            charity.donationAddress.setPostalCode(address.getString("postalCode"));
            charity.donationAddress.setCountryName(address.getString("country"));
        }

        return charity;
    }
}
