package pojo.data;

public class SetTemperatureDeviationData extends Data {
    private int temperatureDeviation;

    @Override
    public byte[] toBytes() {
        byte[] data = new byte[1];
        data[0] = (byte) temperatureDeviation;
        return data;
    }

    public int getTemperatureDeviation() {
        return temperatureDeviation;
    }

    public void setTemperatureDeviation(int temperatureDeviation) {
        this.temperatureDeviation = temperatureDeviation;
    }
}
