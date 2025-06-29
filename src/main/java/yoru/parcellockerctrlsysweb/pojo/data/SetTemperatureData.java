package yoru.parcellockerctrlsysweb.pojo.data;

import lombok.Getter;
import lombok.Setter;
import yoru.parcellockerctrlsysweb.utils.HexUtils;

@Setter
@Getter
public class SetTemperatureData extends Data {
    private double setTemperature;

    @Override
    public byte[] toBytes() {
        byte[] data = new byte[1];
        data[0] = HexUtils.temperatureToByte(setTemperature);
        return data;
    }

}
