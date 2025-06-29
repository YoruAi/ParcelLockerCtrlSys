package yoru.parcellockerctrlsysweb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yoru.parcellockerctrlsysweb.pojo.Result;
import yoru.parcellockerctrlsysweb.service.DeviceStatusService;
import yoru.parcellockerctrlsysweb.utils.SerialPortUtils;

@Slf4j
@RestController
@RequestMapping("/device-status")
public class DeviceStatusController {
    @Autowired
    private DeviceStatusService deviceStatusService;

    @GetMapping("/all-ports")
    public Result getAllPorts() {
        return Result.success(SerialPortUtils.getAvailablePortNames());
    }

    @GetMapping("/{portName}")
    public Result getLatestDeviceStatus(@PathVariable String portName) {
        return Result.success(deviceStatusService.loadLatestDeviceStatus(portName));
    }

    @GetMapping("/history-set-temp/{portName}")
    public Result listHistorySetTemperature(@PathVariable String portName) {
        return Result.success(deviceStatusService.loadHistorySetTemperature(portName));
    }

    @GetMapping("/history-cur-temp/{portName}")
    public Result listHistoryCurrentTemperature(@PathVariable String portName) {
        return Result.success(deviceStatusService.loadHistoryCurrentTemperature(portName));
    }
}
