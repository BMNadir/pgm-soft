package pwj.device;

public class DeviceInfo {
    // Family level info
    private String familyName;
    private int progEntryScript;
    private int progExitScript;
    private int readDevIdScript;
    private float vpp;
     
    // Memory buffers
    private String name;
    private int[] progMem;
    private int[] eepromMem;
    private int[] config;
    private int[] userID;
    
    // Device level info
    private int eeMemAddr;
    private int configAddr;
    private int userIdAddr;
    private int bandGapMask;
    private short cpMask;
    private byte cpConfig;
    private boolean osccalSave;
    private int ignoreAddres;
    private float vddMin;
    private float vddMax;
    private float vddErase;
    private byte calibrationWords; 
    private byte progMemAddrBytes;
    private short progMemRdWords;
    private short eeRdLocations; 
    private short ProgMemWrWords;
    private byte progMemPanelBufs;
    private int progMemPanelOffset;
    private short eeWrLocations;
    private short dpMask;
    private boolean writeCfgOnErase;
    private boolean blankCheckSkipUserIDs;
    private short ignoreBytes;
    private int bootFlash;
    private short testMemRdWords;
    private short eeRowEraseWords;

    public DeviceInfo(String familyName, int progEntryScript, int progExitScript, int readDevIdScript, float vpp, String name, int progMemSize, int eepromMemSize, int configSize, int userISize, int eeMemAddr, int configAddr, int userIdAddr, int bandGapMask, short cpMask, byte cpConfig, boolean osccalSave, int ignoreAddres, float vddMin, float vddMax, float vddErase, byte calibrationWords, byte progMemAddrBytes, short progMemRdWords, short eeRdLocations, short ProgMemWrWords, byte progMemPanelBufs, int progMemPanelOffset, short eeWrLocations, short dpMask, boolean writeCfgOnErase, boolean blankCheckSkipUserIDs, short ignoreBytes, int bootFlash, short testMemRdWords, short eeRowEraseWords) 
    {
        this.familyName = familyName;
        this.progEntryScript = progEntryScript;
        this.progExitScript = progExitScript;
        this.readDevIdScript = readDevIdScript;
        this.vpp = vpp;
        this.name = name;
        this.progMem = new int[progMemSize];
        this.eepromMem = new int[eepromMemSize];
        this.config = new int[configSize];
        this.userID = new int[userISize];
        this.eeMemAddr = eeMemAddr;
        this.configAddr = configAddr;
        this.userIdAddr = userIdAddr;
        this.bandGapMask = bandGapMask;
        this.cpMask = cpMask;
        this.cpConfig = cpConfig;
        this.osccalSave = osccalSave;
        this.ignoreAddres = ignoreAddres;
        this.vddMin = vddMin;
        this.vddMax = vddMax;
        this.vddErase = vddErase;
        this.calibrationWords = calibrationWords;
        this.progMemAddrBytes = progMemAddrBytes;
        this.progMemRdWords = progMemRdWords;
        this.eeRdLocations = eeRdLocations;
        this.ProgMemWrWords = ProgMemWrWords;
        this.progMemPanelBufs = progMemPanelBufs;
        this.progMemPanelOffset = progMemPanelOffset;
        this.eeWrLocations = eeWrLocations;
        this.dpMask = dpMask;
        this.writeCfgOnErase = writeCfgOnErase;
        this.blankCheckSkipUserIDs = blankCheckSkipUserIDs;
        this.ignoreBytes = ignoreBytes;
        this.bootFlash = bootFlash;
        this.testMemRdWords = testMemRdWords;
        this.eeRowEraseWords = eeRowEraseWords;
    }
    
    
}
