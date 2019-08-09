package com.example.una.RecyclerViewModels;

import java.util.ArrayList;

public class HomeFragmentSection {
    public static final int CHARITY_LIST_TYPE = 0;
    public static final int CATEGORIES_LIST_TYPE = 1;

    String title;
    ArrayList<Object> arrayList;
    int viewType;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Object> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<Object> arrayList) {
        this.arrayList = arrayList;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
