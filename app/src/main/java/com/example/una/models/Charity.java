package com.example.una.models;

import android.location.Address;
import org.json.JSONException;
import org.json.JSONObject;
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
    boolean isFeatured;

    public Charity() {}

    // deserialize the JSON
    public Charity(JSONObject jsonObject) throws JSONException {
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
