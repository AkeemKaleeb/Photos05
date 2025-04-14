package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.DataManager;
import model.User;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controls the admin view of the photo album application.
 * The admin view allows administrators to manage users and albums.
 * 
 * @owner Kaileb Cole
 * @owner Maxime Deperrois
 */
public class AdminController {

    private Stage stage;
    private static final String USER_DATA_DIR = System.getProperty("user.home") + File.separator + "PhotoAlbumUsers";
    private static Map<String, User> users = new HashMap<>();

    @FXML
    private ListView<String> userListView;

    /**
     * Initializes the controller and loads all existing users.
     */
    @FXML
    public void initialize() {
        try {
            users = DataManager.loadAllUsers(USER_DATA_DIR);
            loadUsersIntoListView();
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
     * Handles the "Create User" button action.
     */
    @FXML
    private void handleCreateUser() {
        String username = showUsernamePrompt();
        if (username == null || username.trim().isEmpty()) {
            showAlert("Error", "Please enter a username.");
            return;
        }
        if (users.containsKey(username)) {
            showAlert("Error", "Username already exists.");
            return;
        }
        User user = new User(username);
        users.put(username, user);
        saveUserData(user);
        loadUsersIntoListView();
        showAlert("Success", "User created successfully.");
    }

    /**
     * Handles the "Delete User" button action.
     */
    @FXML
    private void handleDeleteUser() {
        if (users.isEmpty()) {
            showAlert("Error", "No users available to delete.");
            return;
        }

        // Create a ChoiceDialog with the list of all users
        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, users.keySet());
        dialog.setTitle("Delete User");
        dialog.setHeaderText("Select a user to delete:");
        dialog.setContentText("User:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return; // User canceled the dialog
        }

        String selectedUser = result.get();
        if (!users.containsKey(selectedUser)) {
            showAlert("Error", "The selected user does not exist.");
            return;
        }

        // Confirm deletion
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to delete the user \"" + selectedUser + "\"?");
        Optional<ButtonType> confirmationResult = confirmationAlert.showAndWait();

        if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {
            users.remove(selectedUser);
            deleteUserData(selectedUser);
            loadUsersIntoListView(); // Refresh the ListView
            showAlert("Success", "User \"" + selectedUser + "\" deleted successfully.");
    }
}

    /**
     * Loads all users into the ListView.
     */
    private void loadUsersIntoListView() {
        userListView.getItems().clear();
        userListView.getItems().addAll(users.keySet());
    }

    /**
     * Handles the "Logout" button action.
     */
    @FXML
    private void handleLogout() {
        try {
            // Load the login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            Parent root = loader.load();

            // Get the LoginController and pass the stage to it
            LoginController controller = loader.getController();
            controller.setStage(stage);

            // Set up the scene and stage
            Scene scene = new Scene(root, 300, 200);
            stage.setTitle("Photo Album Login");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the login view.");
        }
    }

    /**
     * Displays an alert dialog with the given title and message.
     *
     * @param title   the title of the alert
     * @param message the message to display
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows the username prompt dialog and returns the entered username.
     *
     * @return the entered username
     */
    private String showUsernamePrompt() {
        try {
            // Load the FXML file and create a new stage for the dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UsernamePrompt.fxml"));
            Parent page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter Username");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the controller for the dialog
            UsernamePromptController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked() ? controller.getUsername() : null;
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the username prompt.");
            return null;
        }
    }

    /**
     * Saves the user data to disk.
     *
     * @param user the user to save
     */
    private void saveUserData(User user) {
        try {
            File userDir = new File(USER_DATA_DIR);
            if (!userDir.exists()) {
                userDir.mkdirs();
            }
            String filePath = USER_DATA_DIR + File.separator + user.getUsername() + ".dat";
            DataManager.saveUser(user, filePath);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save user data.");
        }
    }

    /**
     * Deletes the user data from disk.
     *
     * @param username the username of the user to delete
     */
    private void deleteUserData(String username) {
        File userFile = new File(USER_DATA_DIR + File.separator + username + ".dat");
        if (userFile.exists()) {
            userFile.delete();
        }
    }

    /**
     * Returns the map of users.
     *
     * @return the map of users
     */
    public static Map<String, User> getUsers() {
        return users;
    }
}