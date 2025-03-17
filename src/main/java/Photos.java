import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Represents the main application class for the photo album application.
 * This class initializes the JavaFX application and launches the primary stage
 * 
 * @owner Kaileb Cole
 * @owner Maxime Deperrois
 */
public class Photos extends Application {
    @Override
    public void start(Stage primaryStage) {
        System.out.println("Launching Photos Application...");
        primaryStage.setTitle("Photos Application");
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}