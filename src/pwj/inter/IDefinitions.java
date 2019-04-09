package pwj.inter;

public interface IDefinitions {
    
    // Firmware commands
    static final byte GET_VERSION                   = 0x01;
    static final byte TOGGLE_LED                    = 0x02;
    static final byte SET_VDD                       = 0x03;
    static final byte SET_VPP                       = 0x04;
    static final byte READ_VOLTAGES                 = 0x05;
    static final byte RUN_ROM_SCRIPT                = 0x06;
    static final byte CLEAR_DOWN_BUFF               = 0x07;
    static final byte WRITE_DOWN_BUFF               = 0x08;
    static final byte CLEAR_UP_BUFF                 = 0x09;
    static final byte UPLOAD                        = 0x0A;
    static final byte RUN_USB_SCRIPT                = 0x0B;
    static final byte RUN_ROM_SCRIIPT_ITR           = 0x0C;
    static final byte UPLOAD_WITHOUT_LENGTH         = 0x0d;
    
    // Script commands
    static final byte READ_N_BITS                   = (byte)0xD5;
    static final byte READ_BYTE                     = (byte)0xD6;
    static final byte VISI24                        = (byte)0xD7;
    static final byte NOP24                         = (byte)0xD8;
    static final byte COREINST24                    = (byte)0xD9;
    static final byte COREINST18                    = (byte)0xDA;
    static final byte POP_DOWNLOAD_BUFFER           = (byte)0xDB;
    static final byte READ_ICSP_STATES              = (byte)0xDC;
    static final byte LOOP_BUFFER                   = (byte)0xDD;
    static final byte WRITE_SFR                     = (byte)0xDE;
    static final byte READ_SFR                      = (byte)0xDF;
    static final byte EXIT_SCRIPT                   = (byte)0xE0;
    static final byte GOTO_IDX                      = (byte)0xE1;
    static final byte IF_GT_GOTO                    = (byte)0xE2;
    static final byte IF_EQ_GOTO                    = (byte)0xE3;
    static final byte SHORT_DELAY                   = (byte)0xE4;
    static final byte LONG_DELAY                    = (byte)0xE5;
    static final byte LOOP                          = (byte)0xE6;
    static final byte SHIFT_BITS_IN_CMD             = (byte)0xE7;
    static final byte SHIFT_BITS_IN_BUFFER          = (byte)0xE8;
    static final byte SHIFT_BITS_OUT_BUFFER         = (byte)0xE9;
    static final byte SHIFT_BITS_OUT_CMD            = (byte)0xEA;
    static final byte SHIFT_BYTE_IN                 = (byte)0xEB;
    static final byte SHIFT_BYTE_IN_BUFFER          = (byte)0xEC;
    static final byte SHIFT_BYTE_OUT_BUFFER         = (byte)0xED;
    static final byte SHIFT_BYTE_OUT                = (byte)0xEE;
    static final byte SET_ICSP_PINS_CMD             = (byte)0xEF;
    static final byte MCLR_TGT_GND_OFF              = (byte)0xF0;
    static final byte MCLR_TGT_GND_ON               = (byte)0xF1;
    static final byte VPP_PWM_OFF                   = (byte)0xF2;
    static final byte VPP_PWM_ON                    = (byte)0xF3;
    static final byte VPP_ON_CMD                    = (byte)0xF4;
    static final byte VPP_OFF                       = (byte)0xF5;
    static final byte VDD_GND_ON                    = (byte)0xF6;
    static final byte VDD_GND_OFF                   = (byte)0xF7;
    static final byte VDD_ON                        = (byte)0xF8;
    static final byte VDD_OFF                       = (byte)0xF9;
    
    // Script types
    static final byte PROG_ENTRY                    = 0x01;
    static final byte PROG_EXIT                     = 0x02;
    static final byte READ_DEV_ID                   = 0x03;
    static final byte PROG_ENTRY_VPP_FIRST          = 0x04;
    static final byte CHIP_ERASE                    = 0x05;
    static final byte PROG_MEM_ADDR_SET             = 0x06;
    static final byte PROG_MEM_READ                 = 0x07;
    static final byte EE_RD_PREP                    = 0x08;
    static final byte EE_RD                         = 0x09;
    static final byte USER_ID_RD_PREP               = 0x0A;
    static final byte USER_ID_RD                    = 0x0B;
    static final byte CONFIG_RD_PREP                = 0x0C;
    static final byte CONFIG_RD                     = 0x0D;
    static final byte PROG_MEM_WR_PREP              = 0x0E;
    static final byte PROG_MEM_WR                   = 0x0F;
    static final byte EE_MEM_WR_PREP                = 0x10;
    static final byte EE_MEM_WR                     = 0x11;
    static final byte USER_ID_WR_PREP               = 0x12;
    static final byte USER_ID_WR                    = 0x13;
    static final byte CONFIG_WR_PREP                = 0x14;
    static final byte CONFIG_WR                     = 0x15;
    static final byte OSCCAL_RD                     = 0x16;
    static final byte OSCCAL_WR                     = 0x17;
    static final byte CHIP_ERASE_PREP               = 0x18;
    static final byte PROG_MEM_ERASE                = 0x19;
    static final byte EE_MEM_ERASE                  = 0x1A;
    static final byte CONFIG_MEM_ERASE              = 0x1B;
    static final byte TEST_MEM_RD                   = 0x1C;
    static final byte EE_ROW_ERASE                  = 0x1D;
    
    // Constants 
    static final float SELF_POWERED_DEVICE_THRESHOLD   = 2.3F;
    static enum writeState {FAILED, SUCCEED};
    static final int DOWNLOAD_BUFFER_SIZE = 256;
    static final int UPLOAD_BUFFER_SIZE = 256; 
}


























