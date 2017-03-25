package com.rrc.wilson.developerreference;

/**
 * Created by Wilson on 2017-03-22.
 */

public class JavaClassDescription extends ClassDescription {

    private String packageName;

    public JavaClassDescription(String className) {
        super(className, "java");
    }

    public JavaClassDescription(String className, String packageName) {
        super(className, "java");
        this.packageName = packageName;
    }
}
