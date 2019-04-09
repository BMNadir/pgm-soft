package pwj;

import com.jfoenix.controls.JFXButton;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
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

public class PGMMainController implements Initializable, IDefinitions {
    
    private boolean programmerFound = false;
    private static boolean deviceFound = false;
    private static byte activeFamily = 0;
    private static int activeDevice = 0;
    private static DeviceInfo device;
    
    private String hexLastPath = "";
    private File hexFile;
    private long lastModified = 0;
    
    ObservableList<MemoryDumpRow> flashList  = FXCollections.observableArrayList ();
    ObservableList<MemoryDumpRow> eepromList = FXCollections.observableArrayList ();
    
    @FXML
    private StackPane rootPane = new StackPane ();
    @FXML
    private AnchorPane rootAnchorPane= new AnchorPane();
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
    @FXML
    private Label picNameCard = new Label();
    @FXML
    private Label deviceIdLabel = new Label();
    @FXML
    private Label familyLabel = new Label();
    @FXML
    private Label romLabel = new Label();
    @FXML
    private Label eepromLabel = new Label();
    @FXML
    private Label vddLabel = new Label();
    @FXML
    private Label vppLabel = new Label();
    @FXML
    private Tab controlsTab = new Tab();
    @FXML
    private JFXButton writeBtn = new JFXButton();
    @FXML
    private JFXButton readBtn = new JFXButton();
    @FXML
    private JFXButton verifyBtn = new JFXButton();
    @FXML
    private JFXButton eraseBtn = new JFXButton();
    @FXML
    private Tab progMemTab = new Tab();
    @FXML
    private Tab eepromTab = new Tab();
    @FXML
    private CheckMenuItem verifyAfterWrite = new CheckMenuItem();

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
            this.addrCol = new SimpleStringProperty (String.format("%1$06X",addr));
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
    /**
     * @param url*
     * @param rb***********************************************************************/
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

    public static void setDevice(DeviceInfo device) 
    {
        PGMMainController.device = device;
    }
    
    public static void setDeviceFound(boolean deviceFound) {
        PGMMainController.deviceFound = deviceFound;
    }
    
    @FXML
    private void loadHex(ActionEvent event) {
        Stage currentStage = (Stage) rootPane.getScene().getWindow();
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier HEX", "*.hex"));
        fc.setTitle("Charger HEX");
        
        if (!hexLastPath.isEmpty())
            fc.setInitialDirectory(new File(hexLastPath)); // Open FileChooser at last location
        hexFile = fc.showOpenDialog(currentStage);
        if (hexFile != null)
        {
            // Get directory of last loaded hex
            lastModified = hexFile.lastModified();
            hexLastPath = hexFile.getParent();     
            hexPath.setText(hexLastPath);
        }
    }
    
