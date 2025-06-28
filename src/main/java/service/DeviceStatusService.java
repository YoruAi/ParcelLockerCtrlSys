package service;

import dao.DeviceStatusDao;
import dao.JDBCDeviceStatusDao;
import pojo.DeviceStatus;

import java.util.List;

public class DeviceStatusService {
    private static final DeviceStatusDao deviceStatusDao = new JDBCDeviceStatusDao();

    public static void storeDeviceStatus(DeviceStatus deviceStatus) {
        deviceStatusDao.insertDeviceStatus(deviceStatus);
    }

    public static DeviceStatus loadLatestDeviceStatus(String portName) {
        return deviceStatusDao.queryLatestDeviceStatus(portName);
    }

    public static List<Double> loadHistorySetTemperature(String portName) {
        return deviceStatusDao.queryHistorySetTemperature(portName);
    }

    public static List<Double> loadHistoryCurrentTemperature(String portName) {
        return deviceStatusDao.queryHistoryCurrentTemperature(portName);
    }
}
