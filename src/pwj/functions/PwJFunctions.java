package pwj.functions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pwj.PGMMainController;
import pwj.db.DbUtil;
import pwj.device.DeviceInfo;
import pwj.usb.USBFunctions;
import static pwj.usb.USBFunctions.programmer;
import pwj.inter.IDefinitions;

public class PwJFunctions implements IDefinitions {
    private static float vdd;
    private static float lastVdd = 3.3f; // For PIC24F, dsPIC33, PIC18F_J_ 
    private static float vpp;
    private static boolean selfPoweredDeviceFound = false;
    private static ResultSet rs;
    
    // Check if target device is self powered
    public static boolean checkForPoweredDevice ()
    {
        if (getVddVppVoltages ())
        {
           if (vdd > SELF_POWERED_DEVICE_THRESHOLD)
           {
               selfPoweredDeviceFound = true;
               setVdd (vdd, 0.85f);
               return true;
           }
        }
        selfPoweredDeviceFound = false;
        return false;
    }
    
    // Read and upload VDD and VPP voltages
    @SuppressWarnings("empty-statement")
    public static boolean getVddVppVoltages ()
    {
        byte[] cmd = new byte[1];
        cmd[0] = READ_VOLTAGES;
        USBFunctions.hidWrite(cmd);
        byte[] response = new byte[5];
        
        while (programmer.read(response, 3000) < 0);
        // Check if received packet length is greater than zero
        if (response[0] > 0)
        {
            // vddVppADC = CCPH:CCPL, Add 255 to both bytes to get the unsigned value
            float vddVppADC = (float)(((response[2] + 256)* 256.0f) + (response[1] + 256));
            vdd = (vddVppADC / 65536) * 5f;
            
            vddVppADC = (float)((response[4] * 256.0f) + response[3]);
            vpp = (vddVppADC / 65536) * 13.7f;
            return true;
        }
        return false;
    }
     
    public static void setVdd (float voltage, float threshold)
    {
        // Verify that voltage is above minimum value required for VPP pump to work
        voltage = (voltage < 2.5f) ? 2.5f : voltage;  
        lastVdd = voltage;
        
        // Get voltage CCPH:CCPL value
        short voltageCCP = (short)((voltage * 32f) + 10.5f);
        
         // CCP value is left justified
        voltageCCP <<= 6;      
        byte vFault = (byte)(((threshold * voltage) / 5F) * 255F);
        vFault = (vFault < 210) ? (byte)210 : vFault;  
        
        byte[] cmd = new byte[4];
        cmd[0] = SET_VDD;
        cmd[1] = (byte) (voltageCCP & 0xFF);
        cmd[2] = (byte) (voltageCCP / 256);
        cmd[3] = vFault;
        USBFunctions.hidWrite(cmd);
    }
    
    public static void sendScript(byte[] cmd)
    {
        byte[] script = new byte [cmd.length + 2];
        script[0] = RUN_USB_SCRIPT;
        script[1] = (byte)cmd.length;
        for (byte i = 0; i < cmd.length ; i++)
        {
            script[i+2] = cmd[i];
        }
        USBFunctions.hidWrite(script);
    }
    
    // NOT FULLY IMPLEMENTED 
    public static void readOSSCAL(int address)
    {
        byte[] cmd =  new byte [11];
        cmd[0] = RUN_USB_SCRIPT;
        cmd[1] = 2;
        cmd[2] = MCLR_TGT_GND_ON;
        cmd[3] = VDD_ON;
        cmd[4] = CLEAR_DOWN_BUFF;
        cmd[5] = WRITE_DOWN_BUFF;
        cmd[6] = 3;
        cmd[7] = (byte) (address & 0xFF);
        cmd[8] = (byte)(0xFF & (address >> 8));
        cmd[9] = (byte)(0xFF & (address >> 16));
        cmd[10] = 000;
        USBFunctions.hidWrite(cmd);
    }
    
