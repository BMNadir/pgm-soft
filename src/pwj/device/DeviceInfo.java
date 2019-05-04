package pwj.device;

public class DeviceInfo {
    // Family level info
    private String familyName;
    private int progEntryScript;
    private byte progEntryScriptLen;
    private int progExitScript;
    private byte progExitScriptLen;
    private int readDevIdScript;
    private byte readDevIdScriptLen;
    private int blankValue;
    private float vpp;
    private byte progMemHexBytes; 
    private byte eeMemHexBytes;
    private byte userIdHexBytes;
    private byte bytesPerLocation;
    private byte eeMemBytesPerWord;
    private byte eeMemAddressIncrement;
    private byte userIdBytes;
    private byte addressIncrement;
    
    // Memory buffers
    private String name;
    private int[] progMem;
    private int[] eepromMem;
    private int[] config;
    private int[] userID;
    private int[] configMasks = new int[8];
    private int[] configBlanks = new int[8];
    private int progMemSize;
    private int eeMemSize;
    private int configWords;
    private int userIDs;
    
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
    private short rowEraseSize;
    private int bandGap;
    
    // Device Scripts
    private int chipEraseScript = 0;
    private byte chipEraseScriptLen = 0;
    private int progMemAddrSetScript = 0;
    private byte progMemAddrSetScriptLen = 0;
    private int progMemRdScript = 0;
    private byte progMemRdScriptLen = 0;
    private int eeRdPrepScript = 0;
    private byte eeRdPrepScriptLen = 0;
    private int eeRdScript = 0;
    private byte eeRdScriptLen = 0;
    private int userIdRdPrepScript = 0;
    private byte userIdRdPrepScriptLen = 0;
    private int userIdRdScript = 0;
    private byte userIdRdScriptLen = 0;
    private int configRdPrepScript = 0;
    private byte configRdPrepScriptLen = 0;
    private int configRdScript = 0;
    private byte configRdScriptLen = 0;
    private int progMemWrPrepScript = 0;
    private byte progMemWrPrepScriptLen = 0;
    private int progMemWrScript = 0;
    private byte progMemWrScriptLen = 0;
    private int eeWrPrepScript = 0;
    private byte eeWrPrepScriptLen = 0;
    private int eeWrScript = 0;
    private byte eeWrScriptLen = 0;
    private int userIdWrPrepScript = 0;
    private byte userIdWrPrepScriptLen = 0;
    private int userIdWrScript = 0;
    private byte userIdWrScriptLen = 0;
    private int configWrPrepScript = 0;
    private byte configWrPrepScriptLen = 0;
    private int configWrScript = 0;
    private byte configWrScriptLen = 0;
    private int osscalRdScript = 0;
    private byte osscalRdScriptLen = 0;
    private int osscalWrScript = 0;
    private byte osscalWrScriptLen = 0;
    private int chipErasePrepScript = 0;
    private byte chipErasePrepScriptLen = 0;
    private int progMemEraseScript = 0;
    private byte progMemEraseScriptLen = 0;
    private int eeMemEraseScript = 0;
    private byte eeMemEraseScriptLen = 0;
    private int configMemEraseScript = 0;
    private byte configMemEraseScriptLen = 0;
    private int eeRowEraseScript = 0;
    private byte eeRowEraseScriptLen = 0;
    private int rowEraseScript = 0;
    private byte rowEraseScriptLen = 0;
    
