package pwj;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pwj.db.DbUtil;
import pwj.device.DeviceInfo;
import pwj.functions.PwJFunctions;
import pwj.usb.USBFunctions;
import pwj.inter.IDefinitions;
import pwj.ui.Prompt;

public class PGMMainController implements Initializable, IDefinitions{
    private boolean programmerFound = false;
    private static byte activeFamily = 0;
    private static int activeDevice = 0;
    private static DeviceInfo device;
    private String hexLastPath = "";
    private File hexFile;
    ObservableList<MemoryDumpRow> flashList  = FXCollections.observableArrayList ();
    ObservableList<MemoryDumpRow> eepromList = FXCollections.observableArrayList ();
    
    @FXML
    private StackPane rootPane = new StackPane ();
    @FXML
    private AnchorPane rootAnchorPane= new AnchorPane();
    @FXML
    private Label deviceStatus = new Label();
    @FXML
    private Label hexPath = new Label();
    @FXML
    private Label programmerStatus = new Label();
    @FXML
    private AnchorPane dropArea = new AnchorPane();
    @FXML
    private MenuItem connectMenuItem = new MenuItem();
    @FXML
    private MenuItem disconnectMenuItem = new MenuItem();
    @FXML
    private TableView<MemoryDumpRow> flashTable = new TableView<MemoryDumpRow>();
    @FXML
    private TableColumn<MemoryDumpRow, String> flashAddrCol = new TableColumn<MemoryDumpRow, String>();
    @FXML
    private TableColumn<MemoryDumpRow, String> flashAddr0 = new TableColumn<MemoryDumpRow, String>();
    @FXML
    private TableColumn<MemoryDumpRow, String> flashAddr1 = new TableColumn<MemoryDumpRow, String>();
    @FXML
    private TableColumn<MemoryDumpRow, String> flashAddr2 = new TableColumn<MemoryDumpRow, String>();
    @FXML
    private TableColumn<MemoryDumpRow, String> flashAddr3 = new TableColumn<MemoryDumpRow, String>();
    @FXML
    private TableColumn<MemoryDumpRow, String> flashAddr4 = new TableColumn<MemoryDumpRow, String>();
    @FXML
    private TableColumn<MemoryDumpRow, String> flashAddr5 = new TableColumn<MemoryDumpRow, String>();
    @FXML
    private TableColumn<MemoryDumpRow, String> flashAddr6 = new TableColumn<MemoryDumpRow, String>();
    @FXML
    private TableColumn<MemoryDumpRow, String> flashAddr7 = new TableColumn<MemoryDumpRow, String>();
    @FXML
    private TableView<MemoryDumpRow> eepromTable;
    @FXML
    private TableColumn<MemoryDumpRow, String> eepromAddrCol;
    @FXML
    private TableColumn<MemoryDumpRow, String> eepromAddr0;
    @FXML
    private TableColumn<MemoryDumpRow, String> eepromAddr1;
    @FXML
    private TableColumn<MemoryDumpRow, String> eepromAddr2;
    @FXML
    private TableColumn<MemoryDumpRow, String> eepromAddr3;
    @FXML
    private TableColumn<MemoryDumpRow, String> eepromAddr4;
    @FXML
    private TableColumn<MemoryDumpRow, String> eepromAddr5;
    @FXML
    private TableColumn<MemoryDumpRow, String> eepromAddr6;
    @FXML
    private TableColumn<MemoryDumpRow, String> eepromAddr7;
    @FXML
    private MenuItem detectPICMenuItem = new MenuItem();
    
    
    public static class MemoryDumpRow 
    {
        private static int addr = -8; //So that the first line has the address 0
        private final SimpleStringProperty addrCol;
        private final SimpleStringProperty addr0;
        private final SimpleStringProperty addr1;
        private final SimpleStringProperty addr2;
        private final SimpleStringProperty addr3;
        private final SimpleStringProperty addr4;
        private final SimpleStringProperty addr5;
        private final SimpleStringProperty addr6;
        private final SimpleStringProperty addr7;

        public MemoryDumpRow(String addr0, String addr1, String addr2, String addr3, String addr4, String addr5, String addr6, String addr7) 
        {
            addr += 8;
            this.addrCol = new SimpleStringProperty (Integer.toHexString(addr));
            this.addr0 = new SimpleStringProperty (addr0);
            this.addr1 = new SimpleStringProperty (addr1);
            this.addr2 = new SimpleStringProperty (addr2);
            this.addr3 = new SimpleStringProperty (addr3);
            this.addr4 = new SimpleStringProperty (addr4);
            this.addr5 = new SimpleStringProperty (addr5);
            this.addr6 = new SimpleStringProperty (addr6);
            this.addr7 = new SimpleStringProperty (addr7);
        }

