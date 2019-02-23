package pwj.functions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pwj.db.DbUtil;
import pwj.usb.USBFunctions;
import static pwj.usb.USBFunctions.programmer;
import pwj.inter.IDefinitions;

public class PwJFunctions implements IDefinitions {
    private static float vdd;
    private static float lastVdd = 3.3f; // For PIC24F, dsPIC33, PIC18F_J_ 
    private static float vpp;
    private static boolean selfPoweredDeviceFound = false;
    private static ResultSet resultSet;
    
    // Check if target device is self powered
    public static boolean checkForPoweredDevice ()
    {
        if (getVddVppVoltages ())
        {
           if (vdd > selfPoweredDeviceThreshold)
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
    public static boolean getVddVppVoltages ()
    {
        byte[] cmd = new byte[1];
        cmd[0] = READ_VOLTAGES;
        
        USBFunctions.hidWrite(cmd);
        byte[] response = new byte[5];
        while (programmer.read(response, 500) < 0);
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
    
    public static void identifyDevice()
    {
        if (!selfPoweredDeviceFound)
        {
            // Initialize VDD to 3.3V
            setVdd(3.3F, 0.85f); 
        }
        // Iterate over all families, last family has FamilyID = 11
        for (byte i = 11; i < 12; i++)
            searchForDevice(i);
    }
    
    private static boolean searchForDevice (byte id)
    {
        int progEntryScript = 0; // Used for the ProgEntry script address
        int progExitScript = 0;  // Used for the ProgExit script address
        int readDevIdScript = 0; // Used for the ReadDevID script address
        int devIdMask = 0;       // Stores the deviceID mask
        
        byte progEntryLen = 0;
        byte progExitLen = 0;
        byte readDevIdLen = 0;
        
        float familyVpp = 0;
        
        // https://stackoverflow.com/a/7150290
        String query = "SELECT SCRIPTS.SCRIPTADDRESS, SCRIPTS.SCRIPTLEN, SCRIPTS.SCRIPTTYPE, FAMILY.VPP, FAMILY.DEVIDMASK"
                + " FROM FAMILY_SCRIPTS"
                + " INNER JOIN FAMILY ON FAMILY.FAMILYID=FAMILY_SCRIPTS.FAMILYID"
                + " INNER JOIN SCRIPTS ON FAMILY_SCRIPTS.SCRIPTADDRESS = SCRIPTS.SCRIPTADDRESS"
                + " WHERE FAMILY.FAMILYID ="+id;
        
        resultSet = DbUtil.execQuery(query);
        try {
            // Should return VPP and all scripts used by a particular family
            while (resultSet.next())
            {
                familyVpp = resultSet.getFloat("VPP");
                if (resultSet.getString("SCRIPTTYPE").equals("PROG_ENTRY"))
                {
                    progEntryScript = resultSet.getInt("SCRIPTADDRESS");
                    progEntryLen = resultSet.getByte("SCRIPTLEN");
                }
                
                else if (resultSet.getString("SCRIPTTYPE").equals("PROG_EXIT"))
                {
                    progExitScript = resultSet.getInt("SCRIPTADDRESS");
                    progExitLen = resultSet.getByte("SCRIPTLEN");
                    devIdMask = resultSet.getInt("DEVIDMASK");
                }
                else if (resultSet.getString("SCRIPTTYPE").equals("READ_DEV_ID"))
                {
                    readDevIdScript = resultSet.getInt("SCRIPTADDRESS");
                    readDevIdLen = resultSet.getByte("SCRIPTLEN");
                }
                    
            }
        } catch (SQLException ex) {
            Logger.getLogger(PwJFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
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

        cmd [9] = RUN_ROM_SCRIPT;
        cmd [10] = progEntryLen;
        cmd [11] = (byte) progEntryScript;
        cmd [12] = (byte)(progEntryScript >> 8);
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
        byte[] response = new byte[5];
        while (programmer.read(response, 500) < 0);
        
        long devID =  response[4] * 0x1000000 + response[3] * 0x10000 + response[2] * 0x100 + response[1];
        
        // For midrange, deviceID should be shifted to the right by one
        if (id == 11)   devID >>= 1;
        devID &= devIdMask;
        System.out.println(Long.toHexString(devID));
        return true;
    }
    
    public static boolean writeDevice ()
    {
        // See if programmer is still connected 
        if (USBFunctions.checkForProgrammer() == null)
        {
            return false;
        }
        
        checkForPoweredDevice ();
        
        if (!searchForDevice((byte)11))
        {
            return false;
        }
        
        return true;
    }
}
