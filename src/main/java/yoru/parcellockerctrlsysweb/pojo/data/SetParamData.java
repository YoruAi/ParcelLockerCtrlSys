package yoru.parcellockerctrlsysweb.pojo.data;

import lombok.Getter;
import lombok.Setter;
import yoru.parcellockerctrlsysweb.utils.HexUtils;

import java.util.Arrays;

@Setter
@Getter
public class SetParamData extends Data {
    private String deviceCode = "FFFFFFFFFF"; // 10位
    private int deviceAddress = 1;
    private int statusUploadInterval = 1;
    private int compressorStartupDelay = 30;
    private double setTemperature = 4;
    private int temperatureDeviation = 2;

    @Override
    public byte[] toBytes() {
        byte[] data = new byte[18];
        Arrays.fill(data, (byte) 0);

        System.arraycopy(HexUtils.hexToBytes(deviceCode), 0, data, 0, 5);
        data[5] = (byte) deviceAddress;
        data[6] = 0;
        data[7] = (byte) statusUploadInterval;
        data[8] = (byte) compressorStartupDelay;
        data[9] = 0;
        data[10] = 0;
        data[11] = HexUtils.temperatureToByte(setTemperature);
        data[12] = (byte) temperatureDeviation;
        data[13] = (byte) 0xFF;
        data[14] = (byte) 0xFF;
        data[15] = (byte) 0xFF;
        data[16] = (byte) 0xFF;
        data[17] = (byte) 0;

        return data;
    }

}
