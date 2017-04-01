package com.rrc.wilson.developerreference;

/**
 * Created by Wilson on 2017-03-22.
 */

public abstract class ClassDescription {
    protected String className, language;
    protected String[] urls;

    public ClassDescription(String className, String language) {
        this(className, language, null);
    }

    public ClassDescription(String className, String language, String[] urls) {
        this.className = className;
        this.language = language;
        this.urls = urls;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String[] getUrls() {
        return urls;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }
}