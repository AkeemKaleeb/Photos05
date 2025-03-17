package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the photo album application.
 * Each user has a username, a list of albums, and methods to manage those albums.
 * 
 * @author Kaileb Cole
 * @author Maxime Deperrois
 */
public class User implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private String username;
    private List<Album> albums;

    /**
     * Creates a new user with the given username.
     * 
     * @param username the username of the user
     */
    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<>();
        this.albums = new ArrayList<>();
    }

    /**
     * Returns the username of the user.
     * 
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user. Allows user to change name
     * 
     * @param username the new username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the list of albums of the user.
     * 
     * @return the list of albums of the user
     */
    public List<Album> getAlbums() {
        return albums;
    }

    /**
     * Adds an album to the list of albums of the user.
     * 
     * @param album the album to add
     */
    public void addAlbum(Album album) {
        albums.add(album);
    }

    /**
     * Removes an album from the list of albums of the user.
     * 
     * @param album the album to remove
     */
    public void removeAlbum(Album album) {
        albums.remove(album);
    }
}
