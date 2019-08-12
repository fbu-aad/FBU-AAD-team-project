package com.example.una;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class CharityNavigatorClient {

    private Context context;
    // the base URL for the API
    public final static String API_BASE_URL = "https://api.data.charitynavigator.org/v2";
    // the parameter name for the API key
    public final static String API_KEY_PARAM = "app_key";
    // the parameter name for the API application ID
    public final static String API_ID_PARAM = "app_id";
    public final static String TAG = "CharityNavigatorClient";

    AsyncHttpClient client;

    public CharityNavigatorClient(Context context) {
        this.client = new AsyncHttpClient();
        this.context = context;
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }

    public void getCategories(RequestParams params, JsonHttpResponseHandler handler) {
        buildParams(params);
        String url = getApiUrl("/Categories");
        client.get(url, params, handler);
    }

    public void getOrganizations(RequestParams params, JsonHttpResponseHandler handler) {
        buildParams(params);
        String url = getApiUrl("/Organizations");
        client.get(url, params, handler);
    }

    public void getFeatured(RequestParams params, JsonHttpResponseHandler handler) {
        buildParams(params);
        String url = getApiUrl("/Lists/" + context.getString(R.string.featured_list_id));
        client.get(url, params, handler);
    }

    public void getRecommended(RequestParams params, JsonHttpResponseHandler handler) {
        buildParams(params);
        String url = getApiUrl("/Lists/" + context.getString(R.string.recommended_list_id));
        client.get(url, params, handler);
    }

    private RequestParams buildParams(RequestParams params) {
        params.put(API_KEY_PARAM, context.getString(R.string.cn_api_key));
        params.put(API_ID_PARAM, context.getString(R.string.cn_app_id));
        return params;
    }

    public void getCharityInfo(RequestParams params, String ein, JsonHttpResponseHandler handler) {
        buildParams(params);
        String url = getApiUrl("/Organizations/" + ein);
        client.get(url, params, handler);
    }
}
