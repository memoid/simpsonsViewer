package com.xfinity.simpsonsviewer.util;

/**
 * Created by gmoro on 5/5/2016.
 */
public class Converter {

    public String convertName(String description) {

        return description.split(" - ")[0];

    }

    public String convertDescription(String description) {
        return description.split(" - ")[1];
    }

}
