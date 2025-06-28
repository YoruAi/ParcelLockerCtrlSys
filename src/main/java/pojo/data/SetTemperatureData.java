package pojo.data;

import utils.HexUtils;

public class SetTemperatureData extends Data {
    private double setTemperature;

    @Override
    public byte[] toBytes() {
        byte[] data = new byte[1];
        data[0] = HexUtils.temperatureToByte(setTemperature);
        return data;
    }

    public double getSetTemperature() {
        return setTemperature;
    }

    public void setSetTemperature(double setTemperature) {
        this.setTemperature = setTemperature;
    }
}
