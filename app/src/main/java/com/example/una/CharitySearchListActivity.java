package com.example.una;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.una.models.Charity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class CharitySearchListActivity extends AppCompatActivity {

    private RecyclerView rvCharities;
    private CharitySearchAdapter charitySearchAdapter;
    private CharityNavigatorClient client;
    private ArrayList<Charity> charities = new ArrayList<Charity>();;

    // the parameter name for the page size
    public final static String API_PAGE_SIZE_PARAM = "pageSize";
    // the parameter name for the search query
    public final static String API_SEARCH_PARAM = "search";
    public final static String TAG = "CharitySearchList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new CharityNavigatorClient(this);
        setContentView(R.layout.activity_charity_search_list);

        charitySearchAdapter = new CharitySearchAdapter(this, charities);

        rvCharities = (RecyclerView) findViewById(R.id.rvCharities);
        rvCharities.setAdapter(charitySearchAdapter);
        rvCharities.setLayoutManager(new LinearLayoutManager(this));
        rvCharities.addItemDecoration(new DividerItemDecoration(rvCharities.getContext(), DividerItemDecoration.VERTICAL));
        String query = getIntent().getStringExtra("query");
        fetchCharities(query);
        setTitle(query);
    }

    private void fetchCharities(String query) {
        RequestParams params = new RequestParams();
        params.put(API_PAGE_SIZE_PARAM, 10);
        params.put(API_SEARCH_PARAM, query);
        // TODO stretch - add search filter and more params
        client.getOrganizations(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    if (response != null) {
                        for (int i = 0; i < response.length(); i++) {
                            Charity charity = new Charity(response.getJSONObject(i));
                            charities.add(charity);
                            Log.i(TAG, String.format("Loaded %s charities", charities.size()));
                        }
                        charitySearchAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}