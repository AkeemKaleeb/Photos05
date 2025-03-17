package model;

import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Handles serialization and deserialization of user data
 * The data manager is responsible for saving and loading user data to/from disk
 * 
 * @owner Kaileb Cole
 * @owner Maxime Deperrois
 */
public class DataManager {
    /**
     * Save the user to the given file path
     * 
     * @param user the user to save
     * @param filePath the file path of the user data
     * @throws IOException if the file path is invalid
     */
    public static void saveUser(User user, String filePath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(user);
        }
    }

    /**
     * Load the user from the given file path
     * 
     * @param filePath the file path of the user data
     * @return the user loaded from the file
     * @throws IOException if the file path is invalid
     * @throws ClassNotFoundException if the class of the serialized object cannot be found
     */
    public static User loadUser(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return (User) in.readObject();
        }
    }
}
