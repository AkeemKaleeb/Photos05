package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles serialization and deserialization of user data.
 * 
 * @owner Kaileb Cole
 * @owner Maxime Deperrois
 */
public class DataManager {

    /**
     * Save the user to the given file path.
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
     * Load the user from the given file path.
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

    /**
     * Load all users from the user data directory.
     * 
     * @param userDir the directory containing user data files
     * @return a map of usernames to User objects
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class of the serialized object cannot be found
     */
    public static Map<String, User> loadAllUsers(String userDir) throws IOException, ClassNotFoundException {
        Map<String, User> users = new HashMap<>();
        File dir = new File(userDir);
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".dat")) {
                    User user = loadUser(file.getAbsolutePath());
                    users.put(user.getUsername(), user);
                }
            }
        }
        return users;
    }
}