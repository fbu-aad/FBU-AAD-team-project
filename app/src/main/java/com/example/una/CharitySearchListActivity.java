package com.example.una;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.ScrollListener.EndlessRecyclerViewScrollListener;
import com.example.una.models.Charity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class CharitySearchListActivity extends AppCompatActivity {

    private RecyclerView rvCharities;
    private CharitySearchAdapter charitySearchAdapter;
    private CharityNavigatorClient client;
    private ArrayList<Charity> charities = new ArrayList<Charity>();
    private EndlessRecyclerViewScrollListener scrollListener;
    private String sQuery;
    private String sCategoryID;

    // the parameter name for the page size
    public final static String API_PAGE_SIZE_PARAM = "pageSize";
    // the parameter name for the page number
    public final static String API_PAGE_NUM_PARAM = "pageNum";
    // the parameter name for the search query
    public final static String API_SEARCH_PARAM = "search";
    public final static String API_CATEGORY_ID_PARAM = "categoryID";
    public final static String TAG = "CharitySearchList";

    @BindView(R.id.pbLoading) ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new CharityNavigatorClient(this);
        setContentView(R.layout.activity_charity_search_list);
        ButterKnife.bind(this);

        charitySearchAdapter = new CharitySearchAdapter(this, charities);

        rvCharities = (RecyclerView) findViewById(R.id.rvCharities);
        rvCharities.setAdapter(charitySearchAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvCharities.setLayoutManager(linearLayoutManager);
        rvCharities.addItemDecoration(new DividerItemDecoration(rvCharities.getContext(), DividerItemDecoration.VERTICAL));
        sQuery = getIntent().getStringExtra("query");
        sCategoryID = getIntent().getStringExtra("categoryID");
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // triggered only when new data needs to be appended to the list
                fetchCharities(sQuery, page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvCharities.addOnScrollListener(scrollListener);
        setSupportActionBar(findViewById(R.id.toolbar));

        fetchCharities(sQuery, 1);
        setTitle(sQuery);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.menu_charity_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        // make the search bar cover the title
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                rvCharities.setVisibility(View.GONE);
                pbLoading.setVisibility(ProgressBar.VISIBLE);

                sQuery = query;
                charities.clear();
                charitySearchAdapter.notifyDataSetChanged();
                fetchCharities(sQuery, 1);
                setTitle(sQuery);
                scrollListener.resetState();

                // reset SearchView
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_search:
                // handle this selection
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchCharities(String query, int page) {
        RequestParams params = new RequestParams();
        params.put(API_PAGE_SIZE_PARAM, 10);

        if(sCategoryID != null) {
            params.put(API_CATEGORY_ID_PARAM, sCategoryID);
        }
        else {
            params.put(API_SEARCH_PARAM, sQuery);
        }
        if (page != 1) {
            params.put(API_PAGE_NUM_PARAM, page);
        }
        client.getOrganizations(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            try {
                if (response != null) {
                    for (int i = 0; i < response.length(); i++) {
                        Charity charity = new Charity(response.getJSONObject(i), getApplicationContext());
                        charities.add(charity);
                    }
                    Log.i(TAG, String.format("Loaded %s charities", charities.size()));
                    charitySearchAdapter.notifyDataSetChanged();

                    rvCharities.setVisibility(View.VISIBLE);
                    pbLoading.setVisibility(ProgressBar.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        });
    }
}