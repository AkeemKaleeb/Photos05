package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controls the username prompt dialog.
 * 
 * @author Kaileb Cole
 * @author Maxime Deperrois
 */
public class UsernamePromptController {

    @FXML
    private TextField usernameField;

    private Stage dialogStage;
    private String username;
    private boolean okClicked = false;

    /**
     * Sets the stage for this dialog.
     *
     * @param dialogStage the stage to set
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Returns the username entered by the user.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     *
     * @return true if OK clicked, false otherwise
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Handles the OK button action.
     */
    @FXML
    private void handleOk() {
        username = usernameField.getText();
        okClicked = true;
        dialogStage.close();
    }
}