package com.example.una;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.una.NavigationDrawerActivities.DonationHistoryActivity;
import com.example.una.fragments.BroadcastsFragment;
import com.example.una.fragments.ChallengesFragment;
import com.example.una.fragments.DonationNotificationsFragment;
import com.example.una.fragments.ExplorePageFragment;
import com.example.una.fragments.QuickDonateFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private String fullName;
    private ActionBarDrawerToggle drawerToggle;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawerLayout) DrawerLayout drawerLayout;
    @BindView(R.id.nvDrawer) NavigationView nvDrawer;
    FirestoreClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // instantiate a new Firestore client
        client = new FirestoreClient();
        View header = nvDrawer.getHeaderView(0);
        TextView navDrawerHeaderName = header.findViewById(R.id.tvCurrentUsername);
        fullName = client.getCurrentUser().getDisplayName();
        navDrawerHeaderName.setText(fullName);
        // telling android to use the toolbar as the actionbar
        setSupportActionBar(toolbar);
        setupDrawerContent(nvDrawer);
        drawerToggle = setupDrawerToggle();
        drawerLayout.addDrawerListener(drawerToggle);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment explorePageFragment = new ExplorePageFragment();
        final Fragment quickDonateFragment = new QuickDonateFragment();
        final Fragment broadcastsFragment = new BroadcastsFragment();
        final Fragment challengesFragment = new ChallengesFragment();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_explore:
                        fragment = explorePageFragment;
                        break;
                    case R.id.action_quick_donate:
                        fragment = quickDonateFragment;
                        break;
                    case R.id.action_notifications:
                        fragment = broadcastsFragment;
                        break;
                    case R.id.action_challenges:
                        fragment = challengesFragment;
                        break;
                    default:
                        fragment = quickDonateFragment;
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flMainFragments, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_quick_donate);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = new DonationNotificationsFragment();

        switch(menuItem.getItemId()) {
            case R.id.profile_item:
                break;
            case R.id.donation_history_item:
                Intent donationIntent = new Intent(MainActivity.this, DonationHistoryActivity.class);
                startActivity(donationIntent);
                break;
            case R.id.notification_item:
                fragmentManager.beginTransaction().replace(R.id.flMainFragments, fragment).commit();
                break;
            case R.id.sign_out_item:
                AuthUI.getInstance()
                        .signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent signOutIntent = new Intent(MainActivity.this, UnaStartupActivity.class);
                                startActivity(signOutIntent);
                                finish();
                            }
                        });
            default:
                Toast.makeText(this, "Default Item", Toast.LENGTH_LONG).show();
        }

        menuItem.setChecked(true);
        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,  R.string.drawer_close);
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
