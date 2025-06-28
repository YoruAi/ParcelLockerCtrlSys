package pojo.data;

public class CompressorControlData extends Data {
    private byte compressorStatus;

    @Override
    public byte[] toBytes() {
        byte[] data = new byte[1];
        data[0] = compressorStatus;
        return data;
    }

    public byte getCompressorStatus() {
        return compressorStatus;
    }

    public void setCompressorStatus(byte compressorStatus) {
        this.compressorStatus = compressorStatus;
    }
}
