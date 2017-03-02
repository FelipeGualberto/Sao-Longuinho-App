package com.org.saolonguinho.shared.models;

import android.media.Image;
import android.widget.ImageView;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.util.Date;

/**
 * Created by Felipe on 26/10/2016.
 */
@ParseClassName("Objects")
public class Objects extends ParseObject {

    public static final String LOCATION = "location";
    public static final String USER = "user";
    public static final String NAME = "name";
    public static final String IMAGE = "image";

    public String getNameObject() {
        return getString(NAME);
    }

    public void setNameObject(String name) {
        put(NAME, name);
    }

    public ParseFile getImageObject() {
        return getParseFile(IMAGE);
    }

    public void setImageObject(File file) {
        ParseFile parsefile = new ParseFile(file);
        try {
            parsefile.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        put(IMAGE, parsefile);
    }

    public void setLocation(String description, Date date) {
        Location location = new Location();
        location.setDescription(description);
        location.setDateLocation(date);
        put(LOCATION, location);
    }

    public Location getLocation() {
        return (Location) getParseObject(LOCATION);
    }

    public void setUser(ParseUser user) {
        put(USER, user);
    }
}
