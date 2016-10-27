package com.org.saolonguinho.shared.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Created by Felipe on 26/10/2016.
 */
@ParseClassName("Location")
public class Location extends ParseObject {
    private static final String DATE = "date";
    private static final String DESCRIPTION = "description" ;

    public Date getDateLocation() {
        return getDate(DATE);
    }

    public void setDateLocation(Date date) {
        put(DATE, date);
    }

    public String getDescription(){
        return getString(DESCRIPTION);
    }
    public void setDescription(String description){
        put(DESCRIPTION,description);
    }
}
