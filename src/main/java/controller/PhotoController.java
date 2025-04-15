package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Album;
import model.Photo;
import model.Tag;
import model.TagManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controls the photo view of the photo album application.
 * The photo view allows users to view and manage photos.
 * 
 * @owner Kaileb Cole
 * @owner Maxime Deperrois
 */
public class PhotoController {

    @FXML
    private ImageView photoImageView;

    @FXML
    private TextField titleField;

    @FXML
    private TextField dateField;

    @FXML
    private TextField captionField;

    @FXML
    private TextField tagField;

    @FXML
    private ListView<String> tagsListView;

    @FXML
    private Button previousButton;

    @FXML
    private Button nextButton;

    private Stage stage;
    private Album album;
    private List<Photo> photos;
    private int currentIndex;

    /**
     * Sets the stage for this controller.
     *
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        // Add a key event listener for the Enter key
        captionField.setOnAction(event -> updateCaption());
    }

    /**
     * Sets the album and initializes the photo view.
     *
     * @param album the album to set
     * @param initialIndex the index of the photo to display initially
     */
    public void setAlbum(Album album, int initialIndex) {
        this.album = album;
        this.photos = album.getPhotos();
        this.currentIndex = initialIndex;
        updatePhotoView();
    }

    /**
     * Updates the photo view with the current photo's details.
     */
    private void updatePhotoView() {
        if (photos.isEmpty() || currentIndex < 0 || currentIndex >= photos.size()) {
            return;
        }

        Photo currentPhoto = photos.get(currentIndex);

        // Update the image
        File photoFile = new File(currentPhoto.getFilePath());
        if (photoFile.exists()) {
            Image image = new Image(photoFile.toURI().toString());
            photoImageView.setImage(image);
        }

        // Update the information fields
        titleField.setText(photoFile.getName());
        dateField.setText(currentPhoto.getLastModifiedDate().toString());
        captionField.setText(currentPhoto.getCaption());

        // Update the tags list
        tagsListView.getItems().clear();
        for (Tag tag : currentPhoto.getTags()) {
            tagsListView.getItems().add(tag.getName() + ": " + tag.getValue());
        }

        // Enable/disable navigation buttons
        previousButton.setDisable(currentIndex == 0);
        nextButton.setDisable(currentIndex == photos.size() - 1);
    }

    @FXML
    private void handleMovePhoto() {
        Photo currentPhoto = photos.get(currentIndex);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Move Photo");
        dialog.setHeaderText("Enter the name of the destination album:");
        dialog.setContentText("Album Name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String destinationAlbumName = result.get().trim();
            Album destinationAlbum = album.getUser().getAlbums().stream()
                    .filter(a -> a.getName().equals(destinationAlbumName))
                    .findFirst()
                    .orElse(null);

            if (destinationAlbum == null) {
                showAlert("Error", "Album not found.");
                return;
            }

            if (destinationAlbum.getPhotos().contains(currentPhoto)) {
                showAlert("Error", "Photo already exists in the destination album.");
                return;
            }

            destinationAlbum.addPhoto(currentPhoto);
            album.removePhoto(currentPhoto);
            photos.remove(currentPhoto);
            if (currentIndex >= photos.size()) {
                currentIndex = photos.size() - 1;
            }
            updatePhotoView();
            showAlert("Success", "Photo moved successfully.");
        }
    }

    @FXML
    private void handleTagPhoto() {
        Photo currentPhoto = photos.get(currentIndex);

        // Display current tags
        StringBuilder currentTags = new StringBuilder("Current Tags:\n");
        if (currentPhoto.getTags().isEmpty()) {
            currentTags.append("No tags assigned.");
        } else {
            for (Tag tag : currentPhoto.getTags()) {
                currentTags.append("- ").append(tag.getName()).append(": ").append(tag.getValue()).append("\n");
            }
        }

        // Prompt the user to choose between adding or deleting a tag
        List<String> options = List.of("Add Tag", "Delete Tag");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Add Tag", options);
        dialog.setTitle("Tag Photo");
        dialog.setHeaderText(currentTags.toString());
        dialog.setContentText("What would you like to do?");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return; 
        }

