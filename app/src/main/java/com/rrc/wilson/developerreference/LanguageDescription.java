package com.rrc.wilson.developerreference;

/**
 * Created by Wilson on 2017-03-31.
 */

class LanguageDescription {
    private String name;
    private int id, supported;
    private String[] urls;

    public LanguageDescription(String name, int id, String[] urls, int supported) {
        this.name = name;
        this.id = id;
        this.urls = urls;
        this.supported = supported;
    }

    public LanguageDescription(String name) {
        this(name, -1, null, 0);
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

    public boolean supported(){ return supported > 0; }
}
