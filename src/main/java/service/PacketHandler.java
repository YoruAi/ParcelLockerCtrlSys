package service;

import pojo.DeviceStatus;
import pojo.Frame;
import pojo.data.AckData;
import pojo.data.UploadStatusData;
import utils.PacketUtils;
import utils.SerialPacket;
import utils.SerialPortUtils;
import view.ParcelLockerCtrlSysFrame;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.*;

public class PacketHandler {
    private static final int WAITING_TIME = 3000;

    private static ParcelLockerCtrlSysFrame parent;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private static final Map<Integer, ScheduledFuture<?>> ackQueue = new ConcurrentHashMap<>();
    private static final Map<String, ScheduledFuture<?>> queryQueue = new ConcurrentHashMap<>();


    public static synchronized void recvSerialPacket(String portName, Frame frame) {
        byte type = frame.getType();
        byte[] data = frame.getData();
        DeviceStatus deviceStatus;
        if (type == Frame.UPLOAD_STATUS_TYPE) {
            // 上传状态帧
            UploadStatusData uploadStatusData = UploadStatusData.parseData(data);
            if (uploadStatusData == null) return;
            deviceStatus = new DeviceStatus();

            deviceStatus.setDeviceCode(uploadStatusData.getDeviceCode());
            deviceStatus.setDeviceAddress(uploadStatusData.getDeviceAddress());
            deviceStatus.setSystemStatus(uploadStatusData.getSystemStatus());
            deviceStatus.setCompressorStatus(uploadStatusData.getCompressorStatus());
            deviceStatus.setCurrentTemperature(uploadStatusData.getCurrentTemperature());
            deviceStatus.setLockStatus(uploadStatusData.getLockStatus());
            deviceStatus.setStatusUploadInterval(uploadStatusData.getStatusUploadInterval());
            deviceStatus.setCompressorStartupDelay(uploadStatusData.getCompressorStartupDelay());
            deviceStatus.setSetTemperature(uploadStatusData.getSetTemperature());
            deviceStatus.setTemperatureDeviation(uploadStatusData.getTemperatureDeviation());
            deviceStatus.setTime(LocalDateTime.now());
            deviceStatus.setPortName(portName);

            // Query响应
            triggerUploadStatus(portName);
        } else {
            // ACK帧
            AckData ackData = AckData.parseData(data);
            if (ackData == null) return;
            deviceStatus = DeviceStatusService.loadLatestDeviceStatus(portName);

            // ACK响应
            triggerAck(ackData.getFrameNumber());
        }

        DeviceStatusService.storeDeviceStatus(deviceStatus);
        FrameService.storeFrame(frame);

        // 更新界面（设备状态更新）
        parent.updateDeviceState(deviceStatus);
        // 更新界面（帧更新）
        parent.updateFrame(frame);
    }

    public static synchronized void sendSerialPacket(String portName, Frame frame) {
        SerialPacket serialPacket = PacketUtils.buildSerialPacket(frame);
        SerialPortUtils.writeToSerialPort(portName, serialPacket.getFrameData());

        frame.setLength(serialPacket.getLength());
        frame.setCheckCode(serialPacket.getCheckCode());

        // 命令帧等待ACK
        if (frame.getType() != Frame.QUERY_TYPE)
            waitForAck(frame.getFrameNumber(), portName);
        else
            waitForUploadStatus(portName);

        FrameService.storeFrame(frame);

        // 更新界面（帧更新）
        parent.updateFrame(frame);
    }


    private synchronized static void waitForAck(int frameNumber, String portName) {
        parent.setACKWaiting();

        ScheduledFuture<?> future = scheduler.schedule(() -> {
            if (ackQueue.remove(frameNumber) != null) {
                if (ackQueue.isEmpty()) parent.unsetACKWaiting();
                parent.notifyACKError(frameNumber, portName);
            }
        }, WAITING_TIME, TimeUnit.MILLISECONDS);

        ackQueue.put(frameNumber, future);
    }

    private synchronized static void triggerAck(int frameNumber) {
        ScheduledFuture<?> future = ackQueue.get(frameNumber);
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }
        ackQueue.remove(frameNumber);
        if (ackQueue.isEmpty()) parent.unsetACKWaiting();
    }

    private static void waitForUploadStatus(String portName) {
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            if (queryQueue.remove(portName) != null) {
                parent.notifyQueryError(portName);
            }
        }, WAITING_TIME, TimeUnit.MILLISECONDS);

        queryQueue.put(portName, future);
    }

    private static void triggerUploadStatus(String portName) {
        ScheduledFuture<?> future = queryQueue.get(portName);
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }
        queryQueue.remove(portName);
    }


    public static void setParent(ParcelLockerCtrlSysFrame parent) {
        PacketHandler.parent = parent;
    }
}