    public DeviceInfo(String familyName, int progEntryScript, byte progEntryScriptLen, int progExitScript, byte progExitScriptLen, int readDevIdScript, byte readDevIdScriptLen, float vpp, int blankValue, int[] deviceConfigMasks, int[] configBlanks, byte progMemHexBytes, byte eeMemHexBytes, byte userIdHexBytes, byte bytesPerLocation, byte eeMemBytesPerWord, byte eeMemAddressIncrement, byte userIdBytes, byte addressIncrement, String name, int progMemSize, int eepromMemSize, int configSize, int userIdSize, int eeMemAddr, int configAddr, int userIdAddr, int bandGapMask, short cpMask, byte cpConfig, boolean osccalSave, int ignoreAddres, float vddMin, float vddMax, float vddErase, byte calibrationWords, byte progMemAddrBytes, short progMemRdWords, short eeRdLocations, short ProgMemWrWords, byte progMemPanelBufs, int progMemPanelOffset, short eeWrLocations, short dpMask, boolean writeCfgOnErase, boolean blankCheckSkipUserIDs, short ignoreBytes, int bootFlash, short testMemRdWords, short eeRowEraseWords, short rowEraseSize) 
    {
        this.familyName = familyName;
        this.progEntryScript = progEntryScript;
        this.progEntryScriptLen = progEntryScriptLen;
        this.progExitScript = progExitScript;
        this.progExitScriptLen = progExitScriptLen;
        this.readDevIdScript = readDevIdScript;
        this.readDevIdScriptLen = readDevIdScriptLen;
        this.vpp = vpp;
        this.blankValue = blankValue;
        this.progMemHexBytes = progMemHexBytes;
        this.eeMemHexBytes = eeMemHexBytes;
        this.userIdHexBytes = userIdHexBytes;
        this.bytesPerLocation = bytesPerLocation;
        this.eeMemBytesPerWord = eeMemBytesPerWord;
        this.eeMemAddressIncrement = eeMemAddressIncrement;
        this.userIdBytes = userIdBytes;
        this.addressIncrement = addressIncrement;
        this.name = name;
        this.progMemSize = progMemSize;
        this.eeMemSize = eepromMemSize;
        this.configWords = configSize;
        this.userIDs = userIdSize;
        this.progMem = new int[progMemSize];
        this.eepromMem = new int[eeMemSize];
        this.config = new int[configWords];
        this.userID = new int[userIDs];
        this.configMasks = deviceConfigMasks;
        this.configBlanks = configBlanks;
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
        this.rowEraseSize = rowEraseSize;
        
        
        clearProgMem ();
        clearConfigs ();
        clearEeMem ();
        clearUserIDs();
    }
    
    ///////// GETTERS /////////
    public String getFamilyName() {
        return familyName;
    }

    public int getProgEntryScript() {
        return progEntryScript;
    }

    public int getProgExitScript() {
        return progExitScript;
    }

    public int getReadDevIdScript() {
        return readDevIdScript;
    }

    public byte getProgEntryScriptLen() {
        return progEntryScriptLen;
    }

    public byte getProgExitScriptLen() {
        return progExitScriptLen;
    }

    public byte getReadDevIdScriptLen() {
        return readDevIdScriptLen;
    }

    public float getVpp() {
        return vpp;
    }

    public int getBlankValue() {
        return blankValue;
    }

    public byte getProgMemHexBytes() {
        return progMemHexBytes;
    }

    public byte getEeMemHexBytes() {
        return eeMemHexBytes;
    }

    public byte getUserIdHexBytes() {
        return userIdHexBytes;
    }
    
    public int[] getConfigMasks() {
        return configMasks;
    }

    public int[] getConfigBlanks() {
        return configBlanks;
    }

    public int getUserIDs() {
        return userIDs;
    }

    public byte getBytesPerLocation() {
        return bytesPerLocation;
    }

    public byte getEeMemBytesPerWord() {
        return eeMemBytesPerWord;
    }

    public byte getEeMemAddressIncrement() {
        return eeMemAddressIncrement;
    }

    public byte getUserIdBytes() {
        return userIdBytes;
    }

    public byte getAddressIncrement() {
        return addressIncrement;
    }
    
    public String getName() {
        return name;
    }

    public int[] getProgMem() {
        return progMem;
    }

    public int[] getEepromMem() {
        return eepromMem;
    }

    public int[] getConfig() {
        return config;
    }

    public int[] getUserID() {
        return userID;
    }

    public int getProgMemSize() {
        return progMemSize;
    }

    public int getEeMemSize() {
        return eeMemSize;
    }
    
    public int getEeMemAddr() {
        return eeMemAddr;
    }

    public int getConfigAddr() {
        return configAddr;
    }

    public int getConfigWords() {
        return configWords;
    }
    