        public String getAddrCol() {
            return addrCol.get().toUpperCase();
        }

        public String getAddr0() {
            return addr0.get();
        }

        public String getAddr1() {
            return addr1.get();
        }

        public String getAddr2() {
            return addr2.get();
        }

        public String getAddr3() {
            return addr3.get();
        }

        public String getAddr4() {
            return addr4.get();
        }

        public String getAddr5() {
            return addr5.get();
        }

        public String getAddr6() {
            return addr6.get();
        }

        public String getAddr7() {
            return addr7.get();
        }
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /**************************************************************************/
    /**                             METHODS                                  **/
    /**************************************************************************/
    /**************************************************************************/
    /**************************************************************************/
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DbUtil.dbConnection();                                  // Connect to DB
        connectMenuItem.fire();                                 // Start connectToProgrammer routine
        initFlashDump ();
        MemoryDumpRow.addr = -8;                                // So that the first address will be equal to 0
        initEEPROMDump ();
        flashAddrCol.getStyleClass().add("address-column");     // Change background color of address column in Flash memory dump
        flashTable.getStyleClass().add("noheader");             // Remove column headers from Flash memory dump
        eepromAddrCol.getStyleClass().add("address-column");    // Change background color of address column in EEPROM memory dump
        eepromTable.getStyleClass().add("noheader");            // Remove column headers from EEPROM memory dump
    }    

    public static void setActiveFamily(byte activeFamilyId) {
        PGMMainController.activeFamily = activeFamilyId;
    }

    public static void setActiveDevice(int activeDevice) {
        PGMMainController.activeDevice = activeDevice;
    }

    public static int getActiveDevice() {
        return activeDevice;
    }
    
    
    @FXML
    private void loadHex(ActionEvent event) {
        Stage currentStage = (Stage) rootPane.getScene().getWindow();
        
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier HEX", "*.hex"));
        fc.setTitle("Charger HEX");
        
        if (!hexLastPath.isEmpty())
            fc.setInitialDirectory(new File(hexLastPath)); // Open FileChooser at last location
        File file = fc.showOpenDialog(currentStage);
        if (file != null)
        {
            // Get directory of last loaded hex
            hexLastPath = file.getParent();     
            hexPath.setText(hexLastPath);
        }
    }

    @FXML
    private void handleFileDrag(DragEvent event) {
        if (event.getDragboard().hasFiles()) // Proceed if dragged object is a file
        {
            hexFile = event.getDragboard().getFiles().get(0);
            String hexFileName = hexFile.getName();
            // Accept only files with .HEX extension
            if (hexFileName.substring(hexFileName.lastIndexOf(".")).equals(".hex"))
            {
                event.acceptTransferModes(TransferMode.ANY);
                
            }
        }
    }

    @FXML
    private void handleFileDrop(DragEvent event) {
        hexLastPath = hexFile.getParent();      // Get directory of last loaded hex
        hexPath.setText(hexLastPath);           // Display the path in the label
    }

    @FXML
    private void connectToProgrammer(ActionEvent event) {
        byte[] version = USBFunctions.checkForProgrammer();
        if (version!= null)
        {
            setProgrammerFound(true);
            programmerStatus.setText("Programmateur connecté, FW Vr: " + version[1] + ".0"+ version[2]+ "."+ version[3]);
            disconnectMenuItem.setDisable(false);
            connectMenuItem.setDisable(true);
            
            pwjInit ();
            
            /***** FOR TESTING ONLY *****/
            /*
            byte[] cmd = new byte [17];
            cmd [0] = SET_VPP;
            cmd [1] = 0x40;
            cmd [2] = (byte) (12 * 18.61);
            cmd [3] = (byte) (12 * 0.7 * 18.61);
            
            cmd [4] = RUN_ROM_SCRIPT;
            cmd [5] = 17;
            cmd [6] = 0x3E;
            cmd [7] = 0x61;
            
            cmd [8] = RUN_ROM_SCRIPT;
            cmd [9] = 18;
            cmd [10] = (byte) 0x8A;
            cmd [11] = 0x61;
            
            cmd [12] = RUN_ROM_SCRIPT;
            cmd [13] = 7;
            cmd [14] = (byte) 0x3C;
            cmd [15] = 0x60;
            cmd [16] = UPLOAD;
            USBFunctions.hidWrite(cmd);
            byte[] response = new byte[10];
            while (programmer.read(response, 500) < 0);
            
            for (byte bt : response)
               System.out.println(bt);
            System.out.println();
            /*
            /*****************************/
        }
        
    }

