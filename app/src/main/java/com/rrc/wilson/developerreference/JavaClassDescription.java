package com.rrc.wilson.developerreference;

/**
 * Created by Wilson on 2017-03-22.
 */

public class JavaClassDescription extends ClassDescription {

    private String packageName;

    public JavaClassDescription(String className) {
        this(className, null);
    }

    public JavaClassDescription(String className, String packageName) {
        this(className, packageName, null);
    }

    public JavaClassDescription(String className, String packageName, String[] urls) {
        super(className, "JAVA", packageName, urls);
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
