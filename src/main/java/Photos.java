import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import view.LoginController;

/**
 * Represents the main application class for the photo album application.
 * This class initializes the JavaFX application and launches the primary stage
 * 
 * @owner Kaileb Cole
 * @owner Maxime Deperrois
 */
public class Photos extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("FXML Path: " + getClass().getResource("/view/LoginView.fxml"));
        // Load the FXML File
        FXMLLoader loader= new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
        Parent root = loader.load();

        // Get the LoginController and pass the stage to it
        LoginController controller = loader.getController();
        controller.setStage(primaryStage);

        // Set up the scene and stage
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Photo Album Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}