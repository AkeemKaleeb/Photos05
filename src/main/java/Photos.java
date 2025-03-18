import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Album;
import model.DataManager;
import model.Photo;
import model.User;
import view.LoginController;

/**
 * Represents the main application class for the photo album application.
 * This class initializes the JavaFX application and launches the primary stage
 * 
 * @owner Kaileb Cole
 * @owner Maxime Deperrois
 */
public class Photos extends Application {
    private static final String STOCK_USER = "stock";
    private static final String STOCK_ALBUM = "stock";
    private static final String STOCK_PHOTOS_DIR = "/data/stockPhotos";

    /**
     * Starts the JavaFX application and launches the primary stage
     * 
     * @param primaryStage the primary stage for the application
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO: Check if stock user exists already, if not, create it
        loadStockUser();

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

    private void loadStockUser() {
        User stockUser = new User(STOCK_USER);
        Album stockAlbum = new Album(STOCK_ALBUM);

        File stockPhotosDir = new File(STOCK_PHOTOS_DIR);
        if(stockPhotosDir.exists() && stockPhotosDir.isDirectory()) {
            for(File file : stockPhotosDir.listFiles()) {
                if(file.isFile() && (file.getName().endsWith(".jpg") || 
                                    file.getName().endsWith(".jpeg") || 
                                    file.getName().endsWith(".png") || 
                                    file.getName().endsWith(".gif") ||
                                    file.getName().endsWith(".bmp"))) {
                    try {
                        Photo photo = new Photo(file.getAbsolutePath());
                        stockAlbum.addPhoto(photo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        stockUser.addAlbum(stockAlbum);

        // Save the stock user to a file in the project directory
        try {
            DataManager.saveUser(stockUser, Paths.get("data", "stockUser.dat").toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launched by running the program
     * Responsible for initiating the JavaFX Application
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}