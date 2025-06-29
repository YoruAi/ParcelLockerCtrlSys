package yoru.parcellockerctrlsysweb.utils;

import static yoru.parcellockerctrlsysweb.pojo.DeviceStatus.LOCK_STATUS_OFF;

public class LockStatusUtils {
    // 下标从0开始
    public static boolean isLocked(short lockStatus, int index) {
        int bit = 1 << ((index + 8) % 16);
        return (lockStatus & bit) == LOCK_STATUS_OFF;
    }
}
