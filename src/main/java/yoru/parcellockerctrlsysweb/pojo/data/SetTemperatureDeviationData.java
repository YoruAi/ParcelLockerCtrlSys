package yoru.parcellockerctrlsysweb.pojo.data;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SetTemperatureDeviationData extends Data {
    private int temperatureDeviation;

    @Override
    public byte[] toBytes() {
        byte[] data = new byte[1];
        data[0] = (byte) temperatureDeviation;
        return data;
    }

}
