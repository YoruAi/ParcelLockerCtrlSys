package yoru.parcellockerctrlsysweb.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    private int direction; // 方向（0收；1发）
    private int length;
    private int frameNumber;
    private int deviceAddress;
    private byte type;
    private byte[] data;
    private short checkCode;
    private LocalDateTime time;
    private String portName;
}
