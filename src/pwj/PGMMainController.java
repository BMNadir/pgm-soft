package pwj;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pwj.usb.USBFunctions;

public class PGMMainController implements Initializable {
    private boolean programmerFound = false;
    private String hexLastPath = "";
    private File hexFile;
    
    @FXML
    private StackPane rootPane = new StackPane ();
    @FXML
    private Label deviceStatus = new Label();
    @FXML
    private Label hexPath = new Label();
    @FXML
    private Label programmerStatus = new Label();
    @FXML
    private AnchorPane dropArea = new AnchorPane();
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @FXML
    private void loadHex(ActionEvent event) {
        Stage currentStage = (Stage) rootPane.getScene().getWindow();
        
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier HEX", "*.hex"));
        fc.setTitle("Charger HEX");
        
        if (!hexLastPath.isEmpty())
            fc.setInitialDirectory(new File(hexLastPath)); //Open FileChooser at last location
        File file = fc.showOpenDialog(currentStage);
        if (file != null)
        {
            hexLastPath = file.getParent();     //Get directory of last loaded hex
            hexPath.setText(hexLastPath);
        }
    }

    @FXML
    private void handleFileDrag(DragEvent event) {
        if (event.getDragboard().hasFiles()) //Proceed if dragged object is a file
        {
            hexFile = event.getDragboard().getFiles().get(0);
            String hexFileName = hexFile.getName();
            if (hexFileName.substring(hexFileName.lastIndexOf(".")).equals(".hex"))
            {
                event.acceptTransferModes(TransferMode.ANY);
                
            }
        }
    }

    @FXML
    private void handleFileDrop(DragEvent event) {
        hexLastPath = hexFile.getParent();     //Get directory of last loaded hex
        hexPath.setText(hexLastPath);
    }

    @FXML
    private void connectToProgrammer(ActionEvent event) {
        byte[] version = USBFunctions.checkForProgrammer();
        if (version!= null)
        {
            setProgrammerFound(true);
            programmerStatus.setText("Programmateur Connecté, FW version : " + version[1] + "."+ version[2]+ "."+ version[3]);
            //connecterAuProgrammateur.setDisable(true);
        }
        
    }

    public void setProgrammerFound(boolean programmerFound) {
        this.programmerFound = programmerFound;
    }
    
    
}
