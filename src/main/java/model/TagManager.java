package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the list of tag types in the photo album application.
 * 
 * @owner Kaileb Cole
 * @owner Maxime Deperrois
 */
public class TagManager {
    // Shared list of tag types
    private static final List<String> tagTypes = new ArrayList<>(List.of("location", "person", "activity"));

    /**
     * Returns the list of tag types.
     *
     * @return the list of tag types
     */
    public static List<String> getTagTypes() {
        return tagTypes;
    }

    /**
     * Adds a new tag type to the list if it doesn't already exist.
     *
     * @param tagType the tag type to add
     */
    public static void addTagType(String tagType) {
        if (!tagTypes.contains(tagType)) {
            tagTypes.add(tagType);
        }
    }
}