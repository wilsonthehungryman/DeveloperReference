package com.rrc.wilson.developerreference;

/**
 * LanguageDescription represents a description of a single language
 *
 * <pre>
 * Created by Wilson on 2017-03-31.
 *
 * Revisions
 * Wilson       2017-03-31      Created
 * Wilson       2017-04-20      Finalized and commented
 * </pre>
 */
class LanguageDescription {

    private String name;
    private int id, supported;
    private String[] urls;

    /**
     * The bare minimum constructor
     * Recommended for inserting into the language table
     * @param name The name of the language
     */
    public LanguageDescription(String name) {
        this(name, -1, null, 0);
    }

    /**
     * The full and recommended constructor
     * Meant primarily to hold data selected from the language table
     * @param name The language name
     * @param id The language id
     * @param urls The urls/domain of the language documentation
     * @param supported Whether this language is supported or not
     */
    public LanguageDescription(String name, int id, String[] urls, int supported) {
        this.name = name;
        this.id = id;
        this.urls = urls;
        this.supported = supported;
    }

    /**
     * Gets the name
     * @return The language name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name
     * @param name The new language name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the id (db primary key)
     * @return The language id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id
     * @param id The new id
     */
    public void setId(int id) {
        this.id = id;
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

    /**
     * Gets the supported boolean
     * @return True if the language is supported
     */
    public boolean supported(){ return supported > 0; }
}
