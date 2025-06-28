package dao;

import pojo.DeviceStatus;

import java.util.List;

public interface DeviceStatusDao {
    void insertDeviceStatus(DeviceStatus deviceStatus);

    DeviceStatus queryLatestDeviceStatus(String portName);

    List<Double> queryHistorySetTemperature(String portName);

    List<Double> queryHistoryCurrentTemperature(String portName);
}
