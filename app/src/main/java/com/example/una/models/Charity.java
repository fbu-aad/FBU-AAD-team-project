package com.example.una.models;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import org.parceler.Parcel;

@Parcel
public class Charity {

    // attributes must be public for Parcel to work properly
    String ein; // federal Employer Identification Number
    String name;
    String description; // tag line describing the charity
    String mission; // organization's mission statement
    String category;
    String cause;
    String link;
    String charityNavigatorURL;
    String categoryImageURL;
    String causeImageURL;
    Address donationAddress;
    Address mailingAddress;
    boolean isFeatured;
    private static Context context;

    public Charity() {}

    // deserialize the JSON
    public Charity(JSONObject jsonObject, Context context) throws JSONException {
        this.context = context;

        // extract the values from JSON

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
            categoryImageURL = jsonObject.getJSONObject("category").getString("image");
        }
        if (jsonObject.has("cause")) {
            cause = jsonObject.getJSONObject("cause").getString("causeName");
            causeImageURL = jsonObject.getJSONObject("cause").getString("image");
        }
        if (jsonObject.has("websiteURL")) {
            link = jsonObject.getString("websiteURL");
        }
        if (jsonObject.has("charityNavigatorURL")) {
            charityNavigatorURL = jsonObject.getString("charityNavigatorURL");
        }
        isFeatured = false;

        // set charity donation address
        if (jsonObject.has("donationAddress")) {
            this.donationAddress = getCharityLocation(jsonObject, "donationAddress");
        }

        // set charity mailing address
        if (jsonObject.has("mailingAddress")) {
            this.mailingAddress = getCharityLocation(jsonObject, "mailingAddress");
        }
    }

    private Address getCharityLocation(JSONObject jsonObject, String typeAddress) throws JSONException {
        Address addressObj = null;
        Geocoder coder = new Geocoder(context, Locale.getDefault());

        JSONObject address = jsonObject.getJSONObject(typeAddress);
        List<Address> listAddress;

        String streetAddress1 = address.getString("streetAddress1");
        String streetAddress2 = address.getString("streetAddress2");
        String city = address.getString("city");
        String stateOrProvince = address.getString("stateOrProvince");

        String strAddress = "";
        if (streetAddress1 != null && !streetAddress1.equals("null")) {
            strAddress += streetAddress1;
        }
        if (streetAddress2 != null && !streetAddress2.equals("null")) {
            strAddress += " " + streetAddress2;
        }
        if (city != null && !city.equals("null")) {
            strAddress += " " + city;
        }
        if (stateOrProvince != null && !stateOrProvince.equals("null")) {
            strAddress += " " + stateOrProvince;
        }

        try {
            listAddress = coder.getFromLocationName(strAddress, 1);
            if (!listAddress.isEmpty())
            {
                addressObj = listAddress.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (typeAddress.equals("mailingAddress")) {
            addressObj.setFeatureName("Mailing Address");
        } else {
            addressObj.setFeatureName("Donation Address");
        }

        return addressObj;
    }

    public Charity(String ein, String name) {
        this.ein = ein;
        this.name = name;
    }

    public String getEin() { return ein; }

    public String getName() { return name; }

    public boolean hasDescription() { return description != null && !description.equals("null"); }

    public String getDescription() { return description; }

    public boolean hasMission() { return mission != null && !mission.equals("null"); }

    public String getMission() { return mission; }

    public boolean hasCategory() { return category != null; }

    public String getCategory() { return category; }

    public boolean hasCause() { return cause != null; }

    public String getCause() { return cause; }

    public boolean hasDonationAddress() { return donationAddress != null; }

    public Address getDonationAddress() { return donationAddress; }

    public boolean hasMailingAddress() { return mailingAddress != null; }

    public Address getMailingAddress() { return mailingAddress; }

    public boolean isFeatured() { return isFeatured; }

    public boolean hasLink() { return link != null && !link.equals("null"); }

    public String getLink() { return link; }

    public String getCharityNavigatorURL() {
        return charityNavigatorURL;
    }

    public String getCategoryImageURL() {
        return categoryImageURL;
    }

    public String getCauseImageURL() {
        return causeImageURL;
    }
}
