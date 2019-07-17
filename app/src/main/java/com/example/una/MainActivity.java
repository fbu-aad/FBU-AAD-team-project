package com.example.una;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // TODO change 1st fragment to Denize's HomePageFragment
        final Fragment homePageFragment = new QuickDonateFragment();
        final Fragment quickDonateFragment = new QuickDonateFragment();
        final Fragment mapsViewFragment = new MapsViewFragment();
        final Fragment impactFragment = new ImpactFragment();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
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
