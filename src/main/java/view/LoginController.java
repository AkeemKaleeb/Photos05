package view;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

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

    @FXML
    private void handleSubmitButtonAction() {
        String username = usernameField.getText();
        System.out.println("Username: " + username);
        // Add your login logic here
    }
}
