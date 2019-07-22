package com.example.una;

import android.app.Application;

import com.buglife.sdk.Buglife;
import com.buglife.sdk.InvocationMethod;

public class UnaApplication extends Application {
    @Override
    public void onCreate() {
        // Extended Application to use Buglife
        super.onCreate();
        Buglife.initWithEmail(this, getString(R.string.bug_report_email));
        Buglife.setInvocationMethod(InvocationMethod.SHAKE);
    }
}
