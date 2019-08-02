package com.example.una;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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
    @BindView(R.id.tvCategory) TextView tvCategory;
    @BindView(R.id.tvCause) TextView tvCause;
    @BindView(R.id.tvCNLink) TextView tvCNLink;
    @BindView(R.id.ivCategory) ImageView ivCategory;
    @BindView(R.id.ivCause) ImageView ivCause;
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
                    // TODO check if these fields exist
                    Charity charity = new Charity(response);
                    tvCharityName.setText(charity.getName());
                    tvTagline.setText(charity.getDescription());
                    tvCharityLink.setText(charity.getLink());
                    tvCharityDescription.setText(charity.getDescription());
                    tvCategory.setText(charity.getCategory());
                    tvCause.setText(charity.getCause());
                    tvCharityDescription.setText(charity.getMission());

                    // link to Charity Navigator URL
                    tvCNLink.setClickable(true);
                    tvCNLink.setMovementMethod(LinkMovementMethod.getInstance());
                    String cnLink = "<a href='" + charity.getCharityNavigatorURL()
                            + "'> Charity Navigator's nonprofit profile</a>";
                    if (Build.VERSION.SDK_INT >= 24) {
                        tvCNLink.setText(Html.fromHtml(cnLink, Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        tvCNLink.setText(Html.fromHtml(cnLink));
                    }

                    // set category and cause images
                    String categoryImage = charity.getCategoryImageURL();
                    Glide.with(getApplicationContext())
                            .load(categoryImage)
                            .into(ivCategory);

                    String causeImage = charity.getCauseImageURL();
                    Glide.with(getApplicationContext())
                            .load(causeImage)
                            .into(ivCause);

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