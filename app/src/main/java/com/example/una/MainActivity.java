package com.example.una;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.una.fragments.HomePageFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // TODO change 1st fragment to Denize's HomePageFragment
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

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent logOutIntent = new Intent(context, LoginActivity.class);
                        startActivity(logOutIntent);
                    }
                });
    }
}
