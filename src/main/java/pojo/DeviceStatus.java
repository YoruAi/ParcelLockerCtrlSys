package pojo;

import java.time.LocalDateTime;

public class DeviceStatus {
    public static final int DRAWER_NUM = 10;
    public static final byte SYSTEM_STATUS_OFF = 0;
    public static final byte SYSTEM_STATUS_PRE = 1;
    public static final byte SYSTEM_STATUS_ON = 2;
    public static final byte COMPRESSOR_STATUS_OFF = 0;
    public static final byte COMPRESSOR_STATUS_PRE = 1;
    public static final byte COMPRESSOR_STATUS_ON = 2;
    public static final byte COMPRESSOR_STATUS_BROKEN = 3;
    public static final int LOCK_STATUS_ON = 1;
    public static final int LOCK_STATUS_OFF = 0;
    public static final double MIN_TEMP = -63.5;
    public static final double MAX_TEMP = 63.5;

    private String deviceCode = "FFFFFFFFFF";
    private int deviceAddress = 1;
    private byte systemStatus = SYSTEM_STATUS_OFF;
    private byte compressorStatus = COMPRESSOR_STATUS_OFF;
    private double currentTemperature = 0;
    private double setTemperature = 4;
    private short lockStatus = 0;
    private int statusUploadInterval = 1;
    private int compressorStartupDelay = 30;
    private int temperatureDeviation = 2;
    private LocalDateTime time = null;
    private String portName = null;

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public int getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(int deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public byte getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(byte systemStatus) {
        this.systemStatus = systemStatus;
    }

    public byte getCompressorStatus() {
        return compressorStatus;
    }

    public void setCompressorStatus(byte compressorStatus) {
        this.compressorStatus = compressorStatus;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public double getSetTemperature() {
        return setTemperature;
    }

    public void setSetTemperature(double setTemperature) {
        this.setTemperature = setTemperature;
    }

    public short getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(short lockStatus) {
        this.lockStatus = lockStatus;
    }

    public int getStatusUploadInterval() {
        return statusUploadInterval;
    }

    public void setStatusUploadInterval(int statusUploadInterval) {
        this.statusUploadInterval = statusUploadInterval;
    }

    public int getCompressorStartupDelay() {
        return compressorStartupDelay;
    }

    public void setCompressorStartupDelay(int compressorStartupDelay) {
        this.compressorStartupDelay = compressorStartupDelay;
    }

    public int getTemperatureDeviation() {
        return temperatureDeviation;
    }

    public void setTemperatureDeviation(int temperatureDeviation) {
        this.temperatureDeviation = temperatureDeviation;
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