    public static int findLastUsedInBuffer(int[] bufferToSearch, int blankValue, int startIndex)
    {
        for (int index = startIndex; index > 0; index--)
        {
            if (bufferToSearch[index] != blankValue)
            {
                return index;
            }
        }
        return 0;          
    }
    
    public static void rowErase(DeviceInfo device)
    {
        int memoryRows = device.getProgMemSize() / device.getRowEraseSize();
        // Enter programming mode
        
        runScript(device.getProgEntryScriptLen(), device.getProgEntryScript());

        if (device.getProgMemWrPrepScript() > 0)
        {
            downloadAddress(0);
            runScript(device.getProgMemWrScriptLen(), device.getProgMemWrPrepScript());;
        }

        byte[] rowEraseCmd = new byte[5];
        rowEraseCmd[0] = RUN_ROM_SCRIIPT_ITR;
        rowEraseCmd[1] = device.getRowEraseScriptLen();
        rowEraseCmd[2] = (byte)device.getRowEraseScript();
        rowEraseCmd[3] = (byte)(device.getRowEraseScript() >> 8);
        do 
        {
            if (memoryRows >= 256)
            {

                rowEraseCmd[4] = 0;
                memoryRows -= 256;
            }
            else 
            {
                rowEraseCmd[4] = (byte) (memoryRows & 0xFF);
                memoryRows = 0;
            }
            USBFunctions.hidWrite(rowEraseCmd);
        } while (memoryRows > 0);

        runScript(device.getProgExitScriptLen(), device.getProgExitScript());

        // Erase config memory
        if (device.getConfigMemEraseScript() > 0)
        {
            runScript(device.getProgEntryScriptLen(), device.getProgEntryScript());

            if (device.getProgMemWrPrepScript() > 0)
            {
                downloadAddress(device.getUserIdAddr());
                runScript(device.getProgMemWrScriptLen(), device.getProgMemWrPrepScript());
            }
            runScript(device.getConfigMemEraseScriptLen(), device.getConfigMemEraseScript());
            runScript(device.getProgExitScriptLen(), device.getProgExitScript());
        }
    }
    
    public static void bulkErase(DeviceInfo device)
    {
        if (device.getConfigMemEraseScript() > 0 && device.getRowEraseScript() == 0)
        {
            runScript(device.getProgEntryScriptLen(), device.getProgEntryScript());
            
            if (device.getConfigWrPrepScript() > 0)
            {
                downloadAddress(0);
                runScript(device.getConfigWrPrepScriptLen(), device.getConfigWrPrepScript());
            }
            
            runScript(device.getConfigWrScriptLen(), device.getConfigWrScript());
            runScript(device.getProgExitScriptLen(), device.getProgExitScript());
        }
        runScript(device.getProgEntryScriptLen(), device.getProgEntryScript());
        if (device.getChipErasePrepScript() > 0)
        {
            runScript(device.getChipErasePrepScriptLen(), device.getChipErasePrepScript());
        }
        
        runScript(device.getChipEraseScriptLen(), device.getChipEraseScript());
        runScript(device.getProgExitScriptLen(), device.getProgExitScript());
    }
    
    public static void progMemErase(DeviceInfo device)
    {
        runScript(device.getProgEntryScriptLen(), device.getProgEntryScript());
        runScript(device.getProgMemEraseScriptLen(), device.getProgMemEraseScript());
        runScript(device.getProgExitScriptLen(), device.getProgExitScript());
    }
    
    public static void runScript (byte scriptLen, int scriptAddress)
    {
        byte[] runScriptCmd = new byte[4];
        runScriptCmd[0] = RUN_ROM_SCRIPT;
        runScriptCmd[1] = scriptLen;
        runScriptCmd[2] = (byte) scriptAddress;
        runScriptCmd[3] = (byte) (scriptAddress >> 8);
        USBFunctions.hidWrite(runScriptCmd);
    }
    
