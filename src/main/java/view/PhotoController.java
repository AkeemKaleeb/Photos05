package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Album;
import model.Photo;
import model.Tag;

import java.io.File;
import java.util.List;

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
        private void showAlert(String message) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
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
            showAlert("Error: Failed to return to the album view.");
        }
    }
}