    private boolean importHex()
    {
        int addressbase = 0;
        boolean lineExceedsFlash = false;
        boolean configRead = false;
        boolean fileExceedsFlash = false;
        int progMemSizeBytes = device.getProgMemSize() * device.getProgMemHexBytes();
        int[] progMemBuffer = device.getProgMem();
        int[] eeMemBuffer = device.getEepromMem();
        int[] configWordsBuff = device.getConfig();
        int[] userIdBuffer = device.getUserID();
        int eeMemAddress = device.getEeMemAddr();
        byte eeMemBytes = device.getEeMemHexBytes();
        
        // Clear device buffers
        
        if (device.getUserIdAddr() == 0)
            device.setUserIdAddr(0xFFFFFFFF);
        
        // Initialize config words to blank value
        int[] configs = device.getConfig();
        int[] configMasks = device.getConfigMasks();
        int[] configBlanks = device.getConfigBlanks();
        boolean[] configMaskImplemented = new boolean[8];
        for (byte cw = 0; cw < configs.length; cw++)
        {
            configs[cw] = device.getBlankValue();
            if (configMasks[cw] == 0)
                configMaskImplemented [cw] = true; // if mask is blank (no implemented bits) don't need it in file
            else 
                configMaskImplemented [cw] = false;
        }
        
        // Source: https://stackoverflow.com/questions/5868369/how-to-read-a-large-text-file-line-by-line-using-java
        try (BufferedReader hexReader = new BufferedReader(new FileReader(hexFile))) 
        {
            String hexLine;
            while ((hexLine = hexReader.readLine()) != null) 
            {
               // process the hexLine
               //Line format in Intel Hex Format is  :BBAAAATTCC
               if (hexLine.charAt(0) == ':' && hexLine.length() > 10)
               {    
                   int byteCount = Integer.parseInt(hexLine.substring(1, 3), 16); // BB
                   int fileAddress = addressbase + Integer.parseInt(hexLine.substring(3, 7), 16); // AAAA
                   int lineType = Integer.parseInt(hexLine.substring(7, 9), 16);    // TT
                   
                   if (lineType == 0)    // Data
                   {
                       if (hexLine.length() >= (11 + 2*byteCount))
                       {
                           for (int lineByte = 0; lineByte < byteCount; lineByte++)
                           {
                               int byteAddress = fileAddress + lineByte;
                               int arrayAddress = byteAddress / device.getProgMemHexBytes();
                               int bytePosition = byteAddress % device.getProgMemHexBytes();
                               int wordByte =  0xFFFFFF00 | Integer.parseInt(hexLine.substring(9+(2*lineByte), 11+(2*lineByte)), 16);
                               for (byte shift = 0; shift < bytePosition; shift++)
                                { // Shift byte into proper position
                                    wordByte <<= 8;
                                    wordByte |= 0xFF; // Shift in ones.
                                }
                               lineExceedsFlash = true; // If not in any memory section, then error
                               
                               /* PROGRAM MEMORY */
                               if (byteAddress >= 0 && byteAddress < progMemSizeBytes)
                               {
                                   progMemBuffer[arrayAddress] &= wordByte;
                                   //System.out.println(Integer.toHexString(progMemBuffer[arrayAddress]));
                                   lineExceedsFlash = false;
                               }
                               
                               /* EEPROM */
                               if ((eeMemAddress > 0) && (byteAddress >= eeMemAddress) && (device.getEeMemSize() > 0))
                               {
                                   int eeAddress = (byteAddress - eeMemAddress) / eeMemBytes;
                                   if (eeAddress < device.getEeMemSize())
                                   {
                                       lineExceedsFlash = false;
                                       if (eeMemBytes == device.getProgMemHexBytes())
                                       {
                                           eeMemBuffer[eeAddress] &= wordByte;
                                       }
                                       else 
                                       {
                                           int eeShift = (bytePosition / eeMemBytes) * eeMemBytes;
                                           for (byte shift = 0; shift < eeShift; shift++)
                                           {
                                               wordByte >>= 8;
                                           }
                                           eeMemBuffer[eeAddress] &= wordByte;
                                       }
                                   }
                               }
                               else if ((byteAddress >= eeMemAddress) && (eeMemAddress > 0) && (device.getEeMemSize() == 0))
                               {
                                   lineExceedsFlash = false;
                               }
                               
                               /* CONFIG WORDS */
                               if (byteAddress >= device.getConfigAddr() && device.getConfigWords() > 0)
                               {
                                   int configNum = (byteAddress - device.getConfigAddr()) / device.getProgMemHexBytes();
                                   if (configNum < device.getConfigWords())
                                   {
                                       lineExceedsFlash = false;
                                       configRead = true;
                                       configMaskImplemented[configNum] = true;
                                       configWordsBuff[configNum] &= wordByte; 
                                       if (device.getBlankValue() == 0xFFF)
                                       {
                                           configWordsBuff[configNum] |= configBlanks[5];
                                       }
                                       if (byteAddress < progMemSizeBytes)
                                       {
                                           int orMask = 0;
                                           if (device.getBlankValue() == 0xFFFF)    //PIC18J
                                           {
                                               orMask = 0xF000;
                                           }
                                           else 
                                           {
                                               orMask = (0xFF0000 | configBlanks[configNum]) & ~configMasks[configNum];
                                           }
                                           progMemBuffer[arrayAddress] &= (wordByte & configBlanks[configNum]);
                                           progMemBuffer[arrayAddress] |= orMask;
                                       }
                                   }
                               }
                               
                               /* USER IDs */
                               if (device.getUserIDs() > 0)
                               {
                                   if (byteAddress >= device.getUserIdAddr())
                                   {
                                       int uIDAddress = byteAddress - device.getUserIdAddr() / device.getUserIdHexBytes();
                                       if (uIDAddress < device.getUserIDs())
                                       {
                                           lineExceedsFlash = false;
                                           if (device.getUserIdHexBytes() == device.getProgMemHexBytes())
                                           {
                                               userIdBuffer[uIDAddress] &= wordByte;
                                           }
                                           else 
                                           {
                                               int uIDSshift = (bytePosition / device.getUserIdHexBytes()) * device.getUserIdHexBytes();
                                               for (byte reshift = 0; reshift < uIDSshift; reshift++)
                                               {
                                                   wordByte >>= 8;
                                               }
                                               userIdBuffer[uIDAddress] &= wordByte;
                                           }
                                       }
                                   }
                               }
                               
                               /* INGNORE BYTE */
                               if (device.getIgnoreBytes() > 0)
                               {
                                   if (byteAddress >= device.getIgnoreAddres())
                                   {
                                       if (byteAddress < (device.getIgnoreAddres() + device.getIgnoreBytes()))
                                       {
                                           lineExceedsFlash = false;
                                       }
                                   }
                               }
                           }
                       }
                       if (lineExceedsFlash)
                       {
                            fileExceedsFlash = true;
                       }
                   } // End if (lineType == 0)
                   
                   if (lineType == 2 || lineType == 4)
                   {
                      if (hexLine.length() >= (11 + (2 * byteCount))) 
                      {
                          addressbase = Integer.parseInt(hexLine.substring(9,13), 16);
                      }
                      // For compilers that use a linear address
                      if (lineType == 2)
                      {
                          addressbase <<= 4;
                      }
                      else
                      {
                          // Extended address
                          addressbase <<= 16;
                      }
                   }
                   if (lineType == 1)
                   {
                       break;
                   }
               }
            }
            hexReader.close();
            
            // Get OSCCAL
            if (device.getOsccalSave())
            {
                PwJFunctions.readOSSCAL(device.getProgMemSize() - 1);
            }
            device.setProgMem(progMemBuffer);
            device.setEepromMem(eeMemBuffer);
            device.setConfig(configs);
            device.setUserID(userIdBuffer);
            
            if (fileExceedsFlash)
            {
                Platform.runLater(() -> {
                    Prompt.alert("Le fichier hex contient des adresses invalides pour le "+device.getName(), rootPane, rootAnchorPane);
                });
                return false;
            }
        } catch (IOException e) 
        {
            Platform.runLater(() -> {
                Prompt.alert("Fichier hex n'a pas pu être chargé", rootPane, rootAnchorPane);
            });
            return false;
        }
        return true;
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
        if (deviceFound)
            importHex();
    }

    @FXML
    private void connectToProgrammer(ActionEvent event) {
        byte[] version = USBFunctions.checkForProgrammer();
        if (version!= null && version[0] == 3)
        {
            setProgrammerFound(true);
            programmerStatus.setText("Programmateur connecté, FW Vr: " + version[1] + "."+ version[2]+ "."+ version[3]);
            disconnectMenuItem.setDisable(false);
            connectMenuItem.setDisable(true);
            detectPICMenuItem.setDisable(false);
            pwjInit ();
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
        detectPICMenuItem.setDisable(true);
        setProgrammerFound(false);
        programmerStatus.setText("Programmateur déconnecté");
        PwJ.setUsbFound(false);
        resetUI();
    }
    
    private void updateUI() {
        picNameCard.setText(device.getName());
        deviceIdLabel.setText("0x"+Integer.toHexString(activeDevice));
        familyLabel.setText(device.getFamilyName());
        vddLabel.setText(Float.toString(device.getVddMax()));
        vppLabel.setText(Float.toString(device.getVpp()));
        romLabel.setText(Integer.toString (device.getProgMemSize() / 1024) + " KB");
        eepromLabel.setText(Integer.toString (device.getEeMemSize()) + " B");
    }
    
    private void resetUI() {
        picNameCard.setText("Aucun PIC Détecté");
        deviceIdLabel.setText("N/A");
        familyLabel.setText("N/A");
        vddLabel.setText("N/A");
        vppLabel.setText("N/A");
        romLabel.setText("N/A");
        eepromLabel.setText("N/A");
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
            eepromList.add(new MemoryDumpRow("FF", "FF", "FF", "FF", "FF", "FF", "FF", "FF"));
        }
        eepromTable.getItems().setAll(eepromList);
    }
    
