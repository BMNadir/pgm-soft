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

public class USBFunctions {
    
    private static HidServices hidServices ;
    public static HidDevice programmer;
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
    
    public static byte[] checkForProgrammer ()
    {
        // Provide a list of attached devices
        for (HidDevice hidDevice : hidServices.getAttachedHidDevices()) {
            //Compare VID and PID of the detected device to those in the FW
            if(hidDevice.getProductId() == 0x0001 && hidDevice.getVendorId() == 0x0025)
            {
                programmer = hidDevice;
                programmer.open();
                byte[] instruction = new byte[2];
                instruction[0] = 0x02;
                instruction[1] = 0x01;
                USBFunctions.hidWrite(instruction);
                byte[] response = new byte[4];
                while (programmer.read(response, 500) < 0);
                PwJ.setUsbFound(true);
                return response;
            }
        }
        return null;
    }
    
    public static void hidWrite(byte[] cmd)
    {
        byte[] toSend = new byte[cmd.length + 1];
        toSend[0] = 0;
        for (byte i = 0; i < cmd.length ; i++)
        {
            toSend[i+1] = cmd[i];
        }
        programmer.write(toSend, toSend.length, (byte) 0);
    }
    
    
    public static void usbTerminate ()
    {
        programmer.close();
        hidServices.shutdown();
    }
}
