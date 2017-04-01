package com.rrc.wilson.developerreference;

/**
 * Created by Wilson on 2017-03-31.
 */

class LanguageDescription {
    String name;
    int id;
    String[] urls;

    public LanguageDescription(String name, int id, String[] urls) {
        this.name = name;
        this.id = id;
        this.urls = urls;
    }

    public LanguageDescription(String name) {
        this(name, -1, null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String[] getUrls() {
        return urls;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }
}
