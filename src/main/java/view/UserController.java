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
    private List<String> albums = new ArrayList<>();

    /**
     * Sets the stage for this controller.
     *
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

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
        albums.add(albumName);
        albumListView.getItems().add(albumName);
        albumNameField.clear();
    }

    @FXML
    private void handleDeleteAlbum() {
        String selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
        if (selectedAlbum == null) {
            showAlert("Error", "Please select an album to delete.");
            return;
        }
        albums.remove(selectedAlbum);
        albumListView.getItems().remove(selectedAlbum);
    }

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
        albums.remove(selectedAlbum);
        albums.add(newAlbumName);
        albumListView.getItems().set(albumListView.getSelectionModel().getSelectedIndex(), newAlbumName);
        albumNameField.clear();
    }

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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}