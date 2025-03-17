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
import model.Photo;
import model.User;

import java.io.IOException;

/**
 * Controls the album view of the photo album application.
 * The album view allows users to manage photos within an album.
 * 
 * @owner Kaileb Cole
 * @owner Maxime Deperrois
 */
public class AlbumController {

    @FXML
    private TextField photoPathField;

    @FXML
    private ListView<String> photoListView;

    private Stage stage;
    private Album album;
    private User user;

    /**
     * Sets the stage for this controller.
     *
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the album for this controller.
     *
     * @param album the album to set
     */
    public void setAlbum(Album album) {
        this.album = album;
        this.user = album.getUser(); // Assuming Album has a reference to User
        loadAlbumPhotos();
    }

    /**
     * Loads the album's photos into the ListView.
     */
    private void loadAlbumPhotos() {
        photoListView.getItems().clear();
        for (Photo photo : album.getPhotos()) {
            photoListView.getItems().add(photo.getFilePath());
        }
    }

    /**
     * Handles the "Add Photo" button action.
     */
    @FXML
    private void handleAddPhoto() {
        String photoPath = photoPathField.getText();
        if (photoPath == null || photoPath.trim().isEmpty()) {
            showAlert("Error", "Please enter a photo path.");
            return;
        }
        try {
            Photo photo = new Photo(photoPath);
            album.addPhoto(photo);
            photoListView.getItems().add(photoPath);
            photoPathField.clear();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add photo.");
        }
    }

    /**
     * Handles the "Remove Photo" button action.
     */
    @FXML
    private void handleRemovePhoto() {
        String selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto == null) {
            showAlert("Error", "Please select a photo to remove.");
            return;
        }
        Photo photo = findPhotoByPath(selectedPhoto);
        if (photo != null) {
            album.removePhoto(photo);
            photoListView.getItems().remove(selectedPhoto);
        }
    }

    /**
     * Handles the "Caption Photo" button action.
     */
    @FXML
    private void handleCaptionPhoto() {
        // Implement caption photo functionality
    }

    /**
     * Handles the "Tag Photo" button action.
     */
    @FXML
    private void handleTagPhoto() {
        // Implement tag photo functionality
    }

    /**
     * Handles the "Delete Tag" button action.
     */
    @FXML
    private void handleDeleteTag() {
        // Implement delete tag functionality
    }

    /**
     * Handles the "Move Photo" button action.
     */
    @FXML
    private void handleMovePhoto() {
        // Implement move photo functionality
    }

    /**
     * Handles the "Search Photos" button action.
     */
    @FXML
    private void handleSearchPhotos() {
        // Implement search photos functionality
    }

    /**
     * Handles the "Back to Albums" button action.
     * Loads the user view.
     */
    @FXML
    private void handleBackToAlbums() {
        try {
            // Load the user view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserView.fxml"));
            Parent root = loader.load();

            // Get the UserController and pass the stage and user to it
            UserController controller = loader.getController();
            controller.setStage(stage);
            controller.setUser(user);

            // Set up the scene and stage
            Scene scene = new Scene(root, 600, 400);
            stage.setTitle("Photo Album User");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the user view.");
        }
    }

    /**
     * Shows an alert dialog with the specified title and message.
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
     * Finds a photo in the album by its file path.
     *
     * @param photoPath the file path of the photo to find
     * @return the photo with the specified file path, or null if not found
     */
    private Photo findPhotoByPath(String photoPath) {
        for (Photo photo : album.getPhotos()) {
            if (photo.getFilePath().equals(photoPath)) {
                return photo;
            }
        }
        return null;
    }
}