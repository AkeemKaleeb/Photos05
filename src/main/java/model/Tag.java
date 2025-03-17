package model;

import java.io.Serializable;

/**
 * Represents a tag in the photo album application.
 * Each tag has a name and a value.
 * 
 * @owner Kaileb Cole
 * @owner Maxime Deperrois
 */
public class Tag implements Serializable {
    private String name;
    private String value;

    /**
     * Creates a new tag with the given name and value.
     * 
     * @param name the name of the tag
     * @param value the value of the tag
     */
    public Tag(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Returns the name of the tag.
     * 
     * @return the name of the tag
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the value of the tag.
     * 
     * @return the value of the tag
     */
    public String getValue() {
        return value;
    }
}
