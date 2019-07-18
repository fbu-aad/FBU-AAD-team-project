package com.example.una;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.una.fragments.HomePageFragment;
import com.example.una.fragments.ImpactFragment;
import com.example.una.fragments.MapsViewFragment;
import com.example.una.fragments.QuickDonateFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
