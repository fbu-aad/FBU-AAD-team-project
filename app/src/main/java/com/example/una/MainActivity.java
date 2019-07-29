package com.example.una;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.una.NavigationDrawerActivities.DonationHistoryActivity;
import com.example.una.fragments.HomePageFragment;
import com.example.una.fragments.ImpactFragment;
import com.example.una.fragments.MapsViewFragment;
import com.example.una.fragments.QuickDonateFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawerLayout) DrawerLayout drawerLayout;
    @BindView(R.id.nvDrawer) NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // telling android to use the toolbar as the actionbar
        setSupportActionBar(toolbar);

        setupDrawerContent(nvDrawer);
        drawerToggle = setupDrawerToggle();
        drawerLayout.addDrawerListener(drawerToggle);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        final Fragment homePageFragment = new HomePageFragment();
        final Fragment quickDonateFragment = new QuickDonateFragment();
        final Fragment mapsViewFragment = new MapsViewFragment();
        final Fragment impactFragment = new ImpactFragment();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_home_page:
                        fragment = homePageFragment;
                        break;
                    case R.id.action_quick_donate:
                        fragment = quickDonateFragment;
                        break;
                    case R.id.action_maps_view:
                        fragment = mapsViewFragment;
                        break;
                    case R.id.action_impact:
                        fragment = impactFragment;
                        break;
                    default:
                        fragment = homePageFragment;
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flMainFragments, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home_page);
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
        switch(menuItem.getItemId()) {
            case R.id.profile_item:
                Toast.makeText(this, "Profile Item is working", Toast.LENGTH_LONG).show();
                break;
            case R.id.donation_history_item:
                Toast.makeText(this, "Donation History Item is working", Toast.LENGTH_LONG).show();
                Intent donationIntent = new Intent(MainActivity.this, DonationHistoryActivity.class);
                startActivity(donationIntent);
                break;
            case R.id.notification_item:
                Toast.makeText(this, "Notifications Item is working", Toast.LENGTH_LONG).show();
                break;
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
