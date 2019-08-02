package com.example.una;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.una.models.Charity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class CharityDetailsActivity extends AppCompatActivity {
    @BindView(R.id.ivCharityImage) ImageView ivCharityImage;
    @BindView(R.id.tvCharityName) TextView tvCharityName;
    @BindView(R.id.tvTagline) TextView tvTagline;
    @BindView(R.id.tvCharityDescription) TextView tvCharityDescription;
    @BindView(R.id.fabEmail) FloatingActionButton fabEmail;
    @BindView(R.id.tvCharityLink) TextView tvCharityLink;
    @BindView(R.id.tvMoreInfo) TextView tvMoreInfo;
    @BindView(R.id.pbLoadingCharity) ProgressBar pbLoadingCharity;
    @BindView(R.id.tvAboutUs) TextView tvAboutUs;
    String ein;
    CharityNavigatorClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_details);
        ButterKnife.bind(this);
        ein = getIntent().getStringExtra("ein");
        getCharityInfo(ein);

        fabEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "CharityDetailsActivity -> I am working", Toast.LENGTH_LONG).show();
            }
        });
    }

    // gets current charity's info based on eid value
    private void getCharityInfo(String ein) {
        RequestParams params = new RequestParams();
        client = new CharityNavigatorClient(CharityDetailsActivity.this);
        client.getCharityInfo(params, ein, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] header, JSONObject response) {
                try {
                    Charity charity = new Charity(response);
                    tvCharityName.setText(charity.getName());
                    tvTagline.setText(charity.getDescription());
                    tvCharityLink.setText(charity.getLink());



                    ivCharityImage.setVisibility(View.VISIBLE);
                    tvCharityName.setVisibility(View.VISIBLE);
                    tvTagline.setVisibility(View.VISIBLE);
                    tvCharityLink.setVisibility(View.VISIBLE);
                    tvMoreInfo.setVisibility(View.VISIBLE);
                    tvAboutUs.setVisibility(View.VISIBLE);
                    fabEmail.setVisibility(View.VISIBLE);
                    pbLoadingCharity.setVisibility(View.GONE);
                } catch (JSONException e) {
                    Log.e("CharityDetailsActivity", "Failed to parse response", e);
                }
            }
        });
    }
}