package pwj;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PwJ extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("PGMMain.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle ("PwJ");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
