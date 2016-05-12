package com.xfinity.simpsonsviewer.entity;

/**
 * Created by gmoro on 5/12/2016.
 */
public class CharacterEntity {

    private String name, description, url;

    CharacterEntity() {}

    CharacterEntity(String name, String description, String url) {
        this.name = name;
        this.description = description;
        this.url = url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }
}
