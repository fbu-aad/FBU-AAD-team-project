package com.example.una;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.una.models.Broadcast;
import com.example.una.models.Charity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class CharityDetailsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "CharityDetailsActivity";
    // charity details information
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
    CharityNavigatorClient cnClient;
    FirestoreClient firestoreClient;
    private GoogleMap mMap;
    private ArrayList<Address> addresses = new ArrayList<>();

    // bottom sheet information
    @BindView(R.id.rgSuggestedDonations) RadioGroup rgSuggestedDonations;
    @BindView(R.id.radio1) RadioButton radio1;
    @BindView(R.id.radio3) RadioButton radio3;
    @BindView(R.id.radio5) RadioButton radio5;
    @BindView(R.id.radio10) RadioButton radio10;
    @BindView(R.id.radioTuition) RadioButton radioTuition;
    @BindView(R.id.etCustomAmount) EditText etCustomAmount;
    @BindView(R.id.tvDonationTitle) TextView tvDonationTitle;
    @BindView(R.id.bottomSheet) ConstraintLayout bottomSheet;
    @BindView(R.id.donateBtn) Button donateBtn;
    @BindView(R.id.raise_bar) ImageView raiseBar;

    FirestoreClient client;
    Charity charity;
    BottomSheetBehavior bottomSheetBehavior;
    DecimalFormat df;
    Context context;
    Double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_details);
        ButterKnife.bind(this);
        ein = getIntent().getStringExtra("ein");

        // hide map fragment
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Fragment mapFragment = manager.findFragmentById(R.id.map);
        ft.hide(mapFragment);
        ((SupportMapFragment) mapFragment).getMapAsync(this);
        ft.commit();

        getCharityInfo(ein);

        fabEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TO DO: does nothing on click
            }
        });

        // set the behavior and data in the bottom sheet fragment
        df = new DecimalFormat("###,###,###,##0.00");
        context = this;
        client = new FirestoreClient();
        amount = 0.00;
        donateBtn.setEnabled(false);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        etCustomAmount.addTextChangedListener(new CurrencyTextWatcher(etCustomAmount, "$0.00"));
        setValueCheckedListener();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void setMap() {
        // show map fragment
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Fragment mapFragment = manager.findFragmentById(R.id.map);
        ft.show(mapFragment);
        ft.commit();

        for (Address address : addresses) {
            try {
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng)
                        .title(address.getFeatureName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    // gets current charity's info based on eid value
    private void getCharityInfo(String ein) {
        RequestParams params = new RequestParams();
        cnClient = new CharityNavigatorClient(CharityDetailsActivity.this);
        cnClient.getCharityInfo(params, ein, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] header, JSONObject response) {
                try {
                    Charity charity = new Charity(response, getApplicationContext());
                    if (charity.hasDonationAddress()) {
                        addresses.add(charity.getDonationAddress());
                    }

                    if (charity.hasMailingAddress()) {
                        addresses.add(charity.getMailingAddress());
                    }

                    setMap();

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

                    donateBtn.setEnabled(true);
                    donateBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (amount > 0.0) {
                                createNewDonation(setDonateBroadcastParams(charity), amount);
                                resetBottomSheet();
                            }
                        }
                    });
                } catch (JSONException e) {
                    Log.e("CharityDetailsActivity", "Failed to parse response", e);
                }
            }
        });
    }

    private void setValueCheckedListener() {
        rgSuggestedDonations.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.radio1:
                        etCustomAmount.setText(".24");
                        amount = 0.24;
                        break;
                    case R.id.radio3:
                        etCustomAmount.setText("3.25");
                        amount = 3.25;
                        break;
                    case R.id.radio5:
                        etCustomAmount.setText("5.49");
                        amount = 5.49;
                        break;
                    case R.id.radio10:
                        etCustomAmount.setText("13.50");
                        amount = 13.50;
                        break;
                    case R.id.radioTuition:
                        etCustomAmount.setText("25,000.00");
                        amount = 25000.00;
                        break;
                    default:
                        etCustomAmount.setText("0.00");
                        amount = 0.00;
                        break;
                }
            }
        });
    }

    private void createNewDonation(Broadcast broadcast, Double amount) {
        client.createNewDonation(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Your donation was received",
                        Toast.LENGTH_SHORT).show();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Your donation was not received :(",
                        Toast.LENGTH_SHORT).show();
            }
        }, broadcast, amount);
    }

    private void resetBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        etCustomAmount.setText("");
        rgSuggestedDonations.clearCheck();
    }

    private Broadcast setDonateBroadcastParams(Charity charity) {
        Map<String, Object> broadcastParams = new HashMap<>();
        broadcastParams.put("charity_name", charity.getName());
        broadcastParams.put("type", Broadcast.DONATION);
        broadcastParams.put("privacy", PrivacySetting.PUBLIC);
        broadcastParams.put("user_name", client.getCurrentUser().getDisplayName());
        broadcastParams.put("donor", client.getCurrentUser().getUid());
        broadcastParams.put("frequency", Frequency.SINGLE_DONATION);
        broadcastParams.put("charity_ein", charity.getEin());

        Broadcast broadcast = new Broadcast(broadcastParams);
        return broadcast;
    }
}