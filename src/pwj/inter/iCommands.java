package pwj.inter;

public interface iCommands {
    // Firmware commands
    static final byte GET_VERSION           = 0x01;
    static final byte TOGGLE_LED            = 0x02;
    static final byte SET_VDD               = 0x03;
    static final byte SET_VPP               = 0x04;
    static final byte READ_VOLTAGES         = 0x05;
    static final byte RUN_ROM_SCRIPT        = 0x06;
    static final byte CLEAR_DOWN_BUFF       = 0x07;
    static final byte WRITE_DOWN_BUFF       = 0x08;
    static final byte CLEAR_UP_BUFF         = 0x09;
    static final byte UPLOAD                = 0x0A;
}
