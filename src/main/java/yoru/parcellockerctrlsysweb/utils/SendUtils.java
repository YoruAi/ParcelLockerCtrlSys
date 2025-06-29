package yoru.parcellockerctrlsysweb.utils;

import yoru.parcellockerctrlsysweb.pojo.Frame;
import yoru.parcellockerctrlsysweb.pojo.data.*;
import yoru.parcellockerctrlsysweb.service.PacketHandler;

import java.util.List;

public class SendUtils {
    // 帧号 //
    private static volatile int currentFrameNumber = 0;

    synchronized
    private static void updateFrameNumber() {
        currentFrameNumber++;
        if (currentFrameNumber == 256) {
            currentFrameNumber = 1;
        }
    }

    // synchronized
    public static void sendQueryCommand(String currentDevicePortName, int currentDeviceAddress,
                                        String portName, int deviceAddress) {
        updateFrameNumber();

        QueryData data = new QueryData();

        Frame frame = FrameUtils.buildSendFrame(
                currentFrameNumber,
                deviceAddress,
                Frame.QUERY_TYPE,
                data,
                portName
        );

        PacketHandler.sendSerialPacket(frame.getPortName(), frame);
    }

    // synchronized
    public static void sendSetTemperatureDeviationCommand(String currentDevicePortName, int currentDeviceAddress,
                                                          int temperatureDeviation) {
        updateFrameNumber();

        SetTemperatureDeviationData data = new SetTemperatureDeviationData();
        data.setTemperatureDeviation(temperatureDeviation);

        Frame frame = FrameUtils.buildSendFrame(
                currentFrameNumber,
                currentDeviceAddress,
                Frame.SET_TEMPERATURE_DEVIATION_TYPE,
                data,
                currentDevicePortName
        );

        PacketHandler.sendSerialPacket(frame.getPortName(), frame);
    }

    // synchronized
    public static void sendSetTemperatureCommand(String currentDevicePortName, int currentDeviceAddress,
                                                 double setTemperature) {
        updateFrameNumber();

        SetTemperatureData data = new SetTemperatureData();
        data.setSetTemperature(setTemperature);

        Frame frame = FrameUtils.buildSendFrame(
                currentFrameNumber,
                currentDeviceAddress,
                Frame.SET_TEMPERATURE_TYPE,
                data,
                currentDevicePortName
        );

        PacketHandler.sendSerialPacket(frame.getPortName(), frame);
    }

    // synchronized
    public static void sendSetParamCommand(String currentDevicePortName, int currentDeviceAddress,
                                           String deviceCode, int deviceAddress, double setTemperature,
                                           int compressorStartupDelay, int statusUploadInterval,
                                           int temperatureDeviation) {
        updateFrameNumber();

        SetParamData data = new SetParamData();
        data.setDeviceCode(deviceCode);
        data.setDeviceAddress(deviceAddress);
        data.setSetTemperature(setTemperature);
        data.setCompressorStartupDelay(compressorStartupDelay);
        data.setStatusUploadInterval(statusUploadInterval);
        data.setTemperatureDeviation(temperatureDeviation);

        Frame frame = FrameUtils.buildSendFrame(
                currentFrameNumber,
                0x7F,
                Frame.SET_PARAM_TYPE,
                data,
                currentDevicePortName
        );

        PacketHandler.sendSerialPacket(frame.getPortName(), frame);
    }

    // synchronized
    public static void sendUnlockCommand(String currentDevicePortName, int currentDeviceAddress,
                                         List<Integer> indexes) {
        short needLockStatus = 0;
        if (!indexes.isEmpty()) {
            for (int index : indexes) {
                needLockStatus = (short) (needLockStatus | (1 << ((index + 8) % 16)));
            }
        }

        updateFrameNumber();

        UnlockData data = new UnlockData();
        data.setUnlockStatus(needLockStatus);
        Frame frame = FrameUtils.buildSendFrame(
                currentFrameNumber,
                currentDeviceAddress,
                Frame.UNLOCK_TYPE,
                data,
                currentDevicePortName
        );

        PacketHandler.sendSerialPacket(frame.getPortName(), frame);
    }

    // synchronized
    public static void sendCompressorStartControlCommand(String currentDevicePortName, int currentDeviceAddress,
                                                         int deviceAddress, String portName) {
        updateFrameNumber();

        byte controlCommand = (byte) 1;

        CompressorControlData data = new CompressorControlData();
        data.setCompressorStatus(controlCommand);
        Frame frame = FrameUtils.buildSendFrame(
                currentFrameNumber,
                deviceAddress,
                Frame.COMPRESSOR_CONTROL_TYPE,
                data,
                portName
        );

        PacketHandler.sendSerialPacket(frame.getPortName(), frame);
    }

    // synchronized
    public static void sendCompressorStopControlCommand(String currentDevicePortName, int currentDeviceAddress,
                                                        int deviceAddress, String portName) {
        updateFrameNumber();

        byte controlCommand = (byte) 0;

        CompressorControlData data = new CompressorControlData();
        data.setCompressorStatus(controlCommand);
        Frame frame = FrameUtils.buildSendFrame(
                currentFrameNumber,
                deviceAddress,
                Frame.COMPRESSOR_CONTROL_TYPE,
                data,
                portName
        );

        PacketHandler.sendSerialPacket(frame.getPortName(), frame);
    }

    // synchronized
    public static void sendSetDeviceAddressCommand(String currentDevicePortName, int currentDeviceAddress,
                                                   String deviceCode, int deviceAddress) {
        updateFrameNumber();

        SetDeviceAddressData data = new SetDeviceAddressData();
        data.setDeviceCode(deviceCode);
        data.setDeviceAddress(deviceAddress);

        Frame frame = FrameUtils.buildSendFrame(
                currentFrameNumber,
                deviceAddress,
                Frame.SET_DEVICE_ADDRESS_TYPE,
                data,
                currentDevicePortName);
        PacketHandler.sendSerialPacket(frame.getPortName(), frame);
    }


}