    public int getUserIdAddr() {
        return userIdAddr;
    }

    public int getBandGapMask() {
        return bandGapMask;
    }

    public short getCpMask() {
        return cpMask;
    }

    public byte getCpConfig() {
        return cpConfig;
    }

    public boolean getOsccalSave() {
        return osccalSave;
    }

    public int getIgnoreAddres() {
        return ignoreAddres;
    }

    public float getVddMin() {
        return vddMin;
    }

    public float getVddMax() {
        return vddMax;
    }

    public float getVddErase() {
        return vddErase;
    }

    public byte getCalibrationWords() {
        return calibrationWords;
    }

    public byte getProgMemAddrBytes() {
        return progMemAddrBytes;
    }

    public short getProgMemRdWords() {
        return progMemRdWords;
    }

    public short getEeRdLocations() {
        return eeRdLocations;
    }

    public short getProgMemWrWords() {
        return ProgMemWrWords;
    }

    public byte getProgMemPanelBufs() {
        return progMemPanelBufs;
    }

    public int getProgMemPanelOffset() {
        return progMemPanelOffset;
    }

    public short getEeWrLocations() {
        return eeWrLocations;
    }

    public short getDpMask() {
        return dpMask;
    }

    public boolean isWriteCfgOnErase() {
        return writeCfgOnErase;
    }

    public boolean isBlankCheckSkipUserIDs() {
        return blankCheckSkipUserIDs;
    }

    public short getIgnoreBytes() {
        return ignoreBytes;
    }

    public int getBootFlash() {
        return bootFlash;
    }

    public short getTestMemRdWords() {
        return testMemRdWords;
    }

    public short getEeRowEraseWords() {
        return eeRowEraseWords;
    }

    public short getRowEraseSize() {
        return rowEraseSize;
    }

    public boolean isOsccalSave() {
        return osccalSave;
    }

    public int getBandGap() {
        return bandGap;
    }

    public int getChipEraseScript() {
        return chipEraseScript;
    }

    public byte getChipEraseScriptLen() {
        return chipEraseScriptLen;
    }

    public int getProgMemAddrSetScript() {
        return progMemAddrSetScript;
    }

    public byte getProgMemAddrSetScriptLen() {
        return progMemAddrSetScriptLen;
    }

    public int getProgMemRdScript() {
        return progMemRdScript;
    }

    public byte getProgMemRdScriptLen() {
        return progMemRdScriptLen;
    }

    public int getEeRdPrepScript() {
        return eeRdPrepScript;
    }

    public byte getEeRdPrepScriptLen() {
        return eeRdPrepScriptLen;
    }

    public int getEeRdScript() {
        return eeRdScript;
    }

    public byte getEeRdScriptLen() {
        return eeRdScriptLen;
    }

    public int getUserIdRdPrepScript() {
        return userIdRdPrepScript;
    }

    public byte getUserIdRdPrepScriptLen() {
        return userIdRdPrepScriptLen;
    }

    public int getUserIdRdScript() {
        return userIdRdScript;
    }

    public byte getUserIdRdScriptLen() {
        return userIdRdScriptLen;
    }

    public int getConfigRdPrepScript() {
        return configRdPrepScript;
    }

    public byte getConfigRdPrepScriptLen() {
        return configRdPrepScriptLen;
    }

    public int getConfigRdScript() {
        return configRdScript;
    }

    public byte getConfigRdScriptLen() {
        return configRdScriptLen;
    }

    public int getProgMemWrPrepScript() {
        return progMemWrPrepScript;
    }

    public byte getProgMemWrPrepScriptLen() {
        return progMemWrPrepScriptLen;
    }

    public int getProgMemWrScript() {
        return progMemWrScript;
    }

    public byte getProgMemWrScriptLen() {
        return progMemWrScriptLen;
    }

    public int getEeWrPrepScript() {
        return eeWrPrepScript;
    }

    public byte getEeWrPrepScriptLen() {
        return eeWrPrepScriptLen;
    }

    public int getEeWrScript() {
        return eeWrScript;
    }

