package pwj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import static pwj.inter.IDefinitions.WRITE_DOWN_BUFF;
import pwj.ui.Prompt;
import static pwj.usb.USBFunctions.programmer;

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
            if (deviceFound)
                importHex();
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
                Prompt.alert("Le fichier hex contient des adresses invalides pour le "+device.getName(), rootPane, rootAnchorPane);
                return false;
            }
        } catch (IOException e) 
        {
            Prompt.alert("Fichier hex n'a pas pu être chargé", rootPane, rootAnchorPane);
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
            programmerStatus.setText("Programmateur connecté, FW Vr: " + version[1] + ".0"+ version[2]+ "."+ version[3]);
            disconnectMenuItem.setDisable(false);
            connectMenuItem.setDisable(true);
            detectPICMenuItem.setDisable(false);
            //pwjInit ();
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
    
    private boolean hexFileCheck()
    {
        if (hexFile == null)    
        {
            Prompt.alert("Vous devez importer un fichier hex", rootPane, rootAnchorPane);
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
        boolean rowErase = false;
        
        // Check if programmer and device are connected
        if (!interfaceCheck())  return;
        
        if (!hexFileCheck()) return;
        
        byte[] script = {MCLR_TGT_GND_ON, VDD_ON};
        PwJFunctions.sendScript(script);
        
        // Check if device used low voltage row erase
        if (device.getRowEraseSize() > 0)
            rowErase = true;
        
        if (!eraseDevice(rowErase)) return;
        
        // Write device
        
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
        int scriptRunsToUseDownload = 256 / (bytesPerWord * wordsPerWrite); // 256 is the download buffer size
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
        
        
        byte[] downloadBuffer = new byte[256];
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
            byte[] progMemWrCmd = new byte[6];
            progMemWrCmd[0] = CLEAR_UP_BUFF;
            progMemWrCmd[1] = RUN_ROM_SCRIIPT_ITR;
            progMemWrCmd[2] = device.getProgMemWrScriptLen();
            progMemWrCmd[3] = (byte) device.getProgMemWrScript();   
            progMemWrCmd[4] = (byte) (device.getProgMemWrScript() >> 8); 
            progMemWrCmd[5] = (byte) scriptRunsToUseDownload;
            USBFunctions.hidWrite(progMemWrCmd);
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
                byte[] eeMemWrCmd = new byte[5];
                eeMemWrCmd[0] = RUN_ROM_SCRIIPT_ITR;
                eeMemWrCmd[1] = device.getEeWrScriptLen();
                eeMemWrCmd[2] = (byte) device.getEeWrScript();
                eeMemWrCmd[3] = (byte) (device.getEeWrScript() >> 8);
                eeMemWrCmd[4] = (byte) eeScriptRunsPerLoop;
                USBFunctions.hidWrite(eeMemWrCmd);
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
            byte[] downBuff = new byte[256];
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
        System.out.println("Programming done");
    }
    
    @FXML
    private void erasePIC(ActionEvent event) {
        
        byte[] cmd = {0x30, 00, 1,2,3,4,5,6,7,8, 9,9, 88, 65, 0x28, 0x3F, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        PwJFunctions.clearAndDownload(cmd, 0);
        
        
        byte[] progMemWrCmd = new byte[6];
        progMemWrCmd[0] = CLEAR_UP_BUFF;
        progMemWrCmd[1] = RUN_ROM_SCRIIPT_ITR;
        progMemWrCmd[2] = 29;
        progMemWrCmd[3] = (byte) 0x60;   
        progMemWrCmd[4] = (byte) 0x62; 
        progMemWrCmd[5] = (byte) 1;
        USBFunctions.hidWrite(progMemWrCmd);
        
        /*
        boolean rowErase = false;
        // Check if device used low voltage row erase
        if (deviceFound && device.getRowEraseSize() > 0)
        {
            rowErase = true;
        }
        eraseDevice(rowErase);
        */
    }
    
    public boolean eraseDevice (boolean rowErase)
    {
        // Check if programmer and device are connected
        if (!interfaceCheck())  return false;
        
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
}
