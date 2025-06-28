package pojo.data;

public class UnlockData extends Data {
    private short unlockStatus;

    @Override
    public byte[] toBytes() {
        byte[] data = new byte[2];
        data[0] = (byte) ((unlockStatus >> 8) & 0xFF);
        data[1] = (byte) (unlockStatus & 0xFF);
        return data;
    }

    public short getUnlockStatus() {
        return unlockStatus;
    }

    public void setUnlockStatus(short unlockStatus) {
        this.unlockStatus = unlockStatus;
    }
}
