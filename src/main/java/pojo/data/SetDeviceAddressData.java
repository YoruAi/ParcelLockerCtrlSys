package pojo.data;

import utils.HexUtils;

public class SetDeviceAddressData extends Data {
    private String deviceCode; // 10位
    private int deviceAddress;

    @Override
    public byte[] toBytes() {
        byte[] data = new byte[6];
        System.arraycopy(HexUtils.hexToBytes(deviceCode), 0, data, 0, 5);
        data[5] = (byte) deviceAddress;
        return data;
    }

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
}
