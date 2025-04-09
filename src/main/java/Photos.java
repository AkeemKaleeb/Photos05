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
    private static final String STOCK_PHOTOS_DIR = "data/stockPhotos";

    /**
     * Starts the JavaFX application and launches the primary stage
     * 
     * @param primaryStage the primary stage for the application
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Check if stock user exists already, if not, create it
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
        try {
            // Check if the stock user file exists
            String stockUserFilePath = Paths.get("data", "stockUser.dat").toString();
            File stockUserFile = new File(stockUserFilePath);
            

            // Create Stock User and Album
            User stockUser;
            Album stockAlbum;

            if (stockUserFile.exists()) {
                // Load the stock user from the file
                stockUser = DataManager.loadUser(stockUserFilePath);
    
                // Check if the stock album exists
                stockAlbum = stockUser.getAlbums().stream()
                    .filter(album -> album.getName().equals(STOCK_ALBUM))
                    .findFirst()
                    .orElse(null);
    
                if (stockAlbum == null) {
                    // Create the stock album if it doesn't exist
                    stockAlbum = new Album(STOCK_ALBUM);
                    stockUser.addAlbum(stockAlbum);
                }
            } else {
                // Create the stock user and album
                stockUser = new User(STOCK_USER);
                stockAlbum = new Album(STOCK_ALBUM);
                stockUser.addAlbum(stockAlbum);
            }
    
            // Populate the stock album with stock images
            File stockPhotosDir = new File(STOCK_PHOTOS_DIR);
            if (stockPhotosDir.exists() && stockPhotosDir.isDirectory()) {
                for (File file : stockPhotosDir.listFiles()) {
                    if (file.isFile()) {
                        String fileName = file.getName().toLowerCase();
                        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
                            fileName.endsWith(".png") || fileName.endsWith(".bmp") || 
                            fileName.endsWith(".gif")) {
                            Photo photo = new Photo(file.getAbsolutePath());
                            if (!stockAlbum.getPhotos().contains(photo)) {
                                stockAlbum.addPhoto(photo);
                            }
                        }
                    }
                }
            } else {
                System.err.println("Stock photos directory does not exist: " + STOCK_PHOTOS_DIR);
            }
    
            // Save the stock user back to the file
            DataManager.saveUser(stockUser, stockUserFilePath);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Failed to load or initialize the stock user.");
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