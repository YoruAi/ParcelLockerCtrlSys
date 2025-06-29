package yoru.parcellockerctrlsysweb.pojo.data;

import lombok.Getter;
import lombok.Setter;
import yoru.parcellockerctrlsysweb.utils.HexUtils;

import java.util.Arrays;

@Getter
@Setter
public class UploadStatusData extends Data {
    private String deviceCode = "FFFFFFFFFF"; // 10位
    private int deviceAddress = 1;
    private int statusUploadInterval = 1;
    private int compressorStartupDelay = 30;
    private double setTemperature = 4;
    private int temperatureDeviation = 2;

    private byte systemStatus;
    private byte compressorStatus;
    private double currentTemperature;
    private short lockStatus;

    @Override
    public byte[] toBytes() {
        // 实际用不到
        byte[] data = new byte[34];
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

        System.arraycopy(HexUtils.hexToBytes(deviceCode), 0, data, 18, 5);
        data[23] = systemStatus;
        data[24] = 0;
        data[25] = compressorStatus;
        data[26] = HexUtils.temperatureToByte(setTemperature);
        data[27] = HexUtils.temperatureToByte(currentTemperature);
        data[28] = (byte) 0xFF;
        data[29] = (byte) 0xFF;
        data[30] = (byte) ((lockStatus >> 8) & 0xFF);
        data[31] = (byte) (lockStatus & 0xFF);
        data[32] = (byte) 0xFF;
        data[33] = (byte) 0xFF;

        return data;
    }


    public static UploadStatusData parseData(byte[] data) {
        if (data.length != 34) return null;

        UploadStatusData uploadStatusData = new UploadStatusData();
        uploadStatusData.deviceCode = HexUtils.bytesToHex(Arrays.copyOfRange(data, 0, 5));
        uploadStatusData.deviceAddress = data[5];
        uploadStatusData.statusUploadInterval = data[7];
        uploadStatusData.compressorStartupDelay = data[8];
        uploadStatusData.temperatureDeviation = data[12];

        uploadStatusData.systemStatus = data[23];
        uploadStatusData.compressorStatus = data[25];
        uploadStatusData.setTemperature = HexUtils.byteToTemperature(data[26]);
        uploadStatusData.currentTemperature = HexUtils.byteToTemperature(data[27]);
        uploadStatusData.lockStatus = (short) ((data[30] & 0xFF) << 8 | (data[31] & 0xFF));
        return uploadStatusData;
    }

}