    private void updateMemoryUI ()
    {
        String word1, word2, word3, word4, word5, word6, word7, word8;
        int[] progMemBuffer = device.getProgMem();
        flashList.clear();
        flashTable.getItems().clear();
        MemoryDumpRow.addr = -8;
        int flashIndex = 0;
        do 
        {
            word1 = String.format("%1$04X",progMemBuffer[flashIndex++]);
            word2 = String.format("%1$04X",progMemBuffer[flashIndex++]);
            word3 = String.format("%1$04X",progMemBuffer[flashIndex++]);
            word4 = String.format("%1$04X",progMemBuffer[flashIndex++]);
            word5 = String.format("%1$04X",progMemBuffer[flashIndex++]);
            word6 = String.format("%1$04X",progMemBuffer[flashIndex++]);
            word7 = String.format("%1$04X",progMemBuffer[flashIndex++]);
            word8 = String.format("%1$04X",progMemBuffer[flashIndex++]);
            flashList.add(new MemoryDumpRow(word1, word2, word3, word4, word5, word6, word7, word8));
        } while (flashIndex < progMemBuffer.length);
        flashTable.getItems().setAll(flashList);
        
        if (device.getEeMemSize() > 0)
        {
            int[] eeMemBuffer = device.getEepromMem();
            eepromList.clear();
            eepromTable.getItems().clear();
            MemoryDumpRow.addr = -8;
            int eeIndex = 0;
            do 
            {
                word1 = String.format("%1$02X",eeMemBuffer[eeIndex++]);
                word2 = String.format("%1$02X",eeMemBuffer[eeIndex++]);
                word3 = String.format("%1$02X",eeMemBuffer[eeIndex++]);
                word4 = String.format("%1$02X",eeMemBuffer[eeIndex++]);
                word5 = String.format("%1$02X",eeMemBuffer[eeIndex++]);
                word6 = String.format("%1$02X",eeMemBuffer[eeIndex++]);
                word7 = String.format("%1$02X",eeMemBuffer[eeIndex++]);
                word8 = String.format("%1$02X",eeMemBuffer[eeIndex++]);
                eepromList.add(new MemoryDumpRow(word1, word2, word3, word4, word5, word6, word7, word8));
            } while (eeIndex < eeMemBuffer.length);
            eepromTable.getItems().setAll(eepromList);
        }
    }
    
    private boolean interfaceCheck ()
    {
        // Return if programmer not present  
        if (USBFunctions.checkForProgrammer() == null)
        {
            Platform.runLater(() -> {
                Prompt.alert("Programmateur non connecté", rootPane, rootAnchorPane);
                // Disconnect from the programmer if previously connected
                if (programmerFound)
                    disconnectMenuItem.fire();
            });
                 
            return false;
        }
        // Return if no device has been found 
        if (activeFamily == 0)
        {
            Platform.runLater(() -> {
                Prompt.alert("Aucun PIC n'a été détecté", rootPane, rootAnchorPane);
            });
            
            return false;
        }
        
        return true;
    }
    
    private boolean hexFileCheck()
    {
        if (hexFile == null)    
        {
            Platform.runLater(() -> {
                Prompt.alert("Vous devez importer un fichier hex", rootPane, rootAnchorPane);
            });
            return false;
        }
        /*
        if (hexFile.lastModified() != lastModified)
        {
            Prompt.alert("Le fichier hex a été modifié, recharger-le ?", rootPane, rootAnchorPane);
        }
        */
        if (!importHex())   return false;
        
        
        return true;
    }
    
    // Initialize Programmer 
    private void pwjInit ()
    {
        // Make sure VDD is off
        byte[] script = {VDD_OFF};
        PwJFunctions.sendScript(script);
        
        // Initialize VDD to 3.3V
        // PwJFunctions.setVdd(3.3F, 0.85f); 
        PwJFunctions.checkForPoweredDevice ();
        //PwJFunctions.identifyDevice ();
        detectPICMenuItem.fire();
    }
    
    @FXML
    private void detectPIC(ActionEvent event) 
    {
        PwJFunctions.identifyDevice ();
        if (deviceFound)
        {
            updateUI();
            detectPICMenuItem.setDisable(true);
        }
    }
    
