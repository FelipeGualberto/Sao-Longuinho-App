package com.org.saolonguinho;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/**
 * Created by Felipe on 13/10/2016.
 */

public class ApplicationSaoLonguinho extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("LHKTxksPf9ndKukyNEyTohdJx17WXjbany0h4m5I")
                .server("https://parseapi.back4app.com/")
                .clientKey("C19qDjmr1knAn2bBh0anOEDKPl2yHNxEWEuRzwEI")
                .enableLocalDataStore().build());
    }
}
