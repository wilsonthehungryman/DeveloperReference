package com.rrc.wilson.developerreference;

/**
 * RubyClassDescription represents a Ruby class.
 * <b>WARNING</b> not fully implemented
 *
 * <pre>
 * Created by Wilson on 2017-04-08.
 *
 * Revisions
 * Wilson       2017-04-08      Created
 * </pre>
 */
public class RubyClassDescription extends ClassDescription {
    private String inheritance;

    public RubyClassDescription(String className) {
        this(className, null);
    }

    public RubyClassDescription(String className, String inheritance) {
        this(className, inheritance, null);
    }

    public RubyClassDescription(String className, String inheritance, String[] urls) {
        super(className, "Ruby", inheritance, urls);
        this.inheritance = inheritance;
    }
}
