package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

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
    private String albumName;
    private List<String> photos = new ArrayList<>();

    /**
     * Sets the stage for this controller.
     *
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the album name for this controller.
     *
     * @param albumName the album name to set
     */
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    @FXML
    private void handleAddPhoto() {
        String photoPath = photoPathField.getText();
        if (photoPath == null || photoPath.trim().isEmpty()) {
            showAlert("Error", "Please enter a photo path.");
            return;
        }
        if (photos.contains(photoPath)) {
            showAlert("Error", "Photo already exists in the album.");
            return;
        }
        photos.add(photoPath);
        photoListView.getItems().add(photoPath);
        photoPathField.clear();
    }

    @FXML
    private void handleRemovePhoto() {
        String selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto == null) {
            showAlert("Error", "Please select a photo to remove.");
            return;
        }
        photos.remove(selectedPhoto);
        photoListView.getItems().remove(selectedPhoto);
    }

    @FXML
    private void handleCaptionPhoto() {
        // Implement caption photo functionality
        showAlert("Caption Photo", "This feature is not yet implemented.");
    }

    @FXML
    private void handleTagPhoto() {
        // Implement tag photo functionality
        showAlert("Tag Photo", "This feature is not yet implemented.");
    }

    @FXML
    private void handleDeleteTag() {
        // Implement delete tag functionality
        showAlert("Delete Tag", "This feature is not yet implemented.");
    }

    @FXML
    private void handleCopyPhoto() {
        // Implement copy photo functionality
        showAlert("Copy Photo", "This feature is not yet implemented.");
    }

    @FXML
    private void handleMovePhoto() {
        // Implement move photo functionality
        showAlert("Move Photo", "This feature is not yet implemented.");
    }

    @FXML
    private void handleSearchPhotos() {
        // Implement search photos functionality
        showAlert("Search Photos", "This feature is not yet implemented.");
    }

    @FXML
    private void handleSlideshow() {
        // Implement slideshow functionality
        showAlert("Slideshow", "This feature is not yet implemented.");
    }

    @FXML
    private void handleBackToAlbums() {
        try {
            // Load the user view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserView.fxml"));
            Parent root = loader.load();

            // Get the UserController and pass the stage to it
            UserController controller = loader.getController();
            controller.setStage(stage);

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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}