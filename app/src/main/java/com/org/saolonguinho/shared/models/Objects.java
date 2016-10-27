package com.org.saolonguinho.shared.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.File;

/**
 * Created by Felipe on 26/10/2016.
 */
@ParseClassName("Objects")
public class Objects extends ParseObject {

    public final String NAME = "name";
    public final String IMAGE = "image";

    public String getNameObject(){
        return getString(NAME);
    }
    public void setNameObject(String name){
        put(NAME,name);
    }

    public ParseFile getImageObject(){
        return getParseFile(IMAGE);
    }
    public void setImageObject(File file){
        put(IMAGE,file);
    }
}
