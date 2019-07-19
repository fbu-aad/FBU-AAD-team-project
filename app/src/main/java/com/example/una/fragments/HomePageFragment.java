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

import com.example.una.CategoryAdapter;
import com.example.una.R;
import com.example.una.RecyclerViewModels.HomeFragmentSection;
import com.example.una.RecyclerViewModels.HorizontalModel;
import com.example.una.adapters.VerticalRecyclerViewAdapter;
import com.example.una.models.Category;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HomePageFragment extends Fragment {
    public RecyclerView verticalRecyclerView;
    public VerticalRecyclerViewAdapter adapter;
    public CategoryAdapter categoryAdapter;
    ArrayList<HomeFragmentSection> arrayListVertical;
    protected ArrayList<Object> categories;

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
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        arrayListVertical = new ArrayList<>();
        adapter = new VerticalRecyclerViewAdapter(getContext(), arrayListVertical);
        verticalRecyclerView.setAdapter(adapter);
        categories = new ArrayList<>();

        client = new AsyncHttpClient();
        setData();
    }

    private void setData() {
        // featured charities
        HomeFragmentSection featuredModel = new HomeFragmentSection();
        featuredModel.setTitle("Featured");
        ArrayList<Object> featured = new ArrayList<>();
        for (int j = 0; j < 5; j++) {
            HorizontalModel horizontalModel = new HorizontalModel();
            horizontalModel.setDescription("Description" + j);
            horizontalModel.setName("Name: " + j);
            featured.add(horizontalModel);
        }
        featuredModel.setArrayList(featured);
        featuredModel.setViewType(HomeFragmentSection.CHARITY_LIST_TYPE);

        arrayListVertical.add(featuredModel);

        // recommended charities
        HomeFragmentSection recommendedModel = new HomeFragmentSection();
        recommendedModel.setTitle("Recommended for You");
        ArrayList<Object> recommended = new ArrayList<>();
        for (int j = 0; j < 5; j++) {
            HorizontalModel horizontalModel = new HorizontalModel();
            horizontalModel.setDescription("Description" + j);
            horizontalModel.setName("Name: " + j);
            recommended.add(horizontalModel);
        }
        recommendedModel.setArrayList(recommended);
        recommendedModel.setViewType(HomeFragmentSection.CHARITY_LIST_TYPE);

        arrayListVertical.add(recommendedModel);

        // categories
        HomeFragmentSection categoriesModel = new HomeFragmentSection();
        categoriesModel.setTitle("Explore");
        getCategories();
        categoriesModel.setArrayList(categories);
        categoriesModel.setViewType(HomeFragmentSection.CATEGORIES_LIST_TYPE);

        arrayListVertical.add(categoriesModel);

        adapter.notifyDataSetChanged();
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
                        Log.i(TAG, String.format("Loaded %s categories", categories.size()));
                    }
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
//        return categories;
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