    @FXML
    private void writePIC(ActionEvent event) 
    {   
        // Check if programmer and device are connected
        if (!interfaceCheck())  return;

        if (!hexFileCheck()) return;

        byte[] script = {MCLR_TGT_GND_ON, VDD_ON};
        PwJFunctions.sendScript(script);
        
        Prompt.wait("Programmation en cours ...", rootPane, rootAnchorPane);
        Task<writeState> write = new Task<writeState>()
        {
            @Override
            protected writeState call() throws Exception {
                boolean rowErase = false;
                
                // Check if device uses row erase
                if (device.getRowEraseSize() > 0)
                    rowErase = true;

                if (!eraseDevice(rowErase)) return writeState.FAILED;

                // Write device
                //Platform.runLater(() -> Prompt.wait("Programmation en cours ...", rootPane, rootAnchorPane));
                boolean configInProgramSpace = false;
                int configLocation = device.getConfigAddr() / device.getProgMemHexBytes();
                int configWords = device.getConfigWords();
                int[] configBackUps = new int[configWords];
                int[] progMemBuffer = device.getProgMem();
                int[] eeMemBuffer = device.getEepromMem();
                int[] configBuffer = device.getConfig();
                int[] userIdBuffer = device.getUserID();
                int endOfBuffer = progMemBuffer.length;


                if (configLocation < device.getProgMemSize() && configWords > 0)
                {
                    configInProgramSpace = true;
                    for (int config = configWords; config > 0; config--)
                    {
                        configBackUps[config - 1] = progMemBuffer[endOfBuffer - config];
                        progMemBuffer[endOfBuffer - config] = device.getBlankValue();
                    }
                }
                endOfBuffer--;

                // Write program memory
                PwJFunctions.runScript(device.getProgEntryScriptLen(), device.getProgEntryScript()); // Enter programming mode
                if (device.getProgMemWrPrepScript()!= 0)
                {
                    PwJFunctions.downloadAddress(0);
                    PwJFunctions.runScript(device.getProgMemWrPrepScriptLen(), device.getProgMemWrPrepScript());
                }

                // How many words are written in a single write operation 
                int wordsPerWrite = device.getProgMemWrWords(); 
                // How many bytes in a word
                int bytesPerWord = device.getBytesPerLocation();
                // How many executions of PROG_MEM_WR to use the download buffer
                int scriptRunsToUseDownload = DOWNLOAD_BUFFER_SIZE / (bytesPerWord * wordsPerWrite); 
                // Number of words to be written in each iteration of the FOR loop
                int wordsPerLoop = scriptRunsToUseDownload * wordsPerWrite;
                // Counter of number of words that have been downloaded
                int wordsWritten = 0;
                // Index of the last non blank value in program memory
                endOfBuffer = PwJFunctions.findLastUsedInBuffer(progMemBuffer, device.getBlankValue(), endOfBuffer);


                if (((wordsPerWrite == (endOfBuffer + 1)) || (wordsPerLoop > (endOfBuffer + 1))))
                { 
                    scriptRunsToUseDownload = 1;
                    wordsPerLoop = wordsPerWrite;
                }
                // Loop iterations to download entire program memory buffer
                int writes = (endOfBuffer + 1) / wordsPerLoop;
                if (((endOfBuffer + 1) % wordsPerLoop) > 0)  writes++;
                // Number of words to be written 
                endOfBuffer = writes * wordsPerLoop;


                byte[] downloadBuffer = new byte[DOWNLOAD_BUFFER_SIZE];
                do
                {
                    int downloadIndex = 0;
                    for (int word = 0; word < wordsPerLoop; word++)
                    {
                        if (wordsWritten == endOfBuffer)
                        {
                            break;
                        }                   
                        int memWord = progMemBuffer[wordsWritten++];
                        if (activeFamily == 11)
                        {
                            memWord = memWord << 1;
                        }
                        downloadBuffer[downloadIndex++] = (byte) memWord;  // Copy the first byte of prog mem to downloadBuffer

                        for (int bt = 1; bt < bytesPerWord; bt++)   // Copy the rest of the bytes
                        {
                            memWord >>= 8;
                            downloadBuffer[downloadIndex++] = (byte) memWord;
                        }
                    }
                    // Download the data
                    int dataIndex = PwJFunctions.clearAndDownload(downloadBuffer, 0);

                    while (dataIndex < downloadIndex)
                    {
                        dataIndex = PwJFunctions.downloadData(downloadBuffer, dataIndex, downloadIndex);
                    }

                    PwJFunctions.runScriptItr(device.getProgMemWrScriptLen(), device.getProgMemWrScript(), (byte) scriptRunsToUseDownload);

                } while (wordsWritten < endOfBuffer);

                PwJFunctions.runScript(device.getProgExitScriptLen(), device.getProgExitScript());

                int verifyStop = endOfBuffer;
                if (configInProgramSpace)
                {// if config in program memory, restore prog memory to proper values.
                    for (int cfg = configWords; cfg > 0; cfg--)
                    {
                        progMemBuffer[device.getProgMem().length - cfg] = configBackUps[cfg - 1];
                    }
                }

                // EEPROM
                if (device.getEeMemSize() > 0)
                {
                    PwJFunctions.runScript(device.getProgEntryScriptLen(), device.getProgEntryScript());
                    if (device.getEeWrPrepScript() > 0)
                    {
                        PwJFunctions.downloadAddress(0);
                        PwJFunctions.runScript(device.getEeWrPrepScriptLen(), device.getEeWrPrepScript());
                    }
                    int eeBytesPerWord = device.getEeMemBytesPerWord();
                    int eeBlank = 0xFF;
                    if (device.getEeMemAddressIncrement() > 0)
                    {
                        eeBlank = 0xFFFF;
                    }
                    else if (activeFamily == 10)
                    {
                        eeBlank = 0xFFF;
                    }
                    int locationsPerLoop = device.getEeWrLocations(); 
                    if (locationsPerLoop < 16)
                    {
                        locationsPerLoop = 16;
                    }
                    if (!rowErase)
                    {
                        endOfBuffer = PwJFunctions.findLastUsedInBuffer(eeMemBuffer, eeBlank, eeMemBuffer.length - 1);
                    }

                    int eeWrites = (endOfBuffer + 1) / locationsPerLoop;
                    if (((endOfBuffer + 1) % locationsPerLoop) > 0)
                    {
                        eeWrites++;
                    }
                    endOfBuffer = eeWrites * locationsPerLoop; 
                    byte[] eeDownloadBuffer = new byte[(locationsPerLoop * eeBytesPerWord)];


                    int eeScriptRunsPerLoop = locationsPerLoop / device.getEeWrLocations();
                    int locationsWritten = 0;

                    do
                    {
                        int downloadIndex = 0;
                        for (int word = 0; word < locationsPerLoop; word++)
                        {
                            int eeWord = eeMemBuffer[locationsWritten++];
                            if (activeFamily == 11)
                            {
                                eeWord = eeWord << 1;
                            }

                            eeDownloadBuffer[downloadIndex++] = (byte)(eeWord & 0xFF);

                            for (int bt = 1; bt < bytesPerWord; bt++)
                            {
                                eeWord >>= 8;
                                eeDownloadBuffer[downloadIndex++] = (byte)(eeWord & 0xFF);
                            }  
                        }
                        PwJFunctions.clearAndDownload(eeDownloadBuffer, 0);

                        PwJFunctions.runScriptItr(device.getEeWrScriptLen(), device.getEeWrScript(), (byte) eeScriptRunsPerLoop);

                    } while (locationsWritten < endOfBuffer);
                    PwJFunctions.runScript(device.getProgExitScriptLen(), device.getProgExitScript());
                }

                // UserIDs
                if (device.getUserIDs() > 0)
                {
                    PwJFunctions.runScript(device.getProgEntryScriptLen(), device.getProgEntryScript());
                    if (device.getUserIdWrPrepScript() > 0)
                    {
                        PwJFunctions.runScript(device.getUserIdWrPrepScriptLen(), device.getUserIdWrPrepScript());
                    }
                    int bytesPerID = device.getUserIdBytes();
                    byte[] uIdDownloadBuffer = new byte[device.getUserIDs() * bytesPerID];
                    int downloadIndex = 0;
                    int idWritten = 0; 
                    for (int word = 0; word < device.getUserIDs(); word++)
                    {
                        int memWord = userIdBuffer[idWritten++];
                        if (activeFamily == 11)
                        {
                            memWord = memWord << 1;
                        }

                        uIdDownloadBuffer[downloadIndex++] = (byte)(memWord & 0xFF);

                        for (int bt = 1; bt < bytesPerID; bt++)
                        {
                            memWord >>= 8;
                            uIdDownloadBuffer[downloadIndex++] = (byte)(memWord & 0xFF);
                        }
                    }
                    int dataIndex = PwJFunctions.clearAndDownload(uIdDownloadBuffer, 0);
                    while (dataIndex < downloadIndex)
                    {
                        dataIndex = PwJFunctions.downloadData(uIdDownloadBuffer, dataIndex, downloadIndex);
                    }
                    PwJFunctions.runScript(device.getUserIdWrScriptLen(), device.getUserIdWrScript());
                    PwJFunctions.runScript(device.getProgExitScriptLen(), device.getProgExitScript());
                }

                // CONFIGS 
                if (configWords > 0 && !configInProgramSpace)
                {
                    if (device.getFamilyName().equals("PIC18F") || device.getFamilyName().equals("PIC18F_K_"))
                    {
                        if (configWords > 5)
                        {
                            if ((configBuffer[5] & ~0x2000) == configBuffer[5])
                            {
                                int saveConfig6 = configBuffer[5];
                                configBuffer[5] = 0xFFFF;
                                PwJFunctions.writeConfigOutsideProgMem(device, false, false);
                                configBuffer[5] = saveConfig6;
                            }
                        }
                    }
                    PwJFunctions.writeConfigOutsideProgMem(device, false, false);
                }
                else if (configWords > 0 && configInProgramSpace)
                {
                    PwJFunctions.runScript(device.getProgEntryScriptLen(), device.getProgEntryScript());
                    int lastBlock = progMemBuffer.length - device.getProgMemWrWords();
                    if (device.getProgMemWrPrepScript() != 0)
                    {
                        PwJFunctions.downloadAddress(lastBlock * device.getAddressIncrement());
                        PwJFunctions.runScript(device.getProgMemWrPrepScriptLen(), device.getProgMemWrPrepScript());
                    }
                    byte[] downBuff = new byte[DOWNLOAD_BUFFER_SIZE];
                    int downIndex = 0;
                    for (int word = 0; word > device.getProgMemWrWords(); word++)
                    {
                        int memWord = progMemBuffer[lastBlock++];
                        if (activeFamily == 11) memWord <<= 1;
                        downBuff[downIndex++] = (byte) memWord;
                        for (int bt = 1; bt < device.getBytesPerLocation(); bt++)
                        {
                            memWord >>= 8;
                            downBuff[downIndex++] = (byte) memWord;
                        }
                    }
                    int dataIdx = PwJFunctions.clearAndDownload(downBuff, 0);
                    while (dataIdx < downIndex)
                    {
                        dataIdx = PwJFunctions.downloadData(downBuff, dataIdx, downIndex);
                    }
                    PwJFunctions.runScript(device.getProgMemWrScriptLen(), device.getProgMemWrScript());
                    PwJFunctions.runScript(device.getProgExitScriptLen(), device.getProgExitScript());
                }
                return writeState.SUCCEED;
            }
        };
        
        write.setOnSucceeded((WorkerStateEvent event1) -> {
            Prompt.closeWait();
            if (write.getValue() == writeState.SUCCEED)
            {
                Prompt.alert("Programming Done", rootPane, rootAnchorPane);
            }
        });
        
        write.setOnFailed((WorkerStateEvent event1) -> {
            Prompt.closeWait();
            Prompt.alert("Programming failed", rootPane, rootAnchorPane);
        });
        
        Thread th = new Thread(write); 
        th.setDaemon(false);
        th.start();
    }
    
