package yoru.parcellockerctrlsysweb.mapper;

import org.apache.ibatis.annotations.Mapper;
import yoru.parcellockerctrlsysweb.pojo.DeviceStatus;

import java.util.List;

@Mapper
public interface DeviceStatusMapper {
    void insertDeviceStatus(DeviceStatus deviceStatus);

    DeviceStatus queryLatestDeviceStatus(String portName);

    List<Double> queryHistorySetTemperature(String portName);

    List<Double> queryHistoryCurrentTemperature(String portName);
}
