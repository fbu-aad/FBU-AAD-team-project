package com.example.una.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.R;
import com.example.una.RecyclerViewModels.HomeFragmentSection;
import com.example.una.adapters.VerticalRecyclerViewAdapter;
import com.example.una.models.Category;
import com.example.una.models.Charity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.example.una.models.Charity.fromJSON;

public class HomePageFragment extends Fragment {
    public RecyclerView verticalRecyclerView;
    public VerticalRecyclerViewAdapter adapter;
    ArrayList<HomeFragmentSection> arrayListVertical;

    protected ArrayList<Object> categories = new ArrayList<>();
    protected ArrayList<Object> featured = new ArrayList<>();

    // the base URL for the API
    public final static String API_BASE_URL = "https://api.data.charitynavigator.org/v2";
    // the parameter name for the API key
    public final static String API_KEY_PARAM = "app_key";
    // the parameter name for the API application ID
    public final static String API_ID_PARAM = "app_id";
    public final static String TAG = "HomePageFragment";
    AsyncHttpClient client;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        verticalRecyclerView = view.findViewById(R.id.homeRecyclerView);
        // makes verticalRecyclerView's height and width immutable no matter what is inserted or removed inside the recyclerView
        verticalRecyclerView.setHasFixedSize(true);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        arrayListVertical = new ArrayList<>();
        adapter = new VerticalRecyclerViewAdapter(getContext(), arrayListVertical);
        verticalRecyclerView.setAdapter(adapter);
        client = new AsyncHttpClient();
        setData();
    }

    private void setData() {
        // featured charities
        HomeFragmentSection featuredModel = new HomeFragmentSection();
        featuredModel.setTitle(getString(R.string.text_home_featured));
        getFeatured();
        featuredModel.setArrayList(featured);
        featuredModel.setViewType(HomeFragmentSection.CHARITY_LIST_TYPE);

        arrayListVertical.add(featuredModel);

        // categories
        HomeFragmentSection categoriesModel = new HomeFragmentSection();
        categoriesModel.setTitle(getString(R.string.text_home_explore));
        getCategories();
        categoriesModel.setArrayList(categories);
        categoriesModel.setViewType(HomeFragmentSection.CATEGORIES_LIST_TYPE);

        arrayListVertical.add(categoriesModel);

        adapter.notifyDataSetChanged();
    }

    private void getFeatured() {
        // create the URL
        String url = API_BASE_URL + "/Lists/" + getString(R.string.featured_list_id);
        RequestParams params = buildParams();

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray groupsArr = response.getJSONArray("groups");
                    JSONObject groupsObj = groupsArr.getJSONObject(0);
                    JSONArray organizations = groupsObj.getJSONArray("organizations");

                    String prevName = "";
                    for (int i = 0; i < organizations.length(); i++) {
                        JSONObject rankedCharity = organizations.getJSONObject(i);
                        Charity charity = fromJSON(rankedCharity.getJSONObject("organization"));

                        // make sure there are no duplicates in the data
                        String newName = charity.name;
                        if (!prevName.equals(newName)) {
                            featured.add(charity);
                        }
                        prevName = newName;
                    }
                    Log.i(TAG, String.format("Found %s featured charities", featured.size()));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    logError("Failed to parse featured list", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from featured endpoint", throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                logError("Failed to get data from featured endpoint", throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                logError("Failed to get data from featured endpoint", throwable);
            }
        });
    }


    private void getCategories() {
        // create the url
        String url = API_BASE_URL + "/Categories";

        RequestParams params = buildParams();

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        Category category = new Category(response.getJSONObject(i));
                        categories.add(category);
                    }
                    Log.i(TAG, String.format("Loaded %s categories", categories.size()));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    logError("Failed to parse categories", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from categories endpoint", throwable);
            }
        });
    }

    private RequestParams buildParams() {
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.cn_api_key));
        params.put(API_ID_PARAM, getString(R.string.cn_app_id));
        return params;
    }


    // handle errors, log and alert user
    private void logError(String message, Throwable error) {
        // always log the error
        Log.e(TAG, message, error);
        // alert the user to avoid silent errors
        // show a long toast with the error message
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}


