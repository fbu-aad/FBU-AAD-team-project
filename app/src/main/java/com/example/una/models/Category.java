package com.example.una.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Category {

    // attributes
    public String categoryID;
    public String categoryName;
    public String categoryImage;

    public Category(JSONObject jsonObject) throws JSONException {
        categoryID = jsonObject.getString("categoryID");
        categoryName = jsonObject.getString("categoryName");
        categoryImage = jsonObject.getString("image");
    }

    public Category(String id, String name, String imageUrl) {
        this.categoryID = id;
        this.categoryName = name;
        this.categoryImage = imageUrl;
    }
}
