package view;
  
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * Controls the admin view of the photo album application.
 * The admin view allows administrators to manage users and albums.
 * 
 * @owner Kaileb Cole
 * @owner Maxime Deperrois
 */
public class AdminController {
    private Stage stage;

    /**
     * Sets the stage for this controller.
     *
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Handles the "List Users" button action.
     */
    @FXML
    private void handleListUsers() {
        showAlert("List Users", "This feature is not yet implemented.");
    }

    /**
     * Handles the "Create User" button action.
     */
    @FXML
    private void handleCreateUser() {
        showAlert("Create User", "This feature is not yet implemented.");
    }

    /**
     * Handles the "Delete User" button action.
     */
    @FXML
    private void handleDeleteUser() {
        showAlert("Delete User", "This feature is not yet implemented.");
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
