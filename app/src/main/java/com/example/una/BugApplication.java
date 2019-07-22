package com.example.una;

import android.app.Application;

import com.buglife.sdk.Buglife;
import com.buglife.sdk.InvocationMethod;

public class BugApplication extends Application {
    @Override
    public void onCreate() {
        // Extended Application to use Buglife
        super.onCreate();
        Buglife.initWithEmail(this, getString(R.string.denize));
        Buglife.setInvocationMethod(InvocationMethod.SHAKE);
    }
}
