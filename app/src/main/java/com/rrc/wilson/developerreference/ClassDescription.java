package com.rrc.wilson.developerreference;

/**
 * <i>ClassDescription</i> represents the fundamental properties of a class.
 * Individual languages have differences, which is why this class is abstract.
 *
 * <pre>
 * Created by Wilson on 2017-03-22.
 *
 * Revisions
 * Wilson       2017-03-20      Created
 * Wilson       2017-04-20      Finalized and commented
 * </pre>
 */
public abstract class ClassDescription {
    // Properties
    private String className, language, nameSpace;
    private String[] urls;

    /**
     * Bare minimum constructor
     * @param className Name of the class
     * @param language Name of the language
     */
    public ClassDescription(String className, String language) {
        this(className, language, null, null);
    }

    /**
     * Full arg constructor, recommended
     * @param className Name of the class
     * @param language Name of the language
     * @param nameSpace The namespace that the class belongs to, such as Java.util in java
     * @param urls An array of urls that point to documentation for the class
     */
    public ClassDescription(String className, String language, String nameSpace, String[] urls) {
        this.className = className;
        this.language = language;
        this.nameSpace = nameSpace;
        this.urls = urls;
    }

    /**
     * Gets the class name
     * @return The class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Gets the language
     * @return The language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Get the name space
     * @return The name space
     */
    public String getNameSpace() {
        return nameSpace;
    }

    /**
     * Sets the name space
     * @param nameSpace The new name space
     */
    protected void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    /**
     * Gets the urls
     * @return The urls
     */
    public String[] getUrls() {
        return urls;
    }

    /**
     * Sets the urls
     * @param urls The new urls
     */
    public void setUrls(String[] urls) {
        this.urls = urls;
    }
}