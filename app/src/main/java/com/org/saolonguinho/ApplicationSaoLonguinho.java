package com.org.saolonguinho;

import android.app.Application;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.org.saolonguinho.shared.models.Location;
import com.org.saolonguinho.shared.models.Objects;
import com.org.saolonguinho.shared.models.Reminder;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;

/**
 * Created by Felipe on 13/10/2016.
 */

public class ApplicationSaoLonguinho extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Objects.class);
        ParseObject.registerSubclass(Location.class);
        ParseObject.registerSubclass(Reminder.class);
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("LHKTxksPf9ndKukyNEyTohdJx17WXjbany0h4m5I")
                .server("https://parseapi.back4app.com/")
                .clientKey("C19qDjmr1knAn2bBh0anOEDKPl2yHNxEWEuRzwEI")
                .enableLocalDataStore().build());
    }
}