    public byte getEeWrScriptLen() {
        return eeWrScriptLen;
    }

    public int getUserIdWrPrepScript() {
        return userIdWrPrepScript;
    }

    public byte getUserIdWrPrepScriptLen() {
        return userIdWrPrepScriptLen;
    }

    public int getUserIdWrScript() {
        return userIdWrScript;
    }

    public byte getUserIdWrScriptLen() {
        return userIdWrScriptLen;
    }

    public int getConfigWrPrepScript() {
        return configWrPrepScript;
    }

    public byte getConfigWrPrepScriptLen() {
        return configWrPrepScriptLen;
    }

    public int getConfigWrScript() {
        return configWrScript;
    }

    public byte getConfigWrScriptLen() {
        return configWrScriptLen;
    }

    public int getOsscalRdScript() {
        return osscalRdScript;
    }

    public byte getOsscalRdScriptLen() {
        return osscalRdScriptLen;
    }

    public int getOsscalWrScript() {
        return osscalWrScript;
    }

    public byte getOsscalWrScriptLen() {
        return osscalWrScriptLen;
    }

    public int getChipErasePrepScript() {
        return chipErasePrepScript;
    }

    public byte getChipErasePrepScriptLen() {
        return chipErasePrepScriptLen;
    }

    public int getProgMemEraseScript() {
        return progMemEraseScript;
    }

    public byte getProgMemEraseScriptLen() {
        return progMemEraseScriptLen;
    }

    public int getEeMemEraseScript() {
        return eeMemEraseScript;
    }

    public byte getEeMemEraseScriptLen() {
        return eeMemEraseScriptLen;
    }

    public int getConfigMemEraseScript() {
        return configMemEraseScript;
    }

    public byte getConfigMemEraseScriptLen() {
        return configMemEraseScriptLen;
    }

    public int getEeRowEraseScript() {
        return eeRowEraseScript;
    }

    public byte getEeRowEraseScriptLen() {
        return eeRowEraseScriptLen;
    }

    public int getRowEraseScript() {
        return rowEraseScript;
    }

    public byte getRowEraseScriptLen() {
        return rowEraseScriptLen;
    }
    
    
    
    ////////// SETTERS

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setProgEntryScript(int progEntryScript) {
        this.progEntryScript = progEntryScript;
    }

    public void setProgExitScript(int progExitScript) {
        this.progExitScript = progExitScript;
    }

    public void setReadDevIdScript(int readDevIdScript) {
        this.readDevIdScript = readDevIdScript;
    }

