package utils;

import java.util.Arrays;

/**
 * 实现对数据帧的封装、拆封、校验等操作
 * 帧结构：信息头2B + 帧长1B + 帧号1B + 设备地址1B + 功能号1B + 数据nB + 校验和2B + 结束标志2B
 * 最小帧长度：10字节(无数据部分)
 * 最大帧长度：512字节
 */
public class SerialPacket {
    // CRC16 查表
    private static final int[] CRC16_TABLE = {
            0x0, 0x1021, 0x2042, 0x3063, 0x4084, 0x50A5, 0x60C6, 0x70E7,
            0x8108, 0x9129, 0xA14A, 0xB16B, 0xC18C, 0xD1AD, 0xE1CE, 0xF1EF,
            0x1231, 0x210, 0x3273, 0x2252, 0x52B5, 0x4294, 0x72F7, 0x62D6,
            0x9339, 0x8318, 0xB37B, 0xA35A, 0xD3BD, 0xC39C, 0xF3FF, 0xE3DE,
            0x2462, 0x3443, 0x420, 0x1401, 0x64E6, 0x74C7, 0x44A4, 0x5485,
            0xA56A, 0xB54B, 0x8528, 0x9509, 0xE5EE, 0xF5CF, 0xC5AC, 0xD58D,
            0x3653, 0x2672, 0x1611, 0x630, 0x76D7, 0x66F6, 0x5695, 0x46B4,
            0xB75B, 0xA77A, 0x9719, 0x8738, 0xF7DF, 0xE7FE, 0xD79D, 0xC7BC,
            0x48C4, 0x58E5, 0x6886, 0x78A7, 0x840, 0x1861, 0x2802, 0x3823,
            0xC9CC, 0xD9ED, 0xE98E, 0xF9AF, 0x8948, 0x9969, 0xA90A, 0xB92B,
            0x5AF5, 0x4AD4, 0x7AB7, 0x6A96, 0x1A71, 0xA50, 0x3A33, 0x2A12,
            0xDBFD, 0xCBDC, 0xFBBF, 0xEB9E, 0x9B79, 0x8B58, 0xBB3B, 0xAB1A,
            0x6CA6, 0x7C87, 0x4CE4, 0x5CC5, 0x2C22, 0x3C03, 0xC60, 0x1C41,
            0xEDAE, 0xFD8F, 0xCDEC, 0xDDCD, 0xAD2A, 0xBD0B, 0x8D68, 0x9D49,
            0x7E97, 0x6EB6, 0x5ED5, 0x4EF4, 0x3E13, 0x2E32, 0x1E51, 0xE70,
            0xFF9F, 0xEFBE, 0xDFDD, 0xCFFC, 0xBF1B, 0xAF3A, 0x9F59, 0x8F78,
            0x9188, 0x81A9, 0xB1CA, 0xA1EB, 0xD10C, 0xC12D, 0xF14E, 0xE16F,
            0x1080, 0xA1, 0x30C2, 0x20E3, 0x5004, 0x4025, 0x7046, 0x6067,
            0x83B9, 0x9398, 0xA3FB, 0xB3DA, 0xC33D, 0xD31C, 0xE37F, 0xF35E,
            0x2B1, 0x1290, 0x22F3, 0x32D2, 0x4235, 0x5214, 0x6277, 0x7256,
            0xB5EA, 0xA5CB, 0x95A8, 0x8589, 0xF56E, 0xE54F, 0xD52C, 0xC50D,
            0x34E2, 0x24C3, 0x14A0, 0x481, 0x7466, 0x6447, 0x5424, 0x4405,
            0xA7DB, 0xB7FA, 0x8799, 0x97B8, 0xE75F, 0xF77E, 0xC71D, 0xD73C,
            0x26D3, 0x36F2, 0x691, 0x16B0, 0x6657, 0x7676, 0x4615, 0x5634,
            0xD94C, 0xC96D, 0xF90E, 0xE92F, 0x99C8, 0x89E9, 0xB98A, 0xA9AB,
            0x5844, 0x4865, 0x7806, 0x6827, 0x18C0, 0x8E1, 0x3882, 0x28A3,
            0xCB7D, 0xDB5C, 0xEB3F, 0xFB1E, 0x8BF9, 0x9BD8, 0xABBB, 0xBB9A,
            0x4A75, 0x5A54, 0x6A37, 0x7A16, 0xAF1, 0x1AD0, 0x2AB3, 0x3A92,
            0xFD2E, 0xED0F, 0xDD6C, 0xCD4D, 0xBDAA, 0xAD8B, 0x9DE8, 0x8DC9,
            0x7C26, 0x6C07, 0x5C64, 0x4C45, 0x3CA2, 0x2C83, 0x1CE0, 0xCC1,
            0xEF1F, 0xFF3E, 0xCF5D, 0xDF7C, 0xAF9B, 0xBFBA, 0x8FD9, 0x9FF8,
            0x6E17, 0x7E36, 0x4E55, 0x5E74, 0x2E93, 0x3EB2, 0xED1, 0x1EF0
    };

    private static final byte FIXED_LENGTH = 10; // 固定部分长度
    private static final int MAX_FRAME_LENGTH = 512; // 最大帧长度
    // 帧头帧尾
    private static final byte[] FRAME_HEADER = {(byte) 0xFF, (byte) 0xFF};
    private static final byte[] FRAME_END = {(byte) 0xFF, (byte) 0xF7};

