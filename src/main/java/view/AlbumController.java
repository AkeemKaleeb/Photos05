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
import javafx.scene.input.MouseEvent;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
        // Prompt user to select a photo file
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Select Photo");
        fileChooser.getExtensionFilters().addAll(
            new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile == null) {
            showAlert("Error", "No file selected.");
            return;
        }

        String photoPath = selectedFile.getAbsolutePath();

        try {
            Photo photo = new Photo(photoPath);
            if (album.getPhotos().contains(photo)) {
                showAlert("Error", "Photo already exists in the album.");
                return;
            }

            album.addPhoto(photo);
            photoListView.getItems().add(photo);
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
      * Handles showing the date of the photo
      */
     @FXML
     private void handleShowDate() {
         Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
         if (selectedPhoto == null) {
             showAlert("Error", "Please select a photo to view the date.");
             return;
         }
         showAlert("Photo Date", "Date: " + selectedPhoto.getLastModifiedDate());
     }
    
    /**
     * Handles the "Tag Photo" button action.
     */
    @FXML
    private void handleTagPhoto() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto == null) {
            showAlert("Error", "Please select a photo to manage tags.");
            return;
        }

        // Prompt the user to choose between adding or deleting a tag
        List<String> options = List.of("Add Tag", "Delete Tag");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Add Tag", options);
        dialog.setTitle("Tag Photo");
        dialog.setHeaderText("Choose an action:");
        dialog.setContentText("What would you like to do?");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return; // User canceled
        }

        String choice = result.get();
        if (choice.equals("Add Tag")) {
            addTagToPhoto(selectedPhoto);
        } else if (choice.equals("Delete Tag")) {
            deleteTagFromPhoto(selectedPhoto);
        }
    }

    private void addTagToPhoto(Photo photo) {
        // Prompt the user to enter a tag in the format "name:value"
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Tag");
        dialog.setHeaderText("Enter a tag in the format 'name:value':");
        dialog.setContentText("Tag:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty() || result.get().trim().isEmpty()) {
            showAlert("Error", "Tag cannot be empty.");
            return;
        }

        String[] tagParts = result.get().trim().split(":");
        if (tagParts.length != 2) {
            showAlert("Error", "Tag must be in the format 'name:value'.");
            return;
        }

        Tag newTag = new Tag(tagParts[0].trim(), tagParts[1].trim());
        if (photo.getTags().contains(newTag)) {
            showAlert("Error", "This tag already exists.");
            return;
        }

        photo.addTag(newTag);
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
            return; // User canceled
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
            showAlert("Success", "Tag deleted successfully.");
        } else {
            showAlert("Error", "Failed to delete the selected tag.");
        }
    }

    /**
     * Handles the "Move Photo" button action.
     * Prompts the user to decide wether to move the photo or copy it.
     */
    @FXML
    private void handleMovePhoto() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto == null) {
            showAlert("Error", "Please select a photo to move.");
            return;
        }
    
        // Prompt user to select the destination album
        List<String> albumNames = new ArrayList<>();
        for (Album album : user.getAlbums()) {
            if (!album.equals(this.album)) { // Exclude the current album
                albumNames.add(album.getName());
            }
        }
    
        if (albumNames.isEmpty()) {
            showAlert("Error", "No other albums available to move the photo.");
            return;
        }
    
        ChoiceDialog<String> albumDialog = new ChoiceDialog<>(albumNames.get(0), albumNames);
        albumDialog.setTitle("Move Photo");
        albumDialog.setHeaderText("Select Destination Album");
        albumDialog.setContentText("Album:");
        Optional<String> albumResult = albumDialog.showAndWait();
    
        if (!albumResult.isPresent()) {
            return; // User canceled the dialog
        }
    
        String destinationAlbumName = albumResult.get();
        Album destinationAlbum = null;
    
        // Find the destination album
        for (Album album : user.getAlbums()) {
            if (album.getName().equals(destinationAlbumName)) {
                destinationAlbum = album;
                break;
            }
        }
    
        if (destinationAlbum == null) {
            showAlert("Error", "Failed to find the selected album.");
            return;
        }
    
        // Check if the photo already exists in the destination album
        if (destinationAlbum.getPhotos().contains(selectedPhoto)) {
            showAlert("Error", "The photo already exists in the selected album.");
            return;
        }
    
        // Prompt user to decide whether to delete the original photo
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Move Photo");
        confirmationDialog.setHeaderText("Do you want to delete the original photo?");
        confirmationDialog.setContentText("Check the box below if you want to delete the original photo after moving it.");
    
        javafx.scene.control.CheckBox deleteOriginalCheckBox = new javafx.scene.control.CheckBox("Delete original photo");
        confirmationDialog.getDialogPane().setContent(deleteOriginalCheckBox);
    
        Optional<javafx.scene.control.ButtonType> confirmationResult = confirmationDialog.showAndWait();
    
        if (confirmationResult.isEmpty() || confirmationResult.get() != javafx.scene.control.ButtonType.OK) {
            return; // User canceled the dialog
        }
    
        // Move or copy the photo
        destinationAlbum.addPhoto(selectedPhoto);
    
        if (deleteOriginalCheckBox.isSelected()) {
            album.removePhoto(selectedPhoto);
            photoListView.getItems().remove(selectedPhoto);
            showAlert("Success", "Photo moved successfully to album: " + destinationAlbumName + " and the original was deleted.");
        } else {
            showAlert("Success", "Photo copied successfully to album: " + destinationAlbumName + ". The original remains in the current album.");
        }
    }

    @FXML
    private void handleOpenPhoto() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto == null) {
            showAlert("Error", "Please select a photo to view.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PhotoView.fxml"));
            Parent root = loader.load();

            PhotoController controller = loader.getController();
            controller.setStage(stage);
            controller.setAlbum(album, album.getPhotos().indexOf(selectedPhoto));

            Scene scene = new Scene(root, 800, 600);
            stage.setTitle("Photo Viewer");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the photo view.");
        }
    }

    /**
     * Handles the "Search Photos" button action.
     */
    @FXML
    private void handleSearchPhotos() {
        // Prompt user to choose a search type
        List<String> searchOptions = Arrays.asList("Date Range", "Single Tag", "Conjunctive Tags (AND)", "Disjunctive Tags (OR)");
        ChoiceDialog<String> searchDialog = new ChoiceDialog<>("Date Range", searchOptions);
        searchDialog.setTitle("Search Photos");
        searchDialog.setHeaderText("Select Search Type");
        searchDialog.setContentText("Search Type:");
        Optional<String> searchTypeResult = searchDialog.showAndWait();
    
        if (!searchTypeResult.isPresent()) {
            return; // User canceled the dialog
        }
    
        String searchType = searchTypeResult.get();
    
        switch (searchType) {
            case "Date Range":
                searchByDateRange();
                break;
            case "Single Tag":
                searchBySingleTag();
                break;
            case "Conjunctive Tags (AND)":
                searchByConjunctiveTags();
                break;
            case "Disjunctive Tags (OR)":
                searchByDisjunctiveTags();
                break;
            default:
                showAlert("Error", "Invalid search type selected.");
        }
    }

    /**
     * Handles the "Date Range" option for HandleSearchPhotos.
     */
    private void searchByDateRange() {
        // Prompt user for start date
        TextInputDialog startDateDialog = new TextInputDialog();
        startDateDialog.setTitle("Search by Date Range");
        startDateDialog.setHeaderText("Enter Start Date (YYYY-MM-DD):");
        Optional<String> startDateResult = startDateDialog.showAndWait();

        if (!startDateResult.isPresent() || startDateResult.get().trim().isEmpty()) {
            showAlert("Error", "Start date cannot be empty.");
            return;
        }

        // Prompt user for end date
        TextInputDialog endDateDialog = new TextInputDialog();
        endDateDialog.setTitle("Search by Date Range");
        endDateDialog.setHeaderText("Enter End Date (YYYY-MM-DD):");
        Optional<String> endDateResult = endDateDialog.showAndWait();

        if (!endDateResult.isPresent() || endDateResult.get().trim().isEmpty()) {
            showAlert("Error", "End date cannot be empty.");
            return;
        }

        try {
            LocalDateTime startDate = LocalDateTime.parse(startDateResult.get().trim() + "T00:00:00");
            LocalDateTime endDate = LocalDateTime.parse(endDateResult.get().trim() + "T23:59:59");

            // Filter photos by date range
            List<Photo> matchingPhotos = new ArrayList<>();
            for (Photo photo : album.getPhotos()) {
                if (!photo.getLastModifiedDate().isBefore(startDate) && !photo.getLastModifiedDate().isAfter(endDate)) {
                    matchingPhotos.add(photo);
                }
            }

            displaySearchResults(matchingPhotos, "No photos found in the specified date range.");
        } catch (Exception e) {
            showAlert("Error", "Invalid date format. Please use YYYY-MM-DD.");
        }
    }

    /**
     * Handles the "Single Tag" option for HandleSearchPhotos.
     */
    private void searchBySingleTag() {
        // Predefined tag types
        List<String> predefinedTagTypes = Arrays.asList("location", "person", "activity");

        // Prompt user to select a tag type
        ChoiceDialog<String> tagTypeDialog = new ChoiceDialog<>("location", predefinedTagTypes);
        tagTypeDialog.setTitle("Search by Single Tag");
        tagTypeDialog.setHeaderText("Select Tag Type:");
        tagTypeDialog.setContentText("Tag Type:");
        Optional<String> tagTypeResult = tagTypeDialog.showAndWait();

        if (!tagTypeResult.isPresent()) {
            showAlert("Error", "Tag type selection canceled.");
            return;
        }

        String tagType = tagTypeResult.get();

        // Prompt user for tag value
        TextInputDialog tagValueDialog = new TextInputDialog();
        tagValueDialog.setTitle("Search by Single Tag");
        tagValueDialog.setHeaderText("Enter Tag Value:");
        tagValueDialog.setContentText("Tag Value:");
        Optional<String> tagValueResult = tagValueDialog.showAndWait();

        if (!tagValueResult.isPresent() || tagValueResult.get().trim().isEmpty()) {
            showAlert("Error", "Tag value cannot be empty.");
            return;
        }

        String tagValue = tagValueResult.get().trim();

        // Filter photos by single tag
        List<Photo> matchingPhotos = new ArrayList<>();
        for (Photo photo : album.getPhotos()) {
            for (Tag tag : photo.getTags()) {
                if (tag.getName().equals(tagType) && tag.getValue().equals(tagValue)) {
                    matchingPhotos.add(photo);
                    break;
                }
            }
        }

        displaySearchResults(matchingPhotos, "No photos found with the specified tag.");
    }

    /**
     * Handles the "Tag AND Tag" option for HandleSearchPhotos.
     */
    private void searchByConjunctiveTags() {
        // Prompt user for first tag type and value
        String[] firstTag = promptForTag("First Tag");
        if (firstTag == null) return;

        // Prompt user for second tag type and value
        String[] secondTag = promptForTag("Second Tag");
        if (secondTag == null) return;

        // Filter photos by conjunctive tags
        List<Photo> matchingPhotos = new ArrayList<>();
        for (Photo photo : album.getPhotos()) {
            boolean hasFirstTag = false, hasSecondTag = false;
            for (Tag tag : photo.getTags()) {
                if (tag.getName().equals(firstTag[0]) && tag.getValue().equals(firstTag[1])) {
                    hasFirstTag = true;
                }
                if (tag.getName().equals(secondTag[0]) && tag.getValue().equals(secondTag[1])) {
                    hasSecondTag = true;
                }
            }
            if (hasFirstTag && hasSecondTag) {
                matchingPhotos.add(photo);
            }
        }

        displaySearchResults(matchingPhotos, "No photos found with the specified tags (AND).");
    }

    /**
     * Handles the "Tag OR Tag" option for HandleSearchPhotos.
     */
    private void searchByDisjunctiveTags() {
        // Prompt user for first tag type and value
        String[] firstTag = promptForTag("First Tag");
        if (firstTag == null) return;

        // Prompt user for second tag type and value
        String[] secondTag = promptForTag("Second Tag");
        if (secondTag == null) return;

        // Filter photos by disjunctive tags
        List<Photo> matchingPhotos = new ArrayList<>();
        for (Photo photo : album.getPhotos()) {
            boolean hasFirstTag = false, hasSecondTag = false;
            for (Tag tag : photo.getTags()) {
                if (tag.getName().equals(firstTag[0]) && tag.getValue().equals(firstTag[1])) {
                    hasFirstTag = true;
                }
                if (tag.getName().equals(secondTag[0]) && tag.getValue().equals(secondTag[1])) {
                    hasSecondTag = true;
                }
            }
            if (hasFirstTag || hasSecondTag) {
                matchingPhotos.add(photo);
            }
        }

        displaySearchResults(matchingPhotos, "No photos found with the specified tags (OR).");
    }

    /**
     * Prompts the user to enter a tag type and value. Used for single tag, conjunctive tag, and disjunctive tag searches.
     * 
     * @param tagPrompt the prompt message for the tag type
     * @return an array containing the tag type and value, or null if the user cancels
     */
    private String[] promptForTag(String tagPrompt) {
        // Predefined tag types
        List<String> predefinedTagTypes = Arrays.asList("location", "person", "activity");

        // Prompt user to select a tag type
        ChoiceDialog<String> tagTypeDialog = new ChoiceDialog<>("location", predefinedTagTypes);
        tagTypeDialog.setTitle("Search by Tag");
        tagTypeDialog.setHeaderText("Select " + tagPrompt + " Type:");
        tagTypeDialog.setContentText("Tag Type:");
        Optional<String> tagTypeResult = tagTypeDialog.showAndWait();

        if (!tagTypeResult.isPresent()) {
            showAlert("Error", tagPrompt + " type selection canceled.");
            return null;
        }

        String tagType = tagTypeResult.get();

        // Prompt user for tag value
        TextInputDialog tagValueDialog = new TextInputDialog();
        tagValueDialog.setTitle("Search by Tag");
        tagValueDialog.setHeaderText("Enter " + tagPrompt + " Value:");
        tagValueDialog.setContentText("Tag Value:");
        Optional<String> tagValueResult = tagValueDialog.showAndWait();

        if (!tagValueResult.isPresent() || tagValueResult.get().trim().isEmpty()) {
            showAlert("Error", tagPrompt + " value cannot be empty.");
            return null;
        }

        return new String[]{tagType, tagValueResult.get().trim()};
    }

    /**
     * Displays the results of a search.
     * 
     * @param matchingPhotos the list of matching photos
     * @param noResultsMessage the message to display if no results are found
     */
    private void displaySearchResults(List<Photo> matchingPhotos, String noResultsMessage) {
        if (matchingPhotos.isEmpty()) {
            showAlert("No Results", noResultsMessage);
        } else {
            photoListView.setItems(FXCollections.observableArrayList(matchingPhotos));
            showAlert("Search Complete", "Found " + matchingPhotos.size() + " photo(s).");
    
            // Prompt user to create a new album with the search results
            Alert createAlbumDialog = new Alert(Alert.AlertType.CONFIRMATION);
            createAlbumDialog.setTitle("Create New Album");
            createAlbumDialog.setHeaderText("Would you like to create a new album with the search results?");
            createAlbumDialog.setContentText("Click OK to create a new album, or Cancel to skip.");
    
            Optional<javafx.scene.control.ButtonType> result = createAlbumDialog.showAndWait();
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                createNewAlbumFromSearchResults(matchingPhotos);
            }
        }
    }

    /**
     * Displays the results of a search.
     * 
     * @param matchingPhotos the list of matching photos
     */
    private void createNewAlbumFromSearchResults(List<Photo> matchingPhotos) {
        // Prompt user for the new album name
        TextInputDialog albumNameDialog = new TextInputDialog();
        albumNameDialog.setTitle("New Album");
        albumNameDialog.setHeaderText("Enter a name for the new album:");
        albumNameDialog.setContentText("Album Name:");
        Optional<String> albumNameResult = albumNameDialog.showAndWait();
    
        if (!albumNameResult.isPresent() || albumNameResult.get().trim().isEmpty()) {
            showAlert("Error", "Album name cannot be empty.");
            return;
        }
    
        String albumName = albumNameResult.get().trim();
    
        // Check if an album with the same name already exists
        for (Album existingAlbum : user.getAlbums()) {
            if (existingAlbum.getName().equalsIgnoreCase(albumName)) {
                showAlert("Error", "An album with this name already exists.");
                return;
            }
        }
    
        // Create the new album and add the search results
        Album newAlbum = new Album(albumName);
        for (Photo photo : matchingPhotos) {
            newAlbum.addPhoto(photo);
        }
        user.addAlbum(newAlbum);
    
        showAlert("Success", "New album '" + albumName + "' created with " + matchingPhotos.size() + " photo(s).");
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
    @FXML
    private Photo findPhotoByPath(String photoPath) {
        for (Photo photo : album.getPhotos()) {
            if (photo.getFilePath().equals(photoPath)) {
                return photo;
            }
        }
        return null;
    }

    @FXML
    private void handlePhotoDoubleClick(MouseEvent event) {
        // Check if the user double-clicked
        if (event.getClickCount() == 2) {
            Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
            if (selectedPhoto == null) {
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PhotoView.fxml"));
                Parent root = loader.load();

                PhotoController controller = loader.getController();
                controller.setStage(stage);
                controller.setAlbum(album, album.getPhotos().indexOf(selectedPhoto));

                Scene scene = new Scene(root, 800, 600);
                stage.setTitle("Photo Viewer");
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load the photo view.");
            }
        }
    }
}