    public static void runScriptItr (byte scriptLen, int scriptAddress, byte iterations)
    {
        byte[] runScriptItr = new byte[6];
        runScriptItr[0] = CLEAR_UP_BUFF;
        runScriptItr[1] = RUN_ROM_SCRIIPT_ITR;
        runScriptItr[2] = scriptLen;
        runScriptItr[3] = (byte) scriptAddress;
        runScriptItr[4] = (byte) (scriptAddress >> 8);
        runScriptItr[5] = iterations;
        USBFunctions.hidWrite(runScriptItr);
    }
    
    public static int clearAndDownload (byte[] data, int startIndex)
    {
        if (startIndex >= data.length)
        {
            return 0;
        }
        int length = data.length - startIndex;
        if (length > 60)
        {
            length = 60;
        }
        byte[] cmd = new byte[3 + length];
        cmd[0] = CLEAR_DOWN_BUFF;
        cmd[1] = WRITE_DOWN_BUFF;
        cmd[2] = (byte)length;
        for (int i = 0; i < length; i++)
        {
            cmd[3 + i] = (byte)data[startIndex + i];
        }
        if (USBFunctions.hidWrite(cmd) > 0)
        {
            return (startIndex + length);
        }
        return 0;
    }
    
    public static int downloadData(byte[] data, int startIndex, int endIndex)
    {
        
            if (startIndex >= endIndex)
            {
                return 0;
            }
            int length = endIndex - startIndex;
            if (length > 61)
            {
                length = 61;
            }
            byte[] commandArray = new byte[2 + length];
            commandArray[0] = WRITE_DOWN_BUFF;
            commandArray[1] = (byte) length;
            for (int i = 0; i < length; i++)
            {
                commandArray[2 + i] = data[startIndex + i];
            }
            if (USBFunctions.hidWrite(commandArray) > 0)
            {
                return (startIndex + length);
            }
            return 0;
    }
    
    @SuppressWarnings("empty-statement")
    public static byte[] uploadData(boolean includeLength, boolean clearUploadBuffer)
    {
        
        byte[] cmd = new byte[1];
        /*
        if (clearUploadBuffer)
        {
            cmd = new byte[2];
            cmd[0] = CLEAR_UP_BUFF;
        }
        */
        if (includeLength)
        {
            cmd[0] = UPLOAD;    // First byte = length of data
        }
        else 
        {
            cmd[0] = UPLOAD_WITHOUT_LENGTH;
        }
        if (USBFunctions.hidWrite(cmd) > 0)
        {
            byte[] response = new byte[64];
            while (programmer.read(response, 500) < 0);
            
            return response;
        }
        return null;
    }
    
    public static void writeConfigOutsideProgMem(DeviceInfo device, boolean codeProtect, boolean dataProtect)
    {
        int configWords = device.getConfigWords();
        byte[] configBuffer = new byte[configWords * 2];
        int[] configMemBuffer = device.getConfig();
        int[] configMasks = device.getConfigMasks();
        if (device.getBandGapMask() > 0)
        {
            configMemBuffer[0] &= ~device.getBandGapMask();
            configMemBuffer[0] |= device.getBandGap();
        }
        runScript(device.getProgEntryScriptLen(), device.getProgEntryScript());
        if (device.getConfigWrPrepScript() > 0)
        {
            downloadAddress(0);
            runScript(device.getConfigWrPrepScriptLen(), device.getConfigWrPrepScript());
        }
        for (int i = 0, j = 0; i < configWords; i++)
        {
            int configWord = (configMemBuffer[i] & configMasks[i]);
            if (i == device.getCpConfig() - 1)
            {
                if (codeProtect)
                {
                    configWord &= ~device.getCpMask();
                }
                if (dataProtect)
                {
                    configWord &= ~device.getDpMask();
                }
            }
            if (device.getFamilyName().equals("Midrange/Standard"))
            {
                configWord |= (~configMasks[i] & ~device.getBandGapMask());
                configWord &= device.getBlankValue();
                configWord <<= 1;
            }
            configBuffer [j++] = (byte)configWord;
            configBuffer [j++] = (byte) (configWord >> 8);
        }
        clearAndDownload(configBuffer, 0);
        runScript(device.getConfigWrScriptLen(), device.getConfigWrScript());
        runScript(device.getProgExitScriptLen(), device.getProgExitScript());
    }
    
