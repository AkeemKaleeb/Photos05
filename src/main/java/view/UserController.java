package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Album;
import model.DataManager;
import model.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            String displayName = String.format("%s (%d photos)", album.getName(), album.getPhotos().size());
            albums.add(album.getName());
            albumListView.getItems().add(displayName);
        }
    }

    /**
     * Handles the create album button.
     */
    @FXML
    private void handleCreateAlbum() {
        // Prompt the user to enter the album name
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Album");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter the name of the new album:");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent() || result.get().trim().isEmpty()) {
            showAlert("Error", "Album name cannot be empty.");
            return;
        }

        String albumName = result.get().trim();
        if (albums.contains(albumName)) {
            showAlert("Error", "Album name already exists.");
            return;
        }

        Album album = new Album(albumName);
        user.addAlbum(album);
        albums.add(albumName);
        albumListView.getItems().add(String.format("%s (0 photos)", albumName));
    }

    /**
     * Handles the delete album button.
     */
    @FXML
    private void handleDeleteAlbum() {
        String selectedDisplayName = albumListView.getSelectionModel().getSelectedItem();
        if (selectedDisplayName == null) {
            showAlert("Error", "Please select an album to delete.");
            return;
        }

        // Extract the actual album name from the display name
        String actualAlbumName = selectedDisplayName.split(" \\(")[0];

        // Find the album by name
        Album albumToDelete = findAlbumByName(actualAlbumName);
        if (albumToDelete == null) {
            showAlert("Error", "Album not found.");
            return;
        }

        // Confirm deletion
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Album");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to delete the album \"" + actualAlbumName + "\"?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            user.getAlbums().remove(albumToDelete);
            loadUserAlbums(); // Refresh the album list
            showAlert("Success", "Album \"" + actualAlbumName + "\" deleted successfully.");
        }
    }

    /**
     * Handles the rename album button.
     */
    @FXML
    private void handleRenameAlbum() {
        String selectedDisplayName = albumListView.getSelectionModel().getSelectedItem();
        if (selectedDisplayName == null) {
            showAlert("Error", "Please select an album to rename.");
            return;
        }

        // Extract the actual album name from the display name
        String actualAlbumName = selectedDisplayName.split(" \\(")[0];

        // Prompt the user to enter the new album name
        TextInputDialog dialog = new TextInputDialog(actualAlbumName);
        dialog.setTitle("Rename Album");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter the new name for the album:");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent() || result.get().trim().isEmpty()) {
            showAlert("Error", "Album name cannot be empty.");
            return;
        }

        String newAlbumName = result.get().trim();
        if (albums.contains(newAlbumName)) {
            showAlert("Error", "Album name already exists.");
            return;
        }

        Album album = findAlbumByName(actualAlbumName);
        if (album != null) {
            album.setName(newAlbumName);
            albums.remove(actualAlbumName);
            albums.add(newAlbumName);
            loadUserAlbums(); // Refresh the album list
        }
    }

    /**
     * Handles the open album button.
     */
    @FXML
    private void handleOpenAlbum() {
        String selectedDisplayName = albumListView.getSelectionModel().getSelectedItem();
        if (selectedDisplayName == null) {
            showAlert("Error", "Please select an album to open.");
            return;
        }

        String actualAlbumName = selectedDisplayName.split(" \\(")[0];

        Album album = findAlbumByName(actualAlbumName);
        if (album == null) {
            showAlert("Error", "Album not found.");
            return;
        }
        // Load the album view
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AlbumView.fxml"));
            Parent root = loader.load();

            // Get the AlbumController and pass the stage, user, and album to it
            AlbumController controller = loader.getController();
            controller.setStage(stage);
            controller.setAlbum(album);

            // Set up the scene and stage
            Scene scene = new Scene(root, 600, 400);
            stage.setTitle("Album: " + album.getName());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the album view.");
        }
    }

    /**
     * Handles the logout button.
     */
    @FXML
    private void handleLogout() {
        try {
            // Save user data before logging out
            if(!user.getUsername().equals("stock")) {
                saveUserData();
            }
            else {
                saveStockUserData(user);
            }

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
     * Saves the stock user data to a file.
     * @param stockUser the stock user to save
     */
    private void saveStockUserData(User stockUser) {
        try {
            String filePath = Paths.get("data", "stockUser.dat").toString();
            DataManager.saveUser(stockUser, filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows an alert dialog with the given title and message.
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
     * @return the album if found, null otherwise
     */
    private Album findAlbumByName(String albumName) {
        for (Album album : user.getAlbums()) {
            if (album.getName().equals(albumName)) {
                return album;
            }
        }
        return null;
    }

    @FXML
    private void handleAlbumDoubleClick(MouseEvent event) {
        // Check if the user double-clicked
        if (event.getClickCount() == 2) {
            String selectedDisplayName = albumListView.getSelectionModel().getSelectedItem();
            if (selectedDisplayName == null) {
                return;
            }

            // Extract the actual album name from the display name
            String actualAlbumName = selectedDisplayName.split(" \\(")[0];

            // Find the album by name
            Album selectedAlbum = findAlbumByName(actualAlbumName);
            if (selectedAlbum == null) {
                showAlert("Error", "Album not found.");
                return;
            }

            // Open the album view
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AlbumView.fxml"));
                Parent root = loader.load();

                AlbumController controller = loader.getController();
                controller.setStage(stage);
                controller.setAlbum(selectedAlbum);

                Scene scene = new Scene(root, 800, 600);
                stage.setTitle("Album: " + selectedAlbum.getName());
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load the album view.");
            }
        }
    }
}