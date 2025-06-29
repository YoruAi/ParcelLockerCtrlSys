package yoru.parcellockerctrlsysweb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import yoru.parcellockerctrlsysweb.pojo.DeviceStatus;
import yoru.parcellockerctrlsysweb.utils.SendUtils;
import yoru.parcellockerctrlsysweb.utils.SerialPortUtils;

import java.util.Objects;

@Slf4j
@Component
public class ScheduledTasks {
    public static final int QUERY_INTERVAL = 5000;

    @Autowired
    DeviceStatusService deviceStatusService;

    // 每5秒执行一次
    @Scheduled(fixedRate = QUERY_INTERVAL)
    public void scheduledQuery() {
        log.info("统一发送查询帧");
        for (String portName : SerialPortUtils.getAvailablePortNames()) {
            DeviceStatus deviceStatus = Objects.requireNonNullElse(
                    deviceStatusService.loadLatestDeviceStatus(portName), new DeviceStatus());
            int deviceAddress = deviceStatus.getDeviceAddress();
            SendUtils.sendQueryCommand(portName, deviceAddress,
                    portName,
                    deviceAddress);
        }
    }
}
