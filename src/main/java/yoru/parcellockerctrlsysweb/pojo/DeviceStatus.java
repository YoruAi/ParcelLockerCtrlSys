package yoru.parcellockerctrlsysweb.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private byte systemStatus = 0;
    private byte compressorStatus = 0;
    private double currentTemperature = 0;
    private double setTemperature = 4;
    private short lockStatus = 0;
    private int statusUploadInterval = 1;
    private int compressorStartupDelay = 30;
    private int temperatureDeviation = 2;
    private LocalDateTime time = null;
    private String portName = null;
}
