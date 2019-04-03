package pwj.usb;
/*
 * @author B.M Nadir
 */

import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesSpecification;
import org.hid4java.ScanMode;
import pwj.PwJ;
import pwj.inter.IDefinitions;

public class USBFunctions implements IDefinitions {
    
    private static HidServices hidServices ;
    public static HidDevice programmer = null;
    public static void usbInit ()
    {
        HidServicesSpecification hidServicesSpecification = new HidServicesSpecification();
        hidServicesSpecification.setAutoShutdown(true);
        hidServicesSpecification.setScanInterval(500);
        hidServicesSpecification.setPauseInterval(5000);
        hidServicesSpecification.setScanMode(ScanMode.SCAN_AT_FIXED_INTERVAL_WITH_PAUSE_AFTER_WRITE);
        hidServices = HidManager.getHidServices(hidServicesSpecification);
        hidServices.start();
    }
    
    @SuppressWarnings("empty-statement")
    public static byte[] checkForProgrammer ()
    {
        if (programmer != null) programmer.close();
        // Provide a list of attached devices
        for (HidDevice hidDevice : hidServices.getAttachedHidDevices()) 
        {
            // Compare VID and PID of the detected device to those in the FW
            if(hidDevice.getProductId() == 0x0001 && hidDevice.getVendorId() == 0x0025)
            {
                programmer = hidDevice;
                programmer.open();
                byte[] cmd = new byte[1];
                cmd[0] = GET_VERSION;
                USBFunctions.hidWrite(cmd);
                byte[] response = new byte[4];
                while (programmer.read(response, 500) < 0);
                PwJ.setUsbFound(true);
                return response;
            }
        }
        return null;
    }
    
    public static int hidWrite(byte[] cmd)
    {
        byte[] toSend = new byte[cmd.length + 2]; // 1 byte is used for the leading zero, the other is for data length
        toSend[0] = 0;
        toSend[1] = (byte)(cmd.length);
        for (byte i = 0; i < cmd.length ; i++)
        {
            toSend[i+2] = cmd[i];
        }
        return programmer.write(toSend, toSend.length, (byte) 0);
    }
    
    
    public static void usbTerminate ()
    {
        programmer.close();
        hidServices.shutdown();
    }
}
