package view;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.Album;
import model.Photo;
import model.User;
import model.Tag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    private ListView<Photo> photoListView;

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
        this.user = album.getUser(); 
        loadAlbumPhotos();
    }

    /**
     * Loads the album's photos into the ListView.
     */
    private void loadAlbumPhotos() {
        photoListView.setItems(FXCollections.observableArrayList(album.getPhotos()));
        photoListView.setCellFactory(param -> new ListCell<Photo>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(Photo photo, boolean empty) {
                super.updateItem(photo, empty);
                if (empty || photo == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    File file = new File(photo.getFilePath());
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(100);
                    setText(photo.getCaption() != null ? photo.getCaption() : file.getName());
                    setGraphic(imageView);
                }
            }
        });
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

        File file = new File(photoPath);
        if (!file.exists() || !file.isFile()) {
            showAlert("Error", "Invalid photo path.");
            return;
        }

        try {
            Photo photo = new Photo(photoPath);
            if(album.getPhotos().contains(photo)) {
                showAlert("Error", "Photo already exists in the album.");
                return;
            }

            album.addPhoto(photo);
            photoListView.getItems().add(photo);
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
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto == null) {
            showAlert("Error", "Please select a photo to remove.");
            return;
        }
        album.removePhoto(selectedPhoto);
        photoListView.getItems().remove(selectedPhoto);
    }

    /**
     * Handles the "Caption Photo" button action.
     */
    @FXML
    private void handleCaptionPhoto() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto == null) {
            showAlert("Error", "Please select a photo to add or edit a caption.");
            return;
        }

        // Prompt user to enter a new caption
        TextInputDialog captionDialog = new TextInputDialog(selectedPhoto.getCaption());
        captionDialog.setTitle("Edit Caption");
        captionDialog.setHeaderText("Enter a new caption for the selected photo:");
        captionDialog.setContentText("Caption:");
        Optional<String> captionResult = captionDialog.showAndWait();

        if (!captionResult.isPresent() || captionResult.get().trim().isEmpty()) {
            showAlert("Error", "Caption cannot be empty.");
            return;
        }

        String newCaption = captionResult.get().trim();

        // Update the photo's caption
        selectedPhoto.setCaption(newCaption);

        // Refresh the ListView to reflect the updated caption
        photoListView.refresh();

        showAlert("Success", "Caption updated successfully.");
    }

    /**
     * Handles the "Tag Photo" button action.
     */
    @FXML
    private void handleTagPhoto() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto == null) {
            showAlert("Error", "Please select a photo to tag.");
            return;
        }

        // Predefined tag types
        List<String> predefinedTagTypes = Arrays.asList("location", "person", "activity");

        // Prompt user to select or enter a tag type
        ChoiceDialog<String> tagTypeDialog = new ChoiceDialog<>("location", predefinedTagTypes);
        tagTypeDialog.setTitle("Add Tag");
        tagTypeDialog.setHeaderText("Select or Enter Tag Type");
        tagTypeDialog.setContentText("Tag Type:");
        Optional<String> tagTypeResult = tagTypeDialog.showAndWait();

        if (!tagTypeResult.isPresent() || tagTypeResult.get().trim().isEmpty()) {
            showAlert("Error", "Tag type cannot be empty.");
            return;
        }

        String tagType = tagTypeResult.get().trim();

        // Prompt user for the tag value
        TextInputDialog tagValueDialog = new TextInputDialog();
        tagValueDialog.setTitle("Add Tag");
        tagValueDialog.setHeaderText("Enter Tag Value");
        tagValueDialog.setContentText("Tag Value:");
        Optional<String> tagValueResult = tagValueDialog.showAndWait();

        if (!tagValueResult.isPresent() || tagValueResult.get().trim().isEmpty()) {
            showAlert("Error", "Tag value cannot be empty.");
            return;
        }

        String tagValue = tagValueResult.get().trim();

        // Check if the tag already exists
        Tag newTag = new Tag(tagType, tagValue);
        if (selectedPhoto.getTags().contains(newTag)) {
            showAlert("Error", "This tag already exists for the selected photo.");
            return;
        }

        // Add the tag to the photo
        selectedPhoto.addTag(newTag);
        showAlert("Success", "Tag added successfully.");
    }

    /**
     * Handles the "Delete Tag" button action.
     */
    @FXML
    private void handleDeleteTag() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto == null) {
            showAlert("Error", "Please select a photo to delete a tag from.");
            return;
        }

        // Check if the photo has any tags
        if (selectedPhoto.getTags().isEmpty()) {
            showAlert("Error", "This photo has no tags to delete.");
            return;
        }

        // Prompt user to select a tag to delete
        List<String> tagOptions = new ArrayList<>();
        for (Tag tag : selectedPhoto.getTags()) {
            tagOptions.add(tag.getName() + ": " + tag.getValue());
        }

        ChoiceDialog<String> tagDialog = new ChoiceDialog<>(tagOptions.get(0), tagOptions);
        tagDialog.setTitle("Delete Tag");
        tagDialog.setHeaderText("Select a Tag to Delete");
        tagDialog.setContentText("Tag:");
        Optional<String> tagResult = tagDialog.showAndWait();

        if (!tagResult.isPresent()) {
            return; // User canceled the dialog
        }

        String selectedTag = tagResult.get();
        Tag tagToDelete = null;

        // Find the tag object corresponding to the selected string
        for (Tag tag : selectedPhoto.getTags()) {
            if ((tag.getName() + ": " + tag.getValue()).equals(selectedTag)) {
                tagToDelete = tag;
                break;
            }
        }

        if (tagToDelete != null) {
            selectedPhoto.removeTag(tagToDelete);
            showAlert("Success", "Tag deleted successfully.");
        } else {
            showAlert("Error", "Failed to delete the selected tag.");
        }
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