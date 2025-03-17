package view;

import java.util.Map;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controls the login view of the photo album application.
 * The login view allows users to log in to the application and redirects to admin or user interface.
 * 
 * @owner Kaileb Cole
 * @owner Maxime Deperrois
 */
public class LoginController {
    @FXML
    private TextField usernameField;

    private Stage stage;

    /**
     * Sets the stage for this controller.
     *
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();

        if (username == null || username.trim().isEmpty()) {
            showAlert("Error", "Please enter a username.");
            return;
        }

        if (username.equals("admin")) {
            // Load the admin view
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminView.fxml"));
                Parent root = loader.load();

                // Get the AdminController and pass the stage to it
                AdminController controller = loader.getController();
                controller.setStage(stage);

                // Set up the scene and stage
                Scene scene = new Scene(root, 600, 400);
                stage.setTitle("Photo Admin Interface");
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Authenticate user
            Map<String, String> users = AdminController.getUsers();
            if (!users.containsKey(username)) {
                showAlert("Error", "Invalid username.");
                return;
            }

            // Load the user view
            try {
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
            }
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