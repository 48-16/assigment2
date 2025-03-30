package Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VinylClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/View/MainViewFXML.fxml"));
        VBox root = loader.load();

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Vinyl Library Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}