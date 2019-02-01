package pwj;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pwj.usb.USBFunctions;

public class PwJ extends Application {
    private static boolean usbFound = false; //Used so we don't terminate a non-existing USB connection
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("PGMMain.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle ("PwJ");
        stage.show();
    }

    public static void main(String[] args) {
        USBFunctions.usbInit();
        launch(args);
        if (usbFound)
            USBFunctions.usbTerminate();
    }
    
    public static void setUsbFound(boolean usbFound) {
        PwJ.usbFound = usbFound;
    }
    
}
