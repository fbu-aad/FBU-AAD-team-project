package com.example.una;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.una.fragments.CreateChallengeBasicInfoFragment;
import com.example.una.fragments.CreateChallengeCoverPhotoFragment;
import com.example.una.fragments.CreateChallengeStoryFragment;

public class CreateChallengeScreenSlideActivity extends FragmentActivity {
    // number of pages (wizard steps) to show
    private static final int NUM_PAGES = 3;

    // pager widget, which handles animation and swiping
    private ViewPager mPager;

    // pager adapter
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_challenge_screen_slide);

        mPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // if at the first step, go back
            super.onBackPressed();
        } else {
            // otherwise, select the previous step
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    // pager adapter representing 3 CreateChallengeBasicInfoFragment objects
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                // basic information fragment
                return new CreateChallengeBasicInfoFragment();
            } else if (position == 1) {
                // story fragment
                return new CreateChallengeStoryFragment();
            } else {
                // upload cover photo fragment
                return new CreateChallengeCoverPhotoFragment();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