    @FXML
    private void erasePIC(ActionEvent event) {
        
        // Check if programmer and device are connected
        if (!interfaceCheck())  return;
        
        Prompt.wait("Erasing", rootPane, rootAnchorPane);
        boolean rowErase = false;
        // Check if device uses row erase
        if (deviceFound && device.getRowEraseSize() > 0)
        {
            rowErase = true;
        }
        eraseDevice(rowErase);
        Prompt.closeWait();
    }
    
    public boolean eraseDevice (boolean rowErase)
    {
        // Erase Device
        if (rowErase)
        {
            PwJFunctions.rowErase(device);
        }
        else    // Bulk erase
        {
            if (device.getEeMemSize() > 0)
            {
                PwJFunctions.bulkErase(device);
            }
            else if (device.getProgMemEraseScript() > 0)
            {
                PwJFunctions.progMemErase(device);
            }
        }
        return true;
    }
    
    @FXML
    private void readPIC(ActionEvent event) 
    {
        // Check if programmer and device are connected
        if (!interfaceCheck())  return;
        
        byte[] script = {MCLR_TGT_GND_ON, VDD_ON};
        PwJFunctions.sendScript(script);
        
        Prompt.wait("Lecture en cours ...", rootPane, rootAnchorPane);
        Task<Boolean> read = new Task<Boolean>()
        {
            @Override
            protected Boolean call() throws Exception {
                int[] progMembuffer = device.getProgMem();
                int[] eeMemBufer = device.getEepromMem();
                int[] userIdBuffer = device.getUserID();
                int[] configBuffer = device.getConfig();

                if (device.getProgMemSize() > 0)
                {
                    byte[] uploadBuffer = new byte[UPLOAD_BUFFER_SIZE];

                    PwJFunctions.runScript(device.getProgEntryScriptLen(), device.getProgEntryScript()); // Enter programming mode

                    if (device.getProgMemAddrSetScript() != 0 && device.getProgMemAddrBytes() > 0)
                    {
                        PwJFunctions.downloadAddress(0);
                        PwJFunctions.runScript(device.getProgMemAddrSetScriptLen(), device.getProgMemAddrSetScript());
                    }

                    int bytesPerWord = device.getBytesPerLocation();
                    int scriptRunsToFillUpload = UPLOAD_BUFFER_SIZE / (device.getProgMemRdWords() * bytesPerWord); 
                    int wordsPerLoop = scriptRunsToFillUpload * device.getProgMemRdWords();
                    int wordsRead = 0;

                    do 
                    {
                        PwJFunctions.runScriptItr(device.getProgMemRdScriptLen(), device.getProgMemRdScript(), (byte) scriptRunsToFillUpload);

                        byte[] uploadedData;

                        
                        uploadedData = PwJFunctions.uploadData(false, true);
                        if (uploadedData == null)
                        {
                            Platform.runLater(() -> {
                                Prompt.alert("Lecture échouée", rootPane, rootAnchorPane);
                            });
                            return false;
                        }
                        System.arraycopy(uploadedData, 0, uploadBuffer, 0, 64);
                        for (int i = 1; i < 4; i++)
                        {
                            uploadedData = PwJFunctions.uploadData(false, false);
                            if (uploadedData == null)
                            {
                                Platform.runLater(() -> {
                                    Prompt.alert("Lecture échouée", rootPane, rootAnchorPane);
                                });
                                return false;
                            }
                            System.arraycopy(uploadedData, 0, uploadBuffer, (i*64), 64); // System.arraycopy(source, sourceIndex, destination, destinationIndex, # of bytes)
                        }

                        int uploadIndex = 0;
                        for (int word = 0; word < wordsPerLoop; word++)
                        {
                            int bt = 0;
                            int memWord = uploadBuffer[uploadIndex + bt++]; 
                            
                            if (memWord < 0)    memWord += 256; // Adjust for when the byte that has been read is negative
                            
                            if (bt < bytesPerWord)
                                memWord |= (uploadBuffer[uploadIndex + bt++] << 8);

                            if (bt < bytesPerWord)
                                memWord |= (uploadBuffer[uploadIndex + bt++] << 16);
                            uploadIndex += bt;
                            if (activeFamily == 11)
                                memWord = (memWord >> 1) & device.getBlankValue();

                            progMembuffer[wordsRead++] = memWord;
                            if (wordsRead == device.getProgMemSize())
                                break;
                        }

                    } while (wordsRead < device.getProgMemSize());
                    PwJFunctions.runScript(device.getProgExitScriptLen(), device.getProgExitScript());
                }
                // EEPROM
                if (device.getEeMemSize() != 0)
                {
                    byte[] uploadBuffer = new byte[UPLOAD_BUFFER_SIZE];
                    PwJFunctions.runScript(device.getProgEntryScriptLen(), device.getProgEntryScript());

                    if (device.getEeRdPrepScript() != 0)
                    {
                        if (device.getEeMemHexBytes() == 4)
                        {
                            PwJFunctions.downloadAddress(device.getEeMemAddr() / 4);
                        }
                        else 
                        {
                            PwJFunctions.downloadAddress(0);
                        }
                        PwJFunctions.runScript(device.getEeRdPrepScriptLen(), device.getEeRdPrepScript());
                    }
                    int bytesPerWord = device.getEeMemBytesPerWord();
                    int scriptRunsToFillUpload = UPLOAD_BUFFER_SIZE / (device.getEeRdLocations() * bytesPerWord);

                    int wordsPerLoop = scriptRunsToFillUpload * device.getEeRdLocations();
                    int wordsRead = 0;
                    int eeBlank = 0xFF;
                    if (device.getEeMemAddressIncrement() > 0)
                    {
                        eeBlank = 0xFFFF;
                    }
                    else if (activeFamily == 10)
                    {
                        eeBlank = 0xFFF;
                    }
                    do {
                        PwJFunctions.runScriptItr(device.getEeRdScriptLen(), device.getEeRdScript(), (byte) scriptRunsToFillUpload);

                        byte[] uploadedData;
                        for (int i = 0; i < 4; i++) {
                            uploadedData = PwJFunctions.uploadData(false, false);
                            if (uploadedData == null)
                            {
                                Platform.runLater (() -> {
                                    Prompt.alert("Lecture échouée", rootPane, rootAnchorPane);
                                });
                                return false;
                            }
                            System.arraycopy(uploadedData, 0, uploadBuffer, (i*64), 64); // System.arraycopy(source, sourceIndex, destination, destinationIndex, # of bytes)
                        }

                        int uploadIndex = 0;
                        for (int word = 0; word < wordsPerLoop; word++) {
                            int bt = 0;
                            int memWord = uploadBuffer [uploadIndex + bt++];
                            if (memWord < 0)    memWord += 256;
                            
                            if (bt < bytesPerWord)
                            {
                                memWord |= uploadBuffer[uploadIndex + bt++];
                            }
                            uploadIndex += bt;

                            if (activeFamily == 11)
                            {
                                memWord = (memWord >> 1) & eeBlank;
                            }
                            eeMemBufer[wordsRead++] = memWord;
                            if (wordsRead >= device.getEeMemSize())
                            {
                                break;
                            }
                        }
                    } while (wordsRead < device.getEeMemSize());
                    PwJFunctions.runScript(device.getProgExitScriptLen(), device.getProgExitScript());
                }

                // USERIDs
                if (device.getUserIDs() > 0)
                {
                    byte[] uploadBuffer = new byte[UPLOAD_BUFFER_SIZE];
                    PwJFunctions.runScript(device.getProgEntryScriptLen(), device.getProgEntryScript());

                    if (device.getUserIdRdPrepScript()!= 0)
                    {
                        PwJFunctions.runScript(device.getUserIdRdPrepScriptLen(), device.getUserIdRdPrepScript());
                    }
                    int bytesPerWord = device.getUserIdBytes();
                    int wordsRead = 0;
                    int uploadIndex = 0;
                    PwJFunctions.runScript(device.getUserIdRdScriptLen(), device.getUserIdRdScript());

                    byte[] uploadedData;
                    uploadedData = PwJFunctions.uploadData(false, false);  // Upload data without including length
                    if (uploadedData == null)
                    {
                        Platform.runLater (() -> {
                            Prompt.alert("Lecture échouée", rootPane, rootAnchorPane);
                        });
                        return false;
                    }
                    System.arraycopy(uploadedData, 0, uploadBuffer, 0, 64);
                    if ((device.getUserIDs() * bytesPerWord) > 64)
                    {
                        uploadedData = PwJFunctions.uploadData(false, false);
                        System.arraycopy(uploadedData, 0, uploadBuffer, 64, 64);
                    }
                    PwJFunctions.runScript(device.getProgExitScriptLen(), device.getProgExitScript());

                    do {
                        int bt = 0;
                        int memWord = uploadBuffer[uploadIndex + bt++];
                        if (memWord < 0)    memWord += 256;
                        if (bt < bytesPerWord)
                        {
                            memWord |= uploadBuffer[uploadIndex + bt++] << 8;
                        }
                        if (bt < bytesPerWord)
                        {
                            memWord |= uploadBuffer[uploadIndex + bt++] << 16;
                        }
                        uploadIndex += bt;

                        if (activeFamily == 11)
                        {
                            memWord = (memWord >> 1) & device.getBlankValue();
                        }
                        userIdBuffer[wordsRead++] = memWord;
                    } while (wordsRead < device.getUserIDs());
                }

                // CONFIGS
                if (device.getConfigWords() > 0)
                {
                    int configLocation = device.getConfigAddr() / device.getProgMemHexBytes();
                    int configWords = device.getConfigWords();

                    if (device.getConfigAddr() > device.getProgMemSize())
                    {   // Configs outside program memory space
                        int configIndex = 0;
                        PwJFunctions.runScript(device.getProgEntryScriptLen(), device.getProgEntryScript());
                        PwJFunctions.runScript(device.getConfigRdScriptLen(), device.getConfigRdScript());
                        PwJFunctions.runScript(device.getProgExitScriptLen(), device.getProgExitScript());

                        byte[] uploadedData;
                        uploadedData = PwJFunctions.uploadData(false, false);  // Upload data without including length
                        if (uploadedData == null)
                        {
                            Platform.runLater (() -> {
                                Prompt.alert("Lecture échouée", rootPane, rootAnchorPane);
                            });
                            return false;
                        }

                        for (int word = 0; word < configWords; word++)
                        {
                            int configWord = uploadedData[configIndex++];
                            if (configWord < 0)    configWord += 256;
                            configWord |= (uploadedData[configIndex++] << 8);
                            if (activeFamily == 11)
                            {
                                configWord = (configWord >> 1) | device.getBlankValue();
                            }
                            configBuffer[word] = configWord;
                        }
                    } 
                    else
                    {   // Configs inside program memory
                        for (int word = 0; word < configWords; word++)
                        {
                            configBuffer[word] = progMembuffer[configLocation + word];
                        }
                    }

                    // OSCCAL 
                    if (device.getOsccalSave())
                    {
                        PwJFunctions.readOSSCAL(configWords);
                    }
                }
                // TODO: Turn Vdd off
                device.setProgMem(progMembuffer);
                device.setEepromMem(eeMemBufer);
                device.setUserID(userIdBuffer);
                device.setConfig(configBuffer);

                updateMemoryUI ();
                return true;
            }
        };
        
        read.setOnSucceeded((WorkerStateEvent event1) -> {
            Prompt.closeWait();
            if (read.getValue() == true)
            {
                Prompt.alert("Lecture réussie ", rootPane, rootAnchorPane);
            }
        });
        
        read.setOnFailed((WorkerStateEvent event1) -> {
            Prompt.closeWait();
            Prompt.alert("Lecture échouée", rootPane, rootAnchorPane);
        });
        
        
        Thread readThread = new Thread(read);
        readThread.setDaemon(false);
        readThread.start();
    }
    
