package yoru.parcellockerctrlsysweb.pojo.data;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CompressorControlData extends Data {
    private byte compressorStatus;

    @Override
    public byte[] toBytes() {
        byte[] data = new byte[1];
        data[0] = compressorStatus;
        return data;
    }

}
