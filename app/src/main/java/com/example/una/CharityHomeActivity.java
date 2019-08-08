package com.example.una;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.una.fragments.CharityBroadcastsFragment;
import com.example.una.fragments.CharityChallengesFragment;
import com.example.una.models.Charity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.parceler.Parcels;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CharityHomeActivity extends AppCompatActivity {

    private static final String TAG = "CharityHomeActivity";
    Charity charity;

    @BindView(R.id.bottomNavigationCharityHome) BottomNavigationView charityBottomNavigation;
    @BindView(R.id.nvDrawerCharity) NavigationView nvDrawerCharity;
    @BindView(R.id.drawerLayout) DrawerLayout drawerLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_home);
        ButterKnife.bind(this);

        charity = Parcels.unwrap(getIntent().getParcelableExtra("charity"));

        View header = nvDrawerCharity.getHeaderView(0);
        TextView navDrawerHeaderName = header.findViewById(R.id.tvCurrentUsername);
        navDrawerHeaderName.setText(charity.getName());

        setSupportActionBar(toolbar);
        setupDrawerContent(nvDrawerCharity);
        drawerToggle = setupDrawerToggle();
        drawerLayout.addDrawerListener(drawerToggle);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment broadcastsFragment = new CharityBroadcastsFragment();
        final Fragment challengesFragment = new CharityChallengesFragment();

        charityBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_broadcasts:
                        fragment = broadcastsFragment;
                        break;
                    case R.id.action_challenges:
                        fragment = challengesFragment;
                        break;
                    default:
                        fragment = broadcastsFragment;
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flCharityFragments, fragment).commit();
                return true;
            }
        });
        charityBottomNavigation.setSelectedItemId(R.id.action_broadcasts);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.profile_item:
                break;
            case R.id.sign_out_item:
                AuthUI.getInstance()
                        .signOut(getApplicationContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent signOutIntent = new Intent(getApplicationContext(), UnaStartupActivity.class);
                                startActivity(signOutIntent);
                                finish();
                            }
                        });
                break;
            default:
                Log.d(TAG, "Default item clicked in drawer");
        }

        menuItem.setChecked(true);
        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    public Charity getCharity() {
        return charity;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
}