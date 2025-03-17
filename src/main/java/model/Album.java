package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an album in the photo album application.
 * Each album has a name, a list of photos, and methods to manage those photos.
 * 
 * @owner Kaileb Cole
 * @owner Maxime Deperrois
 */
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private List<Photo> photos;

    /**
     * Creates a new album with the given name.
     * 
     * @param name the name of the album
     */
    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }

    /**
     * Returns the name of the album
     * 
     * @return the name of the album
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the album. Allows user to change name
     * 
     * @param name the new name of the album
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the list of photos of the album.
     * 
     * @return the list of photos of the album
     */
    public List<Photo> getPhotos() {
        return photos;
    }

    /**
     * Adds a photo to the list of photos of the album.
     * 
     * @param photo the photo to add
     */
    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    /**
     * Removes a photo from the list of photos of the album.
     * 
     * @param photo the photo to remove
     */
    public void removePhoto(Photo photo) {
        photos.remove(photo);
    }


}
