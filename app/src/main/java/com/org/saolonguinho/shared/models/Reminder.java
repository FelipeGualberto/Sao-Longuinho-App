package com.org.saolonguinho.shared.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Created by Felipe on 26/10/2016.
 */
@ParseClassName("Reminder")
public class Reminder extends ParseObject {
    public final String DATE = "date";

    public Date getDateReminder() {
        return getDate(DATE);
    }

    public void setDate(Date date) {
        put(DATE, date);
    }
}
