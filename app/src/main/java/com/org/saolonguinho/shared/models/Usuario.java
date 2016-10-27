package com.org.saolonguinho.shared.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Felipe on 26/10/2016.
 */
@ParseClassName("Usuario")
public class Usuario extends ParseObject {

    public final String EMAIL = "email";
    public final String NAME = "name";

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
