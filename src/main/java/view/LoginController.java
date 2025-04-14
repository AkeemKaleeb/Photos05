package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.DataManager;
import model.User;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Controls the login view of the photo album application.
 * The login view allows users to log in to the application and redirects to admin or user interface.
 * 
 * @owner Kaileb Cole
 * @owner Maxime Deperrois
 */
public class LoginController {
    @FXML
    private TextField usernameField;

    private Stage stage;
    private User currUser;
    private static final String USER_DATA_DIR = System.getProperty("user.home") + File.separator + "PhotoAlbumUsers";
    private Map<String, User> users;
    
    /**
     * Initializes the controller and loads all existing users.
     */
    @FXML
    public void initialize() {
        try {
            users = DataManager.loadAllUsers(USER_DATA_DIR);
            // Add admin and stock users to the users list
            users.putIfAbsent("admin", new User("admin"));
            users.putIfAbsent("stock", new User("stock"));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load user data.");
        }
    }
    
    /**
     * Sets the stage for this controller.
     *
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    
    /**
     * Returns the currently logged-in user.
     *
     * @return the logged-in user
     */
    public User getCurrentUser() {
        return currUser;
    }

    /**
     * Returns the currently logged-in user.
     *
     * @return the logged-in user
     */
    public void setCurrentUser(User currUser) {
        this.currUser = currUser;
    }

    /**
     * Handles the login button action.
     * If the username is "admin", the admin view is loaded.
     * Otherwise, the user is authenticated and the user view is loaded.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();

        if (username == null || username.trim().isEmpty() || !users.containsKey(username)) {
            showAlert("Error", "User Does Not Exist");
            return;
        }

        if (username.equals("admin")) {
            // Load the admin view
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminView.fxml"));
                Parent root = loader.load();

                // Get the AdminController and pass the stage to it
                AdminController controller = loader.getController();
                controller.setStage(stage);

                // Set up the scene and stage
                Scene scene = new Scene(root, 600, 400);
                stage.setTitle("Photo Admin Interface");
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } 
        else if(username.equals("stock")) {
            // Load the stock user view
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserView.fxml"));
                Parent root = loader.load();

                // Get the StockUserController and pass the stage to it
                UserController controller = loader.getController();
                controller.setStage(stage);

                User stockUser = DataManager.loadUser("data/stockUser.dat");
                controller.setUser(stockUser);

                // Set up the scene and stage
                Scene scene = new Scene(root, 600, 400);
                stage.setTitle("Photo Album Stock User");
                stage.setScene(scene);
                stage.show();
                setCurrentUser(stockUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            // Load the user view
            try {
                String filePath = System.getProperty("user.home") + File.separator + "PhotoAlbumUsers" + File.separator + username + ".dat";
                User user = DataManager.loadUser(filePath);
                if (user == null) {
                    showAlert("Error", "Invalid username.");
                    return;
                }
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserView.fxml"));
                Parent root = loader.load();

                // Get the UserController and pass the stage to it
                UserController controller = loader.getController();
                controller.setStage(stage);
                controller.setUser(user);

                // Set up the scene and stage
                Scene scene = new Scene(root, 600, 400);
                stage.setTitle("Photo Album User");
                stage.setScene(scene);
                stage.show();
                setCurrentUser(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Shows an alert with the specified title and message.
     *
     * @param title the title of the alert
     * @param message the message of the alert
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}