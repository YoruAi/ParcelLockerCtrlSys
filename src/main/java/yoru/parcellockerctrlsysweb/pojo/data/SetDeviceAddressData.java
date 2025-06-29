package yoru.parcellockerctrlsysweb.pojo.data;

import lombok.Getter;
import lombok.Setter;
import yoru.parcellockerctrlsysweb.utils.HexUtils;

@Setter
@Getter
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

}
