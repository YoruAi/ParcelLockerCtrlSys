package yoru.parcellockerctrlsysweb.utils;

public class HexUtils {
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", (b & 0xFF)));
        }
        return hexString.toString();
    }

    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static byte temperatureToByte(double temp) {
        byte data = 0;
        if (temp < 0) {
            data = (byte) (data |
                    (((int) (-temp)) & 0xFF) << 1);
            data = (byte) (data | 0x80);
        } else {
            data = (byte) (data |
                    (((int) temp) & 0xFF) << 1);
        }
        if ((int) temp != temp) {
            data = (byte) (data | 0x01);
        }

        return data;
    }

    public static double byteToTemperature(byte b) {
        double temp = (b & 0x7E) >> 1;
        if ((b & 0x01) != 0) {
            temp += 0.5;
        }
        if ((b & 0x80) != 0) {
            temp = -temp;
        }

        return temp;
    }
}
