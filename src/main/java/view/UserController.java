package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Album;
import model.User;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import model.DataManager;

/**
 * Controls the user view of the photo album application.
 * The user view allows users to manage albums and photos.
 * 
 * @owner Kaileb Cole
 * @owner Maxime Deperrois
 */
public class UserController {

    @FXML
    private TextField albumNameField;

    @FXML
    private ListView<String> albumListView;

    private Stage stage;
    private User user;
    private List<String> albums = new ArrayList<>();

    /**
     * Sets the stage for this controller.
     *
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the user for this controller.
     *
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
        loadUserAlbums();
    }

    /**
     * Loads the user's albums into the ListView.
     */
    private void loadUserAlbums() {
        albums.clear();
        albumListView.getItems().clear();
        for (Album album : user.getAlbums()) {
            albums.add(album.getName());
            albumListView.getItems().add(album.getName());
        }
    }

    /**
     * Allows the user to create a new album.
     * The album name is entered in the text field and then added to the list view.
     */
    @FXML
    private void handleCreateAlbum() {
        String albumName = albumNameField.getText();
        if (albumName == null || albumName.trim().isEmpty()) {
            showAlert("Error", "Please enter an album name.");
            return;
        }
        if (albums.contains(albumName)) {
            showAlert("Error", "Album name already exists.");
            return;
        }
        Album album = new Album(albumName);
        user.addAlbum(album);
        albums.add(albumName);
        albumListView.getItems().add(albumName);
        albumNameField.clear();
    }

    /**
     * Allows the user to delete an album.
     * The selected album in the list view is removed from the list view.
     */
    @FXML
    private void handleDeleteAlbum() {
        String selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
        if (selectedAlbum == null) {
            showAlert("Error", "Please select an album to delete.");
            return;
        }
        Album album = findAlbumByName(selectedAlbum);
        if (album != null) {
            user.removeAlbum(album);
            albums.remove(selectedAlbum);
            albumListView.getItems().remove(selectedAlbum);
        }
    }

    /**
     * Allows the user to rename an album.
     * The selected album in the list view is renamed with the new name entered in the text field.
     */
    @FXML
    private void handleRenameAlbum() {
        String selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
        String newAlbumName = albumNameField.getText();
        if (selectedAlbum == null) {
            showAlert("Error", "Please select an album to rename.");
            return;
        }
        if (newAlbumName == null || newAlbumName.trim().isEmpty()) {
            showAlert("Error", "Please enter a new album name.");
            return;
        }
        if (albums.contains(newAlbumName)) {
            showAlert("Error", "Album name already exists.");
            return;
        }
        Album album = findAlbumByName(selectedAlbum);
        if (album != null) {
            album.setName(newAlbumName);
            albums.remove(selectedAlbum);
            albums.add(newAlbumName);
            albumListView.getItems().set(albumListView.getSelectionModel().getSelectedIndex(), newAlbumName);
            albumNameField.clear();
        }
    }

    /**
     * Allows the user to open an album.
     * The selected album in the list view is opened in the album view.
     */
    @FXML
    private void handleOpenAlbum() {
        String selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
        if (selectedAlbum == null) {
            showAlert("Error", "Please select an album to open.");
            return;
        }
        // Load the album view
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AlbumView.fxml"));
            Parent root = loader.load();

            // Get the AlbumController and pass the stage and album name to it
            AlbumController controller = loader.getController();
            controller.setStage(stage);
            controller.setAlbumName(selectedAlbum);

            // Set up the scene and stage
            Scene scene = new Scene(root, 600, 400);
            stage.setTitle("Album: " + selectedAlbum);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the album view.");
        }
    }

    /**
     * Allows the user to logout.
     * The user data is saved before logging out.
     */
    @FXML
    private void handleLogout() {
        try {
            // Save user data before logging out
            saveUserData();

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
     * Saves the user data to a file.
     */
    private void saveUserData() {
        try {
            String filePath = System.getProperty("user.home") + File.separator + "PhotoAlbumUsers" + File.separator + user.getUsername() + ".dat";
            DataManager.saveUser(user, filePath);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save user data.");
        }
    }

    /**
     * Shows an alert with the specified title and message.
     *
     * @param title   the title of the alert
     * @param message the message of the alert
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Finds an album by name.
     *
     * @param albumName the name of the album to find
     * @return the album with the specified name, or null if not found
     */
    private Album findAlbumByName(String albumName) {
        for (Album album : user.getAlbums()) {
            if (album.getName().equals(albumName)) {
                return album;
            }
        }
        return null;
    }
}