        String choice = result.get();
        if (choice.equals("Add Tag")) {
            addTagToPhoto(currentPhoto);
        } else if (choice.equals("Delete Tag")) {
            deleteTagFromPhoto(currentPhoto);
        }
    }

    private void addTagToPhoto(Photo photo) {
        // Prompt the user to choose between using an existing tag type or adding a new one
        List<String> tagTypeOptions = List.of("Use Existing Tag Type", "Add New Tag Type");
        ChoiceDialog<String> tagTypeChoiceDialog = new ChoiceDialog<>("Use Existing Tag Type", tagTypeOptions);
        tagTypeChoiceDialog.setTitle("Tag Type Selection");
        tagTypeChoiceDialog.setHeaderText("Choose how to proceed:");
        tagTypeChoiceDialog.setContentText("What would you like to do?");
    
        Optional<String> tagTypeChoiceResult = tagTypeChoiceDialog.showAndWait();
        if (tagTypeChoiceResult.isEmpty()) {
            showAlert("Error", "Tag type selection canceled.");
            return;
        }
    
        String tagTypeChoice = tagTypeChoiceResult.get();
        String tagType;
    
        if (tagTypeChoice.equals("Add New Tag Type")) {
            // Prompt the user to define a new tag type
            TextInputDialog newTagTypeDialog = new TextInputDialog();
            newTagTypeDialog.setTitle("New Tag Type");
            newTagTypeDialog.setHeaderText("Enter a new tag type:");
            newTagTypeDialog.setContentText("Tag Type:");
    
            Optional<String> newTagTypeResult = newTagTypeDialog.showAndWait();
            if (newTagTypeResult.isEmpty() || newTagTypeResult.get().trim().isEmpty()) {
                showAlert("Error", "New tag type cannot be empty.");
                return;
            }
    
            tagType = newTagTypeResult.get().trim();
    
            // Add the new tag type to the shared list
            TagManager.addTagType(tagType);
        } else {
            // Use an existing tag type
            List<String> tagTypes = TagManager.getTagTypes();
            ChoiceDialog<String> existingTagTypeDialog = new ChoiceDialog<>("location", tagTypes);
            existingTagTypeDialog.setTitle("Select Tag Type");
            existingTagTypeDialog.setHeaderText("Select an existing tag type:");
            existingTagTypeDialog.setContentText("Tag Type:");
    
            Optional<String> existingTagTypeResult = existingTagTypeDialog.showAndWait();
            if (existingTagTypeResult.isEmpty()) {
                showAlert("Error", "Tag type selection canceled.");
                return;
            }
    
            tagType = existingTagTypeResult.get();
        }
    
        // Prompt the user to enter the tag value
        TextInputDialog tagValueDialog = new TextInputDialog();
        tagValueDialog.setTitle("Add Tag");
        tagValueDialog.setHeaderText("Enter a value for the tag:");
        tagValueDialog.setContentText("Tag Value:");
    
        Optional<String> tagValueResult = tagValueDialog.showAndWait();
        if (tagValueResult.isEmpty() || tagValueResult.get().trim().isEmpty()) {
            showAlert("Error", "Tag value cannot be empty.");
            return;
        }
    
        String tagValue = tagValueResult.get().trim();
        Tag newTag = new Tag(tagType, tagValue);
    
        if (photo.getTags().contains(newTag)) {
            showAlert("Error", "This tag already exists.");
            return;
        }
    
        photo.addTag(newTag);
        tagsListView.getItems().add(newTag.getName() + ": " + newTag.getValue());
        showAlert("Success", "Tag added successfully.");
    }

    private void deleteTagFromPhoto(Photo photo) {
        // Check if the photo has any tags
        if (photo.getTags().isEmpty()) {
            showAlert("Error", "This photo has no tags to delete.");
            return;
        }

        // Prompt the user to select a tag to delete
        List<String> tagOptions = new ArrayList<>();
        for (Tag tag : photo.getTags()) {
            tagOptions.add(tag.getName() + ": " + tag.getValue());
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(tagOptions.get(0), tagOptions);
        dialog.setTitle("Delete Tag");
        dialog.setHeaderText("Select a tag to delete:");
        dialog.setContentText("Tag:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        String selectedTag = result.get();
        Tag tagToDelete = null;

        // Find the tag object corresponding to the selected string
        for (Tag tag : photo.getTags()) {
            if ((tag.getName() + ": " + tag.getValue()).equals(selectedTag)) {
                tagToDelete = tag;
                break;
            }
        }

        if (tagToDelete != null) {
            photo.removeTag(tagToDelete);
            tagsListView.getItems().remove(selectedTag);
            showAlert("Success", "Tag deleted successfully.");
        } else {
            showAlert("Error", "Failed to delete the selected tag.");
        }
    }

    private void updateCaption() {
        Photo currentPhoto = photos.get(currentIndex);
        String newCaption = captionField.getText().trim();
        if (newCaption.isEmpty()) {
            showAlert("Error", "Caption cannot be empty.");
            return;
        }
        currentPhoto.setCaption(newCaption);
        showAlert("Success", "Caption updated successfully.");
    }

    /**
     * Handles the "Previous" button action.
     */
    @FXML
    private void handlePreviousPhoto() {
        if (currentIndex > 0) {
            currentIndex--;
            updatePhotoView();
        }
    }

    /**
     * Handles the "Next" button action.
     */
    @FXML
    private void handleNextPhoto() {
        if (currentIndex < photos.size() - 1) {
            currentIndex++;
            updatePhotoView();
        }
    }

    /**
     * Displays an alert with the given message.
     *
     * @param message the message to display in the alert
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBackToAlbum() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AlbumView.fxml"));
            Parent root = loader.load();

            // Get the AlbumController and pass the stage and album to it
            AlbumController controller = loader.getController();
            controller.setStage(stage);
            controller.setAlbum(album);

            // Set up the scene and stage
            Scene scene = new Scene(root, 800, 600);
            stage.setTitle("Album: " + album.getName());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to return to the album view.");
        }
    }
}