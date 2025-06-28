package pojo;

import java.time.LocalDateTime;

public class Frame {
    public static final byte QUERY_TYPE = (byte) 0x01;
    public static final byte COMPRESSOR_CONTROL_TYPE = (byte) 0x02;
    public static final byte UNLOCK_TYPE = (byte) 0x03;
    public static final byte SET_TEMPERATURE_TYPE = (byte) 0x04;
    public static final byte SET_PARAM_TYPE = (byte) 0x05;
    public static final byte SET_TEMPERATURE_DEVIATION_TYPE = (byte) 0x06;
    public static final byte SET_DEVICE_ADDRESS_TYPE = (byte) 0x09;
    public static final byte UPLOAD_STATUS_TYPE = (byte) 0x10;
    public static final int DIRECTION_RECV = 0;
    public static final int DIRECTION_SEND = 1;

    private int direction;
    private int length;
    private int frameNumber;
    private int deviceAddress;
    private byte type;
    private byte[] data;
    private short checkCode;
    private LocalDateTime time;
    private String portName;

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }

    public int getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(int deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public short getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(short checkCode) {
        this.checkCode = checkCode;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }
}