    private final byte[] buffer = new byte[MAX_FRAME_LENGTH]; // 数据缓冲区
    private int dataLength; // 实际数据长度

    private SerialPacket() {
        dataLength = 0;
        Arrays.fill(buffer, (byte) 0);
    }

    /**
     * 从接收数据构造
     *
     * @param data 接收到的字节数组
     */
    public SerialPacket(byte[] data) {
        this();
        if (data != null && data.length >= FIXED_LENGTH && data.length <= MAX_FRAME_LENGTH) {
            int frameLength = data[2] & 0xFF;
            if (data.length >= frameLength) {
                System.arraycopy(data, 0, buffer, 0, frameLength);
                dataLength = frameLength - FIXED_LENGTH;
            }
        }
    }

    /**
     * 根据参数构造数据包
     *
     * @param frameNo    帧号
     * @param deviceAddr 设备地址
     * @param functionNo 功能号
     * @param data       数据部分 (NOT NULL)
     */
    public SerialPacket(int frameNo, int deviceAddr, byte functionNo, byte[] data) {
        this();

        dataLength += data.length;
        int frameLength = (dataLength + FIXED_LENGTH);

        // 填充头部固定部分
        System.arraycopy(FRAME_HEADER, 0, buffer, 0, 2); // 帧头
        buffer[2] = (byte) frameLength; // 帧长度
        buffer[3] = (byte) frameNo; // 帧号
        buffer[4] = (byte) deviceAddr; // 设备地址
        buffer[5] = functionNo; // 功能号

        // 填充数据部分
        System.arraycopy(data, 0, buffer, 6, data.length);

        // 计算并填充CRC校验
        byte[] crc = calculateCRC16(buffer, 2, frameLength - 6);
        System.arraycopy(crc, 0, buffer, frameLength - 4, 2);

        // 填充帧尾
        System.arraycopy(FRAME_END, 0, buffer, frameLength - 2, 2);
    }

    /**
     * 设置帧号（重发使用）
     */
    public void setFrameNumber(int frameNo) {
        if (frameNo < 0) {
            frameNo = 0;
        }
        buffer[3] = (byte) frameNo;
        // 重新计算CRC
        int frameLength = (dataLength + FIXED_LENGTH);
        byte[] crc = calculateCRC16(buffer, 2, frameLength - 6);
        System.arraycopy(crc, 0, buffer, frameLength - 4, 2);
    }

    /**
     * 获取帧长度
     */
    public int getLength() {
        return buffer[2] & 0xFF;
    }

    /**
     * 获取帧号
     */
    public int getFrameNumber() {
        return buffer[3] & 0xFF;
    }


    /**
     * 获取设备地址
     */
    public int getDeviceAddress() {
        return buffer[4];
    }

    /**
     * 获取功能号
     */
    public byte getType() {
        return buffer[5];
    }

    /**
     * 获取数据部分
     */
    public byte[] getData() {
        byte[] data = new byte[dataLength];
        System.arraycopy(buffer, 6, data, 0, dataLength);
        return data;
    }

    /**
     * 获取校验码
     */
    public short getCheckCode() {
        int frameLength = (dataLength + FIXED_LENGTH);
        byte[] crc = new byte[2];
        System.arraycopy(buffer, frameLength - 4, crc, 0, 2);
        return (short) (((crc[1] & 0xFF) << 8) | (crc[0] & 0xFF));
    }

    /**
     * 获取完整帧数据
     */
    public byte[] getFrameData() {
        int frameLength = (dataLength + FIXED_LENGTH);
        byte[] frame = new byte[frameLength];
        System.arraycopy(buffer, 0, frame, 0, frameLength);
        return frame;
    }

    /**
     * 检查帧是否合法
     */
    public boolean isValid() {
        if (!isValidMinLength()) return false;

        byte[] currentCRC = new byte[2];
        int frameLength = (dataLength + FIXED_LENGTH);
        System.arraycopy(buffer, frameLength - 4, currentCRC, 0, 2);
        byte[] calculatedCRC = calculateCRC16(buffer, 2, frameLength - 6);

        return isValidHeader() &&
                isValidFooter() &&
                Arrays.equals(currentCRC, calculatedCRC);
    }

    // 私有校验方法
    private boolean isValidMinLength() {
        return dataLength + FIXED_LENGTH >= 0;
    }

    private boolean isValidHeader() {
        return buffer[0] == FRAME_HEADER[0] &&
                buffer[1] == FRAME_HEADER[1];
    }

    private boolean isValidFooter() {
        int frameLength = dataLength + FIXED_LENGTH;
        return buffer[frameLength - 2] == FRAME_END[0] &&
                buffer[frameLength - 1] == FRAME_END[1];
    }

    /**
     * 计算CRC16校验码
     */
    private byte[] calculateCRC16(byte[] data, int offset, int length) {
        int crc = 0;
        int i = 0;

        while (length-- > 0) {
            byte temp = (byte) ((crc & 0xFFFF) >> 8);
            crc = (crc << 8) & 0xFFFF;
            crc = (CRC16_TABLE[(temp ^ data[offset + i]) & 0xFF] ^ crc) & 0xFFFF;
            i++;
        }

        byte[] crcByte = new byte[2];
        crcByte[1] = (byte) ((crc >> 8) & 0xFF);
        crcByte[0] = (byte) (crc & 0xFF);
        return crcByte;
    }

}