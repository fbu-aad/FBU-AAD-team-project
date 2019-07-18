package com.example.una;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.una.models.Category;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class DummyFragment extends Fragment {

    // the base URL for the API
    public final static String API_BASE_URL = "https://api.data.charitynavigator.org/v2";
    // the parameter name for the API key
    public final static String API_KEY_PARAM = "app_key";
    // the parameter name for the API application ID
    public final static String API_ID_PARAM = "app_id";
    public final static String TAG = "DummyFragment";


    protected RecyclerView rvCategories;
    protected CategoryAdapter adapter;
    protected ArrayList<Category> categories;
    AsyncHttpClient client;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dummy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvCategories = view.findViewById(R.id.rvCategories);
        client = new AsyncHttpClient();

        // create the data source
        categories = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        adapter = new CategoryAdapter(categories);
        rvCategories.setAdapter(adapter);
        rvCategories.setLayoutManager(linearLayoutManager);

        getCategories();
    }

    private void getCategories() {
        // create the url
        String url = API_BASE_URL + "/Categories";

        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.cn_api_key));
        params.put(API_ID_PARAM, getString(R.string.cn_app_id));

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        Category category = new Category(response.getJSONObject(i));
                        categories.add(category);
                        adapter.notifyItemInserted(categories.size()-1);
                        Log.i(TAG, String.format("Loaded %s categories", categories.size()));

                    }
                } catch (JSONException e) {
                    logError("Failed to parse categories", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from categories endpoint", throwable, true);
            }
        });
    }

    // handle errors, log and alert user
    private void logError(String message, Throwable error, boolean alertUser) {
        // always log the error
        Log.e(TAG, message, error);
        // alert the user to avoid silent errors
        if (alertUser) {
            // show a long toast with the error message
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
