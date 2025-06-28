package pojo.data;

public class AckData extends Data {
    private int deviceAddress;
    private int frameNumber;
    private short ackLockStatus;

    @Override
    public byte[] toBytes() {
        byte[] data = new byte[4];
        data[0] = (byte) deviceAddress;
        data[1] = (byte) frameNumber;
        data[2] = (byte) ((ackLockStatus >> 8) & 0xFF);
        data[3] = (byte) (ackLockStatus & 0xFF);
        return data;
    }

    public static AckData parseData(byte[] data) {
        if (data.length != 4) return null;
        AckData ackData = new AckData();
        ackData.deviceAddress = data[0] & 0xFF;
        ackData.frameNumber = data[1] & 0xFF;
        ackData.ackLockStatus = (short) ((data[2] & 0xFF) << 8 | (data[3] & 0xFF));
        return ackData;
    }

    public int getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(int deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }

    public short getAckLockStatus() {
        return ackLockStatus;
    }

    public void setAckLockStatus(short ackLockStatus) {
        this.ackLockStatus = ackLockStatus;
    }
}
