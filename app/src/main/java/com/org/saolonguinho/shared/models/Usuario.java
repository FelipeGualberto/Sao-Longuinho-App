package com.org.saolonguinho.shared.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Felipe on 26/10/2016.
 */
@ParseClassName("_User")
public class Usuario extends ParseUser {

    public final String EMAIL = "email";
    public final String NAME = "username";

    public String getNameUser() {
        return getString(NAME);
    }

    public void setNameUser(String name) {
        put(NAME, name);
    }

    public String getEmail() {
        return getString(EMAIL);
    }

    public void setEmail(String email) {
        put(EMAIL, email);
    }

}
