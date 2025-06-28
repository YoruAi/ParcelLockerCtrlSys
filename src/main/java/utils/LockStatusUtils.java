package utils;

import static pojo.DeviceStatus.LOCK_STATUS_OFF;
import static pojo.DeviceStatus.LOCK_STATUS_ON;

public class LockStatusUtils {
    // 下标从0开始
    public static boolean isLocked(short lockStatus, int index) {
        int bit = LOCK_STATUS_ON << ((index + 8) % 16);
        return (lockStatus & bit) == LOCK_STATUS_OFF;
    }
}
