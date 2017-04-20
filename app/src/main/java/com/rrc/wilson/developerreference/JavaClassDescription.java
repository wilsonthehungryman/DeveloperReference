package com.rrc.wilson.developerreference;

/**
 * JavaClassDescription represents a Java class.
 *
 * <pre>
 * Created by Wilson on 2017-03-22.
 *
 * Revisions
 * Wilson       2017-03-22      Created
 * Wilson       2017-04-20      Finalized and commented
 * </pre>
 */
public class JavaClassDescription extends ClassDescription {

    private String packageName;

    /**
     * Minimum arg constructor
     * @param className The class name
     */
    public JavaClassDescription(String className) {
        this(className, null, null);
    }

    /**
     * Basic constructor
     * @param className The class name
     * @param packageName The package name
     */
    public JavaClassDescription(String className, String packageName) {
        this(className, packageName, null);
    }

    /**
     * The full arg constructor, recommended
     * @param className The class name
     * @param packageName The package name
     * @param urls The array of urls
     */
    public JavaClassDescription(String className, String packageName, String[] urls) {
        super(className, "JAVA", packageName, urls);
        this.packageName = packageName;
    }

    /**
     * Gets the package name
     * @return The package name, same as getNameSpace
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Sets the package name
     * @param packageName The new package name
     */
    public void setPackageName(String packageName) {
        setNameSpace(packageName);
        this.packageName = packageName;
    }
}
