package yoru.parcellockerctrlsysweb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yoru.parcellockerctrlsysweb.pojo.CommandRequestDTO;
import yoru.parcellockerctrlsysweb.pojo.DeviceStatus;
import yoru.parcellockerctrlsysweb.pojo.Result;
import yoru.parcellockerctrlsysweb.service.DeviceStatusService;
import yoru.parcellockerctrlsysweb.utils.SendUtils;

@Slf4j
@RestController
@RequestMapping("/command")
public class CommandController {

    @Autowired
    DeviceStatusService deviceStatusService;

    @PostMapping("/send-set-param")
    public Result sendSetParamCommand(@RequestBody CommandRequestDTO request) {
        log.info("Send SetParamCommand to {}.", request.getCurrentPortName());

        SendUtils.sendSetParamCommand(
                request.getCurrentPortName(),
                request.getCurrentDeviceAddress(),
                request.getDeviceCode(),
                request.getDeviceAddress(),
                request.getSetTemperature(),
                request.getCompressorStartupDelay(),
                request.getStatusUploadInterval(),
                request.getTemperatureDeviation()
        );

        return Result.success();
    }

    @PostMapping("/send-query")
    public Result sendQueryCommand(@RequestBody CommandRequestDTO request) {
        log.info("Send QueryCommand to {}.", request.getPortName());

        SendUtils.sendQueryCommand(
                request.getCurrentPortName(),
                request.getCurrentDeviceAddress(),
                request.getPortName(),
                request.getDeviceAddress()
        );

        return Result.success();
    }

    @PostMapping("/send-set-temp-deviation")
    public Result sendSetTemperatureDeviationCommand(@RequestBody CommandRequestDTO request) {
        log.info("Send SetTemperatureDeviationCommand to {}.", request.getCurrentPortName());

        SendUtils.sendSetTemperatureDeviationCommand(
                request.getCurrentPortName(),
                request.getCurrentDeviceAddress(),
                request.getTemperatureDeviation()
        );

        return Result.success();
    }

    @PostMapping("/send-set-temp")
    public Result sendSetTemperatureCommand(@RequestBody CommandRequestDTO request) {
        log.info("Send SetTemperatureCommand to {}.", request.getCurrentPortName());

        SendUtils.sendSetTemperatureCommand(
                request.getCurrentPortName(),
                request.getCurrentDeviceAddress(),
                request.getSetTemperature()
        );

        return Result.success();
    }

    @PostMapping("/send-unlock")
    public Result sendUnlockCommand(@RequestBody CommandRequestDTO request) {
        log.info("Send UnlockCommand to {}.", request.getCurrentPortName());

        SendUtils.sendUnlockCommand(
                request.getCurrentPortName(),
                request.getCurrentDeviceAddress(),
                request.getIndexes()
        );

        return Result.success();
    }

    @PostMapping("/send-compressor-start")
    public Result sendCompressorStartControlCommand(@RequestBody CommandRequestDTO request) {
        log.info("Send CompressorStartControlCommand to {}.", request.getPortName());

        SendUtils.sendCompressorStartControlCommand(
                request.getCurrentPortName(),
                request.getCurrentDeviceAddress(),
                request.getDeviceAddress(),
                request.getPortName()
        );

        return Result.success();
    }

    @PostMapping("/send-compressor-stop")
    public Result sendCompressorStopControlCommand(@RequestBody CommandRequestDTO request) {
        log.info("Send CompressorStopControlCommand to {}.", request.getPortName());

        SendUtils.sendCompressorStopControlCommand(
                request.getCurrentPortName(),
                request.getCurrentDeviceAddress(),
                request.getDeviceAddress(),
                request.getPortName()
        );

        return Result.success();
    }

    @PostMapping("/send-set-device-address")
    public Result sendSetDeviceAddressCommand(@RequestBody CommandRequestDTO request) {
        log.info("Send SetDeviceAddressCommand to {}.", request.getCurrentPortName());

        SendUtils.sendSetDeviceAddressCommand(
                request.getCurrentPortName(),
                request.getCurrentDeviceAddress(),
                request.getDeviceCode(),
                request.getDeviceAddress()
        );

        return Result.success();
    }

    @PostMapping("/send-compressor-startup-delay")
    public Result sendSetCompressorStartupDelay(@RequestBody CommandRequestDTO request) {
        log.info("Send SetCompressorStartupDelayCommand to {}.", request.getCurrentPortName());

        DeviceStatus deviceStatus = deviceStatusService.loadLatestDeviceStatus(request.getCurrentPortName());

        SendUtils.sendSetParamCommand(
                request.getCurrentPortName(),
                request.getCurrentDeviceAddress(),
                deviceStatus.getDeviceCode(),
                deviceStatus.getDeviceAddress(),
                deviceStatus.getSetTemperature(),
                request.getCompressorStartupDelay(),
                deviceStatus.getStatusUploadInterval(),
                deviceStatus.getTemperatureDeviation()
        );

        return Result.success();
    }

    @PostMapping("/send-status-upload-interval")
    public Result sendSetStatusUploadInterval(@RequestBody CommandRequestDTO request) {
        log.info("Send SetStatusUploadIntervalCommand to {}.", request.getCurrentPortName());

        DeviceStatus deviceStatus = deviceStatusService.loadLatestDeviceStatus(request.getCurrentPortName());

        SendUtils.sendSetParamCommand(
                request.getCurrentPortName(),
                request.getCurrentDeviceAddress(),
                deviceStatus.getDeviceCode(),
                deviceStatus.getDeviceAddress(),
                deviceStatus.getSetTemperature(),
                deviceStatus.getCompressorStartupDelay(),
                request.getStatusUploadInterval(),
                deviceStatus.getTemperatureDeviation()
        );

        return Result.success();
    }

}
