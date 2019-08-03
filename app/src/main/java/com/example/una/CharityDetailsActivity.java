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
import android.widget.ToggleButton;

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
    @BindView(R.id.tvCharityDescription) TextView tvMission;
    @BindView(R.id.fabEmail) FloatingActionButton fabEmail;
    @BindView(R.id.tvCharityLink) TextView tvCharityLink;
    @BindView(R.id.tvMoreInfo) TextView tvMoreInfo;
    @BindView(R.id.pbLoadingCharity) ProgressBar pbLoadingCharity;
    @BindView(R.id.tvCategory) TextView tvCategory;
    @BindView(R.id.tvCause) TextView tvCause;
    @BindView(R.id.tvCNLink) TextView tvCNLink;
    @BindView(R.id.ivCategory) ImageView ivCategory;
    @BindView(R.id.ivCause) ImageView ivCause;
    @BindView(R.id.tvAboutUs) TextView tvAboutUs;
    @BindView(R.id.tvCategoryLabel) TextView tvCategoryLabel;
    @BindView(R.id.tvCauseLabel) TextView tvCauseLabel;
    @BindView(R.id.fabCall) FloatingActionButton fabCall;
    @BindView(R.id.btnFollow) ToggleButton btnFollow;
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
                    tvCharityName.setVisibility(View.VISIBLE);

                    if (charity.hasDescription()) {
                        tvTagline.setText(charity.getDescription());
                        tvTagline.setVisibility(View.VISIBLE);
                    } else {
                        tvTagline.setVisibility(View.GONE);
                    }
                    if (charity.hasLink()) {
                        tvCharityLink.setText(charity.getLink());
                        tvCharityLink.setVisibility(View.VISIBLE);
                    } else {
                        tvCharityLink.setVisibility(View.GONE);
                    }
                    if (charity.hasCategory()) {
                        tvCategory.setText(charity.getCategory());
                        tvCategory.setVisibility(View.VISIBLE);
                        ivCategory.setVisibility(View.VISIBLE);
                        tvCategoryLabel.setVisibility(View.VISIBLE);

                        String categoryImage = charity.getCategoryImageURL();
                        Glide.with(getApplicationContext())
                                .load(categoryImage)
                                .into(ivCategory);
                    } else {
                        tvCategory.setVisibility(View.GONE);
                        ivCategory.setVisibility(View.GONE);
                        tvCategoryLabel.setVisibility(View.GONE);
                    }
                    if (charity.hasCause()) {
                        tvCause.setText(charity.getCause());
                        tvCause.setVisibility(View.VISIBLE);
                        ivCause.setVisibility(View.VISIBLE);
                        tvCauseLabel.setVisibility(View.VISIBLE);

                        String causeImage = charity.getCauseImageURL();
                        Glide.with(getApplicationContext())
                                .load(causeImage)
                                .into(ivCause);
                    } else {
                        tvCause.setVisibility(View.GONE);
                        ivCause.setVisibility(View.GONE);
                        tvCauseLabel.setVisibility(View.GONE);
                    }
                    if (charity.hasMission()) {
                        tvMission.setText(charity.getMission());
                        tvMission.setVisibility(View.VISIBLE);
                        tvAboutUs.setVisibility(View.VISIBLE);
                    } else {
                        tvMission.setVisibility(View.GONE);
                        tvAboutUs.setVisibility(View.GONE);
                    }
                    tvMoreInfo.setVisibility(View.VISIBLE);
                    ivCharityImage.setVisibility(View.VISIBLE);
                    fabEmail.setVisibility(View.VISIBLE);
                    fabCall.setVisibility(View.VISIBLE);
                    btnFollow.setVisibility(View.VISIBLE);

                    tvCNLink.setVisibility(View.VISIBLE);



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

                    pbLoadingCharity.setVisibility(View.GONE);

                } catch (JSONException e) {
                    Log.e("CharityDetailsActivity", "Failed to parse response", e);
                }
            }
        });
    }
}