    public void setProgrammerFound(boolean programmerFound) {
        this.programmerFound = programmerFound;
    }

    @FXML
    private void disconnectFromProgrammer(ActionEvent event) {
        USBFunctions.usbTerminate();
        connectMenuItem.setDisable(false);
        disconnectMenuItem.setDisable(true);
        setProgrammerFound(false);
        programmerStatus.setText("Programmateur déconnecté");
        PwJ.setUsbFound(false);
    }
    
    private void initFlashDump () 
    {
        flashAddrCol.setCellValueFactory(new PropertyValueFactory<>("addrCol")); //Associate addr member of MemoryDumpRow with flashAddrCol table column
        flashAddr0.setCellValueFactory(new PropertyValueFactory<>("addr0")); 
        flashAddr1.setCellValueFactory(new PropertyValueFactory<>("addr1")); 
        flashAddr2.setCellValueFactory(new PropertyValueFactory<>("addr2")); 
        flashAddr3.setCellValueFactory(new PropertyValueFactory<>("addr3")); 
        flashAddr4.setCellValueFactory(new PropertyValueFactory<>("addr4")); 
        flashAddr5.setCellValueFactory(new PropertyValueFactory<>("addr5")); 
        flashAddr6.setCellValueFactory(new PropertyValueFactory<>("addr6")); 
        flashAddr7.setCellValueFactory(new PropertyValueFactory<>("addr7")); 
        
        for (int i = 0; i < 50; i++)
        {
            flashList.add(new MemoryDumpRow("FFFF", "FFFF", "FFFF", "FFFF", "FFFF", "FFFF", "FFFF", "FFFF"));
        }
        flashTable.getItems().setAll(flashList);
    }
    
    private void initEEPROMDump ()
    {
        eepromAddrCol.setCellValueFactory(new PropertyValueFactory<>("addrCol")); //Associate addr member of MemoryDumpRow with flashAddrCol table column
        eepromAddr0.setCellValueFactory(new PropertyValueFactory<>("addr0")); 
        eepromAddr1.setCellValueFactory(new PropertyValueFactory<>("addr1")); 
        eepromAddr2.setCellValueFactory(new PropertyValueFactory<>("addr2")); 
        eepromAddr3.setCellValueFactory(new PropertyValueFactory<>("addr3")); 
        eepromAddr4.setCellValueFactory(new PropertyValueFactory<>("addr4")); 
        eepromAddr5.setCellValueFactory(new PropertyValueFactory<>("addr5")); 
        eepromAddr6.setCellValueFactory(new PropertyValueFactory<>("addr6")); 
        eepromAddr7.setCellValueFactory(new PropertyValueFactory<>("addr7")); 
        
        for (int i = 0; i < 20; i++)
        {
            eepromList.add(new MemoryDumpRow("FFFF", "FFFF", "FFFF", "FFFF", "FFFF", "FFFF", "FFFF", "FFFF"));
        }
        eepromTable.getItems().setAll(eepromList);
    }
    
    private boolean interfaceCheck ()
    {
        // Return if programmer not present  
        if (USBFunctions.checkForProgrammer() == null)
        {
            Prompt.alert("Programmateur non connecté", rootPane, rootAnchorPane);
            
            // Disconnect from the programmer if previously connected
            if (programmerFound)
                disconnectMenuItem.fire();      
            return false;
        }
        // Return if no device has been found 
        if (activeFamily == 0)
        {
            Prompt.alert("Aucun PIC n'a été détecté", rootPane, rootAnchorPane);
            return false;
        }
        
        return true;
    }
    
    // Initialize Programmer 
    private static void pwjInit ()
    {
        // Make sure VDD is off
        byte[] script = {VDD_OFF};
        PwJFunctions.sendScript(script);
        
        // Initialize VDD to 3.3V
        // PwJFunctions.setVdd(3.3F, 0.85f); 
        PwJFunctions.checkForPoweredDevice ();
        PwJFunctions.identifyDevice ();
    }
    
    @FXML
    private void detectPIC(ActionEvent event) {
        PwJFunctions.identifyDevice ();
    }
    
    @FXML
    private void writePIC(ActionEvent event) 
    {
        if (!interfaceCheck())  return;
        
        // Call deviceWrite in PwJFunctions
    }
    
}