    public void setVpp(float vpp) {
        this.vpp = vpp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProgMem(int[] progMem) {
        this.progMem = progMem;
    }

    public void setEepromMem(int[] eepromMem) {
        this.eepromMem = eepromMem;
    }

    public void setConfig(int[] config) {
        this.config = config;
    }

    public void setUserID(int[] userID) {
        this.userID = userID;
    }

    public void setProgMemSize(int progMemSize) {
        this.progMemSize = progMemSize;
    }

    public void setEeMemSize(int eeMemSize) {
        this.eeMemSize = eeMemSize;
    }

    public void setConfigWords(int configWords) {
        this.configWords = configWords;
    }

    public void setUserIDs(int userIDs) {
        this.userIDs = userIDs;
    }

    public void setEeMemAddr(int eeMemAddr) {
        this.eeMemAddr = eeMemAddr;
    }

    public void setConfigAddr(int configAddr) {
        this.configAddr = configAddr;
    }

    public void setUserIdAddr(int userIdAddr) {
        this.userIdAddr = userIdAddr;
    }

    public void setBandGapMask(int bandGapMask) {
        this.bandGapMask = bandGapMask;
    }

    public void setCpMask(short cpMask) {
        this.cpMask = cpMask;
    }

    public void setCpConfig(byte cpConfig) {
        this.cpConfig = cpConfig;
    }

    public void setOsccalSave(boolean osccalSave) {
        this.osccalSave = osccalSave;
    }

    public void setIgnoreAddres(int ignoreAddres) {
        this.ignoreAddres = ignoreAddres;
    }

    public void setVddMin(float vddMin) {
        this.vddMin = vddMin;
    }

    public void setVddMax(float vddMax) {
        this.vddMax = vddMax;
    }

    public void setVddErase(float vddErase) {
        this.vddErase = vddErase;
    }

    public void setCalibrationWords(byte calibrationWords) {
        this.calibrationWords = calibrationWords;
    }

    public void setProgMemAddrBytes(byte progMemAddrBytes) {
        this.progMemAddrBytes = progMemAddrBytes;
    }

    public void setProgMemRdWords(short progMemRdWords) {
        this.progMemRdWords = progMemRdWords;
    }

    public void setEeRdLocations(short eeRdLocations) {
        this.eeRdLocations = eeRdLocations;
    }

    public void setProgMemWrWords(short ProgMemWrWords) {
        this.ProgMemWrWords = ProgMemWrWords;
    }

    public void setProgMemPanelBufs(byte progMemPanelBufs) {
        this.progMemPanelBufs = progMemPanelBufs;
    }

    public void setProgMemPanelOffset(int progMemPanelOffset) {
        this.progMemPanelOffset = progMemPanelOffset;
    }

    public void setEeWrLocations(short eeWrLocations) {
        this.eeWrLocations = eeWrLocations;
    }

    public void setDpMask(short dpMask) {
        this.dpMask = dpMask;
    }

    public void setWriteCfgOnErase(boolean writeCfgOnErase) {
        this.writeCfgOnErase = writeCfgOnErase;
    }

    public void setBlankCheckSkipUserIDs(boolean blankCheckSkipUserIDs) {
        this.blankCheckSkipUserIDs = blankCheckSkipUserIDs;
    }

    public void setIgnoreBytes(short ignoreBytes) {
        this.ignoreBytes = ignoreBytes;
    }

    public void setBootFlash(int bootFlash) {
        this.bootFlash = bootFlash;
    }

    public void setTestMemRdWords(short testMemRdWords) {
        this.testMemRdWords = testMemRdWords;
    }

    public void setEeRowEraseWords(short eeRowEraseWords) {
        this.eeRowEraseWords = eeRowEraseWords;
    }

    public void setProgEntryScriptLen(byte progEntryScriptLen) {
        this.progEntryScriptLen = progEntryScriptLen;
    }

    public void setProgExitScriptLen(byte progExitScriptLen) {
        this.progExitScriptLen = progExitScriptLen;
    }

    public void setReadDevIdScriptLen(byte readDevIdScriptLen) {
        this.readDevIdScriptLen = readDevIdScriptLen;
    }

    public void setBlankValue(int blankValue) {
        this.blankValue = blankValue;
    }

    public void setProgMemHexBytes(byte progMemHexBytes) {
        this.progMemHexBytes = progMemHexBytes;
    }

    public void setEeMemHexBytes(byte eeMemHexBytes) {
        this.eeMemHexBytes = eeMemHexBytes;
    }

    public void setUserIdHexBytes(byte userIdHexBytes) {
        this.userIdHexBytes = userIdHexBytes;
    }

    public void setBytesPerLocation(byte bytesPerLocation) {
        this.bytesPerLocation = bytesPerLocation;
    }

    public void setEeMemBytesPerWord(byte eeMemBytesPerWord) {
        this.eeMemBytesPerWord = eeMemBytesPerWord;
    }

    public void setEeMemAddressIncrement(byte eeMemAddressIncrement) {
        this.eeMemAddressIncrement = eeMemAddressIncrement;
    }

    public void setUserIdBytes(byte userIdBytes) {
        this.userIdBytes = userIdBytes;
    }

    public void setConfigMasks(int[] configMasks) {
        this.configMasks = configMasks;
    }

    public void setConfigBlanks(int[] configBlanks) {
        this.configBlanks = configBlanks;
    }

    public void setRowEraseSize(short rowEraseSize) {
        this.rowEraseSize = rowEraseSize;
    }

    public void setBandGap(int bandGap) {
        this.bandGap = bandGap;
    }

    public void setChipEraseScript(int chipEraseScript) {
        this.chipEraseScript = chipEraseScript;
    }

    public void setChipEraseScriptLen(byte chipEraseScriptLen) {
        this.chipEraseScriptLen = chipEraseScriptLen;
    }

    public void setProgMemAddrSetScript(int progMemAddrSetScript) {
        this.progMemAddrSetScript = progMemAddrSetScript;
    }

    public void setProgMemAddrSetScriptLen(byte progMemAddrSetScriptLen) {
        this.progMemAddrSetScriptLen = progMemAddrSetScriptLen;
    }

    public void setProgMemRdScript(int progMemRdScript) {
        this.progMemRdScript = progMemRdScript;
    }

    public void setProgMemRdScriptLen(byte progMemRdScriptLen) {
        this.progMemRdScriptLen = progMemRdScriptLen;
    }

    public void setEeRdPrepScript(int eeRdPrepScript) {
        this.eeRdPrepScript = eeRdPrepScript;
    }

    public void setEeRdPrepScriptLen(byte eeRdPrepScriptLen) {
        this.eeRdPrepScriptLen = eeRdPrepScriptLen;
    }

    public void setEeRdScript(int eeRdScript) {
        this.eeRdScript = eeRdScript;
    }

    public void setEeRdScriptLen(byte eeRdScriptLen) {
        this.eeRdScriptLen = eeRdScriptLen;
    }

    public void setUserIdRdPrepScript(int userIdRdPrepScript) {
        this.userIdRdPrepScript = userIdRdPrepScript;
    }

    public void setUserIdRdPrepScriptLen(byte userIdRdPrepScriptLen) {
        this.userIdRdPrepScriptLen = userIdRdPrepScriptLen;
    }

    public void setUserIdRdScript(int userIdRdScript) {
        this.userIdRdScript = userIdRdScript;
    }

    public void setUserIdRdScriptLen(byte userIdRdScriptLen) {
        this.userIdRdScriptLen = userIdRdScriptLen;
    }

    public void setConfigRdPrepScript(int configRdPrepScript) {
        this.configRdPrepScript = configRdPrepScript;
    }

    public void setConfigRdPrepScriptLen(byte configRdPrepScriptLen) {
        this.configRdPrepScriptLen = configRdPrepScriptLen;
    }

    public void setConfigRdScript(int configRdScript) {
        this.configRdScript = configRdScript;
    }

    public void setConfigRdScriptLen(byte configRdScriptLen) {
        this.configRdScriptLen = configRdScriptLen;
    }

    public void setProgMemWrPrepScript(int progMemWrPrepScript) {
        this.progMemWrPrepScript = progMemWrPrepScript;
    }

    public void setProgMemWrPrepScriptLen(byte progMemWrPrepScriptLen) {
        this.progMemWrPrepScriptLen = progMemWrPrepScriptLen;
    }

    public void setProgMemWrScript(int progMemWrScript) {
        this.progMemWrScript = progMemWrScript;
    }

    public void setProgMemWrScriptLen(byte progMemWrScriptLen) {
        this.progMemWrScriptLen = progMemWrScriptLen;
    }

    public void setEeWrPrepScript(int eeWrPrepScript) {
        this.eeWrPrepScript = eeWrPrepScript;
    }

    public void setEeWrPrepScriptLen(byte eeWrPrepScriptLen) {
        this.eeWrPrepScriptLen = eeWrPrepScriptLen;
    }

    public void setEeWrScript(int eeWrScript) {
        this.eeWrScript = eeWrScript;
    }

    public void setEeWrScriptLen(byte eeWrScriptLen) {
        this.eeWrScriptLen = eeWrScriptLen;
    }

    public void setUserIdWrPrepScript(int userIdWrPrepScript) {
        this.userIdWrPrepScript = userIdWrPrepScript;
    }

    public void setUserIdWrPrepScriptLen(byte userIdWrPrepScriptLen) {
        this.userIdWrPrepScriptLen = userIdWrPrepScriptLen;
    }

    public void setUserIdWrScript(int userIdWrScript) {
        this.userIdWrScript = userIdWrScript;
    }

    public void setUserIdWrScriptLen(byte userIdWrScriptLen) {
        this.userIdWrScriptLen = userIdWrScriptLen;
    }

    public void setConfigWrPrepScript(int configWrPrepScript) {
        this.configWrPrepScript = configWrPrepScript;
    }

    public void setConfigWrPrepScriptLen(byte configWrPrepScriptLen) {
        this.configWrPrepScriptLen = configWrPrepScriptLen;
    }

    public void setConfigWrScript(int configWrScript) {
        this.configWrScript = configWrScript;
    }

    public void setConfigWrScriptLen(byte configWrScriptLen) {
        this.configWrScriptLen = configWrScriptLen;
    }

    public void setOsscalRdScript(int osscalRdScript) {
        this.osscalRdScript = osscalRdScript;
    }

    public void setOsscalRdScriptLen(byte osscalRdScriptLen) {
        this.osscalRdScriptLen = osscalRdScriptLen;
    }

    public void setOsscalWrScript(int osscalWrScript) {
        this.osscalWrScript = osscalWrScript;
    }

    public void setOsscalWrScriptLen(byte osscalWrScriptLen) {
        this.osscalWrScriptLen = osscalWrScriptLen;
    }

    public void setChipErasePrepScript(int chipErasePrepScript) {
        this.chipErasePrepScript = chipErasePrepScript;
    }

    public void setChipErasePrepScriptLen(byte chipErasePrepScriptLen) {
        this.chipErasePrepScriptLen = chipErasePrepScriptLen;
    }

    public void setProgMemEraseScript(int progMemEraseScript) {
        this.progMemEraseScript = progMemEraseScript;
    }

    public void setProgMemEraseScriptLen(byte progMemEraseScriptLen) {
        this.progMemEraseScriptLen = progMemEraseScriptLen;
    }

    public void setEeMemEraseScript(int eeMemEraseScript) {
        this.eeMemEraseScript = eeMemEraseScript;
    }

    public void setEeMemEraseScriptLen(byte eeMemEraseScriptLen) {
        this.eeMemEraseScriptLen = eeMemEraseScriptLen;
    }

    public void setConfigMemEraseScript(int configMemEraseScript) {
        this.configMemEraseScript = configMemEraseScript;
    }

    public void setConfigMemEraseScriptLen(byte configMemEraseScriptLen) {
        this.configMemEraseScriptLen = configMemEraseScriptLen;
    }

    public void setEeRowEraseScript(int eeRowEraseScript) {
        this.eeRowEraseScript = eeRowEraseScript;
    }

    public void setEeRowEraseScriptLen(byte eeRowEraseScriptLen) {
        this.eeRowEraseScriptLen = eeRowEraseScriptLen;
    }

    public void setRowEraseScript(int rowEraseScript) {
        this.rowEraseScript = rowEraseScript;
    }

    public void setRowEraseScriptLen(byte rowEraseScriptLen) {
        this.rowEraseScriptLen = rowEraseScriptLen;
    }
    
    
    
    
    
    private void clearProgMem ()
    {
        for (int i = 0; i < progMem.length; i++)
        {
            progMem[i] = blankValue;
        }
    }
    
    private void clearConfigs ()
    {
        for (int i = 0; i < configWords; i++)
        {
            config[i] = configBlanks[i];
        }
    }
    
    private void clearEeMem ()
    {
        int eeBlankVal = 0xFF;
        if (eeMemAddressIncrement > 1)
        {
            eeBlankVal = 0xFFFF;
        }
        if (blankValue == 0xFFF)
        { 
            eeBlankVal = 0xFFF;
        }
        for (int i = 0; i < eepromMem.length; i++)
        {
            eepromMem[i] = eeBlankVal;                  // 8-bit eeprom will just use 8 LSBs
        }
    }
    
    public void clearUserIDs()
    {
        if (userID.length > 0)
        {
            int idBlank = blankValue;
            if (userIDs == 1)
            {
                idBlank = 0xFF;
            }
            for (int i = 0; i < userID.length; i++)
            {
                userID[i] = idBlank;
            }
        }
    }
}
