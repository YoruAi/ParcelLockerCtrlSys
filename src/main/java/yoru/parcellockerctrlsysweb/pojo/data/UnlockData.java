package yoru.parcellockerctrlsysweb.pojo.data;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UnlockData extends Data {
    private short unlockStatus;

    @Override
    public byte[] toBytes() {
        byte[] data = new byte[2];
        data[0] = (byte) ((unlockStatus >> 8) & 0xFF);
        data[1] = (byte) (unlockStatus & 0xFF);
        return data;
    }

}