    @FXML
    private void verifyPIC(ActionEvent event) {
        // Check if programmer and device are connected
        if (!interfaceCheck())  return;
        
        if (!hexFileCheck())    return;
        
        byte[] script = {MCLR_TGT_GND_ON, VDD_ON};
        PwJFunctions.sendScript(script);
        Prompt.wait("Vérification en cours ...", rootPane, rootAnchorPane);
        Task<Boolean> verify = new Task<Boolean>()
        {
            @Override
            protected Boolean call() throws Exception {
                return verifyDevice(device.getProgMemSize() - 1, false);
            }
        };
        verify.setOnSucceeded((WorkerStateEvent event1) -> {
            Prompt.closeWait();
            if (verify.getValue() == true)
            {
                Prompt.alert("Verification successful", rootPane, rootAnchorPane);
            }
        });
        
        verify.setOnFailed((WorkerStateEvent event1) -> {
            Prompt.closeWait();
            Prompt.alert("Programming failed", rootPane, rootAnchorPane);
        });
        
        Thread th = new Thread(verify); 
        th.setDaemon(false);
        th.start();
        
    }
    
    private boolean verifyDevice (int endIndex, boolean verifyingAfterWrite)
    {
        int[] progMemBuffer = device.getProgMem();
        int[] eeMemBuffer = device.getEepromMem();
        int[] userIdBuffer = device.getUserID();
        int[] configMaskBuffer = device.getConfigMasks();
        int[] configBuffer = device.getConfig();
        
        // PROGRAM MEMORY VERIFICATION 
        byte[] uploadBuffer = new byte[UPLOAD_BUFFER_SIZE];
        
        int configLocation = device.getConfigAddr() / device.getProgMemHexBytes();
        int configWords = device.getConfigWords();
        int endOfBuffer = endIndex;
        
        PwJFunctions.runScript(device.getProgEntryScriptLen(), device.getProgEntryScript());
        if (device.getProgMemAddrSetScript() != 0 && device.getProgMemAddrBytes() != 0)
        {
            PwJFunctions.downloadAddress(0);
            PwJFunctions.runScript(device.getProgMemAddrSetScriptLen(), device.getProgMemAddrSetScript());
        }
        int bytesPerWord = device.getBytesPerLocation();
        int scriptRunsToFillUpload = UPLOAD_BUFFER_SIZE / (device.getProgMemRdWords() * bytesPerWord);
        int wordsPerLoop = scriptRunsToFillUpload * device.getProgMemRdWords();
        int wordsRead = 0;
        
        if (device.getProgMemRdWords() == (endOfBuffer + 1))
        {
            scriptRunsToFillUpload = 1;
            wordsPerLoop = endOfBuffer + 1;
        }
        
        do 
        {
            PwJFunctions.runScriptItr(device.getProgMemRdScriptLen(), device.getProgMemRdScript(), (byte) scriptRunsToFillUpload);
            byte[] uploadedData;
            uploadedData = PwJFunctions.uploadData(false, false);
            if (uploadedData == null)
            {
                Platform.runLater(() -> {
                    Prompt.alert("La vérification a échoué 1", rootPane, rootAnchorPane);
                });
                return false;
            }
            System.arraycopy(uploadedData, 0, uploadBuffer, 0, 64);
            uploadedData = PwJFunctions.uploadData(false, false);
            if (uploadedData == null)
            {
                Platform.runLater(() -> {
                    Prompt.alert("La vérification a échoué 1", rootPane, rootAnchorPane);
                });
                return false;
            }
            System.arraycopy(uploadedData, 0, uploadBuffer, 64, 64);
            
            int uploadIdx = 0;
            for (int word = 0; word < wordsPerLoop; word++)
            {
                int bt = 0;
                int memWord = uploadBuffer[uploadIdx + bt++];
                if (memWord < 0)    memWord += 256;
                if (bt < bytesPerWord)
                    memWord |= (uploadBuffer[uploadIdx + bt++] << 8);
                if (bt < bytesPerWord)
                    memWord |= (uploadBuffer[uploadIdx + bt++] << 16);
                uploadIdx += bt;
                if (activeFamily == 11)
                    memWord = (memWord >> 1) & device.getBlankValue();
                
                if (memWord != progMemBuffer[wordsRead++])
                {
                    PwJFunctions.runScript(device.getProgExitScriptLen(), device.getProgExitScript());
                    Platform.runLater (() -> {
                        Prompt.alert("La vérification a échoué 2", rootPane, rootAnchorPane);
                    });
                    return false;
                }
                if (wordsRead > endOfBuffer)    break;
            }
        } while (wordsRead < endOfBuffer);
        PwJFunctions.runScript(device.getProgExitScriptLen(), device.getProgExitScript());
        
        // EEPROM VERIFICATION 
        if (device.getEeMemSize() != 0)
        {
            PwJFunctions.runScript(device.getProgEntryScriptLen(), device.getProgEntryScript());
            if (device.getEeRdPrepScript() != 0)
            {
                PwJFunctions.downloadAddress(0);
                PwJFunctions.runScript(device.getEeRdPrepScriptLen(), device.getEeRdPrepScript());
            }
            
            int eeBytesPerLocation = device.getEeMemBytesPerWord();
            int eeScriptRunsToFillUpload = UPLOAD_BUFFER_SIZE / (device.getEeRdLocations() * eeBytesPerLocation);
            int eeLocationPerLoop = eeScriptRunsToFillUpload * device.getEeRdLocations();
            int eeLocationsRead = 0;
            int eeBlank = 0xFF;
            if (device.getEeMemAddressIncrement() > 0)
            {
                eeBlank = 0xFFFF;
            }
            else if (activeFamily == 10)
            {
                eeBlank = 0xFFF;
            }
            
            do 
            {
                PwJFunctions.runScriptItr(device.getEeRdScriptLen(), device.getEeRdScript(), (byte) eeScriptRunsToFillUpload);
                byte[] uploadedData;
                uploadedData = PwJFunctions.uploadData(false, false);
                if (uploadedData == null)
                {
                    Platform.runLater(() -> {
                        Prompt.alert("La vérification a échoué 3", rootPane, rootAnchorPane);
                    });
                    return false;
                }
                System.arraycopy(uploadedData, 0, uploadBuffer, 0, 64);
                uploadedData = PwJFunctions.uploadData(false, false);
                if (uploadedData == null)
                {
                    Platform.runLater(() -> {
                        Prompt.alert("La vérification a échoué 3", rootPane, rootAnchorPane);
                    });
                    return false;
                }
                System.arraycopy(uploadedData, 0, uploadBuffer, 64, 64);
                int eeUploadIdx = 0;
                for (int word = 0; word < eeLocationPerLoop; word++)
                {
                    int bt = 0;
                    int memWord = uploadBuffer[eeUploadIdx + bt++];
                    if (memWord < 0)    memWord += 256;
                    if (bt < eeBytesPerLocation)
                        memWord |= (uploadBuffer[eeUploadIdx + bt++] << 8);
                    eeUploadIdx += bt;
                    if (activeFamily == 11)
                        memWord = (memWord >> 1) & eeBlank;
                    if (memWord != eeMemBuffer[eeLocationsRead++])
                    {
                        PwJFunctions.runScript(device.getProgExitScriptLen(), device.getProgExitScript());
                        Platform.runLater (() -> {
                            Prompt.alert("La vérification a échoué", rootPane, rootAnchorPane);
                        });
                        return false;
                    }
                    if (eeLocationsRead > device.getEeMemSize())    break;
                }
            } while (eeLocationsRead < device.getEeMemSize());
            PwJFunctions.runScript(device.getProgExitScriptLen(), device.getProgExitScript());
        }
        
        
        // USER IDs VERIFICATION
        if (device.getUserIDs() != 0)
        {
            PwJFunctions.runScript(device.getProgEntryScriptLen(), device.getProgEntryScript());
            if (device.getUserIdRdPrepScript() != 0)
            {
                PwJFunctions.runScript(device.getUserIdRdPrepScriptLen(), device.getUserIdRdPrepScript());
            }
            int uidBytesPerWord = device.getUserIdBytes();
            int uidWordsRead = 0;
            int uidBufferIdx = 0;
            
            PwJFunctions.runScript(device.getUserIdRdScriptLen(), device.getUserIdRdScript());
            byte[] uploadedData;
            uploadedData = PwJFunctions.uploadData(false, false);
            if (uploadedData == null)
            {
                Platform.runLater(() -> {
                    Prompt.alert("La vérification a échoué 4", rootPane, rootAnchorPane);
                });
                return false;
            }
            System.arraycopy(uploadedData, 0, uploadBuffer, 0, 64);
            if ((device.getUserIDs() * bytesPerWord) > 64)
            {
                uploadedData = PwJFunctions.uploadData(false, false);
                System.arraycopy(uploadedData, 0, uploadBuffer, 64, 64);
            }
            
            PwJFunctions.runScript(device.getProgExitScriptLen(), device.getProgExitScript());
            do 
            {
                int bt = 0;
                int memWord = uploadBuffer[uidBufferIdx + bt++];
                if (memWord < 0)    memWord += 256;
                if (bt < uidBytesPerWord)
                    memWord |= (uploadBuffer [uidBufferIdx + bt++] << 8);
                if (bt < uidBytesPerWord)
                    memWord |= (uploadBuffer [uidBufferIdx + bt++] << 16);
                uidBufferIdx += bt;
                if (activeFamily == 11)
                {
                    memWord = (memWord >> 1) & device.getBlankValue();
                }
                if (memWord != userIdBuffer[uidWordsRead++])
                {
                    Platform.runLater (() -> {
                        Prompt.alert("La vérification a échoué 5", rootPane, rootAnchorPane);
                    });
                    return false;
                }
            } while (uidWordsRead < device.getUserIDs());
        }
        
        // CONFIG VERIFICATION
        // don't verify during writing because configs hasn't been written yet, in case they have code protect enabled
        if (!verifyingAfterWrite)
        {
            if (configWords > 0 && configLocation > device.getProgMemSize())
            {
                PwJFunctions.runScript(device.getProgEntryScriptLen(), device.getProgEntryScript());
                PwJFunctions.runScript(device.getConfigRdScriptLen(), device.getConfigRdScript());
                byte[] uploadedData;
                uploadedData = PwJFunctions.uploadData(false, false); 
                PwJFunctions.runScript(device.getProgExitScriptLen(), device.getProgExitScript());
                int bufferIdx = 0;
                for (int word = 0; word < configWords; word++)
                {
                    int config = uploadedData[bufferIdx++];
                    if (config < 0)    config += 256;
                    config |= (uploadedData[bufferIdx++] << 8);
                    if (activeFamily == 11)
                    {
                        config = (config >> 1) & device.getBlankValue();
                    }
                    config &= configMaskBuffer[word];
                    int deviceConfig = configBuffer[word] & configMaskBuffer[word];
                    if (deviceConfig != config)
                    {
                        Platform.runLater (() -> {
                            Prompt.alert("La vérification des configurations a échoué", rootPane, rootAnchorPane);
                        });
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
}
