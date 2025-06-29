package yoru.parcellockerctrlsysweb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yoru.parcellockerctrlsysweb.mapper.DeviceStatusMapper;
import yoru.parcellockerctrlsysweb.pojo.DeviceStatus;

import java.util.List;

@Service
public class DeviceStatusService {
    @Autowired
    private DeviceStatusMapper deviceStatusDao;

    public void storeDeviceStatus(DeviceStatus deviceStatus) {
        deviceStatusDao.insertDeviceStatus(deviceStatus);
    }

    public DeviceStatus loadLatestDeviceStatus(String portName) {
        return deviceStatusDao.queryLatestDeviceStatus(portName);
    }

    public List<Double> loadHistorySetTemperature(String portName) {
        return deviceStatusDao.queryHistorySetTemperature(portName);
    }

    public List<Double> loadHistoryCurrentTemperature(String portName) {
        return deviceStatusDao.queryHistoryCurrentTemperature(portName);
    }
}