    public static void downloadAddress (int address)
    {
        byte[] downloadCmd = new byte[9];
        downloadCmd[0] = CLEAR_DOWN_BUFF;
        downloadCmd[1] = WRITE_DOWN_BUFF;  // download 
        downloadCmd[2] = 3; 
        downloadCmd[3] = (byte) address;
        downloadCmd[4] = (byte) (address >> 8);
        downloadCmd[5] = (byte) (address >> 16);
        USBFunctions.hidWrite(downloadCmd);
    }
    
    public static void identifyDevice()
    {
        if (!selfPoweredDeviceFound)
        {
            // Initialize VDD to 3.3V
            setVdd(3.3F, 0.85f); 
        }
        // Iterate over all families, last family has FamilyID = 11
        for (byte i = 11; i < 12; i++)
        {
            if (searchForDevice(i) != 0)
            {
                PGMMainController.setActiveFamily(i);
                break; // No need to look for other families
            }
        }
    }
    
    @SuppressWarnings("empty-statement")
    public static int searchForDevice (byte id)
    {
        String familyName = "";
        
        int progEntryScript = 0; // Used for the ProgEntry script address
        int progEntryVppScript = 0;
        int progExitScript = 0;  // Used for the ProgExit script address
        int readDevIdScript = 0; // Used for the ReadDevID script address
        int devIdMask = 0;       // Stores the deviceID mask
        int blankValue = 0;
        byte progMemHexBytes = 0;
        byte eeMemHexBytes = 0;
        byte userIdHexBytes = 0;
        byte bytesPerLocation = 0;
        byte eeMemBytesPerWord = 0;
        byte eeMemAddressIncrement = 0;  
        byte userIdBytes = 0;
        byte addressIncrement = 0;
        
        byte progEntryLen = 0;
        byte progEntryVppLen = 0;
        byte progExitLen = 0;
        byte readDevIdLen = 0;
        
        float familyVpp = 0;
        
        // https://stackoverflow.com/a/7150290
        String query = "SELECT *"
                + " FROM FAMILY_SCRIPTS"
                + " INNER JOIN FAMILY ON FAMILY.FAMILYID=FAMILY_SCRIPTS.FAMILYID"
                + " INNER JOIN SCRIPTS ON FAMILY_SCRIPTS.SCRIPTADDRESS = SCRIPTS.SCRIPTADDRESS"
                + " WHERE FAMILY.FAMILYID ="+id;
        
        rs = DbUtil.execQuery(query);
        try {
            // Should return VPP and all scripts used by a particular family
            while (rs.next())
            {
                familyVpp = rs.getFloat("VPP");
                familyName = rs.getString("FAMILYNAME");
                devIdMask = rs.getInt("DEVIDMASK");
                blankValue = rs.getInt("BLANKVALUES");
                progMemHexBytes = rs.getByte("PROGMEMHEXBYTES");
                eeMemHexBytes = rs.getByte("EEHEXBYTES");
                userIdHexBytes = rs.getByte("USERIDHEXBYTES");
                bytesPerLocation = rs.getByte("BYTESPERLOCATION");
                eeMemBytesPerWord = rs.getByte("EEMEMBYTESPERWORD");
                eeMemAddressIncrement = rs.getByte("EEMEMADDRINCREMENT");
                addressIncrement = rs.getByte("ADDRINCREMENT");
                userIdBytes = rs.getByte("USERIDBYTES");
                if (rs.getString("SCRIPTTYPE").equals("PROG_ENTRY"))
                {
                    progEntryScript = rs.getInt("SCRIPTADDRESS");
                    progEntryLen = rs.getByte("SCRIPTLEN");
                }
                
                else if (rs.getString("SCRIPTTYPE").equals("PROG_ENTRY_VPP_FIRST"))
                {
                    progEntryVppScript = rs.getInt("SCRIPTADDRESS");
                    progEntryVppLen = rs.getByte("SCRIPTLEN");
                }
                
                else if (rs.getString("SCRIPTTYPE").equals("PROG_EXIT"))
                {
                    progExitScript = rs.getInt("SCRIPTADDRESS");
                    progExitLen = rs.getByte("SCRIPTLEN");
                }
                else if (rs.getString("SCRIPTTYPE").equals("READ_DEV_ID"))
                {
                    readDevIdScript = rs.getInt("SCRIPTADDRESS");
                    readDevIdLen = rs.getByte("SCRIPTLEN");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PwJFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Exit if one of the addresses wasn't found
        if (progEntryScript == 0 || progExitScript == 0 || readDevIdScript == 0)
            return 0;
        byte[] cmd = new byte [26];
        cmd [0] = SET_VPP;
        cmd [1] = 0x40;
        cmd [2] = (byte) (familyVpp * 18.61);
        cmd [3] = (byte) (familyVpp * 0.7 * 18.61);
        
        cmd [4] = RUN_USB_SCRIPT;
        cmd [5] = 3;
        cmd [6] = MCLR_TGT_GND_ON;
        cmd [7] = VDD_GND_OFF;
        cmd [8] = selfPoweredDeviceFound ? VDD_OFF : VDD_ON;    // Turn on VDD only if device isn't self-powered 
        
        if (progEntryVppScript == 0)
        {
            cmd [9] = RUN_ROM_SCRIPT;
            cmd [10] = progEntryLen;
            cmd [11] = (byte) progEntryScript;
            cmd [12] = (byte)(progEntryScript >> 8);
        }
        else 
        {
            cmd [9] = RUN_ROM_SCRIPT;
            cmd [10] = progEntryVppLen;
            cmd [11] = (byte) progEntryVppScript;
            cmd [12] = (byte)(progEntryVppScript >> 8);
        }
        
        cmd [13] = RUN_ROM_SCRIPT;
        cmd [14] = readDevIdLen;
        cmd [15] = (byte) readDevIdScript;
        cmd [16] = (byte)(readDevIdScript >> 8);
        
        cmd [17] = RUN_ROM_SCRIPT;
        cmd [18] = progExitLen;
        cmd [19] = (byte) progExitScript;
        cmd [20] = (byte)(progExitScript >> 8);
        
        // Turn off VDD
        cmd [21] = RUN_USB_SCRIPT;
        cmd [22] = 2;
        cmd [23] = VDD_OFF;
        cmd [24] = selfPoweredDeviceFound ? VDD_GND_OFF : VDD_GND_ON;
        // Upload bytes that were read from the target 
        cmd [25] = UPLOAD;
        
        USBFunctions.hidWrite(cmd);
        byte[] response = new byte[3];
        while (programmer.read(response, 500) < 0);
        
        int[] deviceIdArray = new int[2];
        
        for (byte i = 0; i < 2; i++)
        {
            deviceIdArray[i] = (response[i+1] >= 0) ? response[i+1] : (response[i+1] + 256);
        }
        
        //long devID =  response[4] * 0x1000000 + response[3] * 0x10000 + response[2] * 0x100 + response[1];
        long devID =  deviceIdArray[1] * 0x100 + deviceIdArray[0];
        // For midrange, deviceID should be shifted to the right by one
        if (id == 11)   devID >>= 1;
        devID &= devIdMask;
        
        // If deviceID is less then the smallest DEVID, exit
        if (devID < 3616) return 0;
        PGMMainController.setDeviceFound(true);
        if (devID != PGMMainController.getActiveDevice())
        {
            PGMMainController.setActiveDevice ((int)devID);
            getDeviceInfo((int)devID, familyName, progEntryScript, progEntryLen, progExitScript, progExitLen, readDevIdScript, readDevIdLen, familyVpp, blankValue, progMemHexBytes, eeMemHexBytes, userIdHexBytes, bytesPerLocation, eeMemBytesPerWord, eeMemAddressIncrement, userIdBytes, addressIncrement);
        }
        return (int)devID;
    }
    
    public static boolean getDeviceInfo (int deviceId, String familyName, int progEntry, byte progEntryLen, int progExit, byte progExitLen, int readDevId, byte readDevIdLen, float vpp, int blankValue, byte progMemHexBytes, byte eeMemHexBytes, byte userIdHexBytes, byte bytesPerLocation, byte eeMemBytesPerWord, byte eeMemAddressIncrement, byte userIdBytes, byte addressIncrement)
    {
        // Get device config first 
        int[] configMasks = new int[8];
        int[] configBlanks = new int[8];
        byte configIndex = 0;
        String configQuery = "SELECT * FROM DEVICE_CONFIG WHERE DEVID="+deviceId;
        rs = DbUtil.execQuery(configQuery);
        
        try 
        {
            if (!rs.isBeforeFirst())
                return false;
            while (rs.next())
            {
                configMasks[configIndex] = rs.getInt("CONFIGMASK");
                configBlanks[configIndex++] = rs.getInt("CONFIGBLANK");
            }
        } catch (SQLException e) {}
        // Get device info
        String deviceQuery = "SELECT * FROM DEVICES WHERE DEVID="+deviceId;
        rs = DbUtil.execQuery(deviceQuery);
        
        try {
            // Should return only one row
            if (!rs.first())
                return false;
            DeviceInfo deviceInfo = new DeviceInfo (
                    familyName, 
                    progEntry,
                    progEntryLen,
                    progExit,
                    progExitLen,
                    readDevId, 
                    readDevIdLen,
                    vpp,
                    blankValue,
                    configMasks,
                    configBlanks,
                    progMemHexBytes,
                    eeMemHexBytes,
                    userIdHexBytes,
                    bytesPerLocation,
                    eeMemBytesPerWord,
                    eeMemAddressIncrement,
                    userIdBytes,
                    addressIncrement,
                    rs.getString("DEVNAME"),
                    rs.getInt("PROGMEMSIZE"),
                    rs.getInt("EEMEMSIZE"),
                    rs.getByte("CONFIGWORDS"),
                    rs.getByte("USERIDWORDS"),
                    rs.getInt("EEADDR"),
                    rs.getInt("CONFIGADDR"),
                    rs.getInt("USERIDADDR"),
                    rs.getInt("BANDGAPMASK"),
                    rs.getShort("CPMASK"),
                    rs.getByte("CPCONFIG"),
                    rs.getBoolean("OSCCALSAVE"),
                    rs.getInt("IGNOREADDR"),
                    rs.getFloat("VDDMIN"),
                    rs.getFloat("VDDMAX"),
                    rs.getFloat("VDDERASE"),
                    rs.getByte("CALIBRATIONWORDS"),
                    rs.getByte("PROGMEMADDRBYTES"),
                    rs.getShort("PROGMEMRDWORDS"),
                    rs.getShort("EERDLOCATIONS"),
                    rs.getShort("PROGMEMWRWORDS"),
                    rs.getByte("PROGMEMPANELBUFS"),
                    rs.getInt("PROGMEMPANELOFFSET"),
                    rs.getShort("EEWRLOCATIONS"),
                    rs.getShort("DPMASK"),
                    rs.getBoolean("WRITECFGONERASE"),
                    rs.getBoolean("BLANKCHECKSKIPUSERIDS"),
                    rs.getShort("IGNOREBYTES"),
                    rs.getInt("BOOTFLASH"),
                    rs.getShort("TESTMEMRDWORDS"),
                    rs.getShort("EEROWERASEWORDS"),
                    rs.getShort("ROWERASESIZE")
            );
             
            String deviceScriptsQuery = "SELECT SCRIPTS.SCRIPTADDRESS, SCRIPTS.SCRIPTLEN, SCRIPTS.SCRIPTTYPE FROM DEVICE_SCRIPTS"
                    + " INNER JOIN SCRIPTS ON DEVICE_SCRIPTS.SCRIPTADDRESS=SCRIPTS.SCRIPTADDRESS"
                    + " WHERE DEVICE_SCRIPTS.DEVID="+deviceId;
            rs = DbUtil.execQuery(deviceScriptsQuery);
            try 
            {
                while (rs.next())
                {
                    switch (rs.getString("SCRIPTTYPE"))
                    {
                        case "CHIP_ERASE":
                            deviceInfo.setChipEraseScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setChipEraseScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                        
                        case "PROG_MEM_ADDR_SET":
                            deviceInfo.setProgMemAddrSetScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setProgMemAddrSetScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "PROG_MEM_READ":
                            deviceInfo.setProgMemRdScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setProgMemRdScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "EE_RD_PREP":
                            deviceInfo.setEeRdPrepScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setEeRdPrepScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "EE_RD":
                            deviceInfo.setEeRdScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setEeRdScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "USER_ID_RD_PREP":
                            deviceInfo.setUserIdRdPrepScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setUserIdRdPrepScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "USER_ID_RD":
                            deviceInfo.setUserIdRdScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setUserIdRdScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "CONFIG_RD_PREP":
                            deviceInfo.setConfigRdPrepScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setConfigRdPrepScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "CONFIG_RD":
                            deviceInfo.setConfigRdScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setConfigRdScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "PROG_MEM_WR_PREP":
                            deviceInfo.setProgMemWrPrepScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setProgMemWrPrepScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "PROG_MEM_WR":
                            deviceInfo.setProgMemWrScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setProgMemWrScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "EE_MEM_WR_PREP":
                            deviceInfo.setEeWrPrepScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setEeWrPrepScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "EE_MEM_WR":
                            deviceInfo.setEeWrScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setEeWrScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "USER_ID_WR_PREP":
                            deviceInfo.setUserIdWrPrepScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setUserIdWrPrepScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "USER_ID_WR":
                            deviceInfo.setUserIdWrScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setUserIdWrScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "CONFIG_WR_PREP":
                            deviceInfo.setConfigWrPrepScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setConfigWrPrepScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "CONFIG_WR":
                            deviceInfo.setConfigWrScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setConfigWrScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "OSCCAL_RD":
                            deviceInfo.setOsscalRdScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setOsscalRdScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "OSCCAL_WR":
                            deviceInfo.setOsscalWrScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setOsscalWrScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "CHIP_ERASE_PREP":
                            deviceInfo.setChipErasePrepScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setChipErasePrepScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "PROG_MEM_ERASE":
                            deviceInfo.setProgMemEraseScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setProgMemEraseScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "EE_MEM_ERASE":
                            deviceInfo.setEeMemEraseScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setEeMemEraseScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                            
                        case "CONFIG_MEM_ERASE":
                            deviceInfo.setConfigMemEraseScript(rs.getInt("SCRIPTADDRESS"));
                            deviceInfo.setConfigMemEraseScriptLen(rs.getByte("SCRIPTLEN"));
                            break;
                    }
                }
            } 
            catch (SQLException e)
            {
                Logger.getLogger(PwJFunctions.class.getName()).log(Level.SEVERE, null, e);
                return false;
            }
            
            PGMMainController.setDevice(deviceInfo);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PwJFunctions.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    
}
