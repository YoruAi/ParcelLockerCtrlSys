package yoru.parcellockerctrlsysweb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yoru.parcellockerctrlsysweb.pojo.DeviceStatus;
import yoru.parcellockerctrlsysweb.pojo.Frame;
import yoru.parcellockerctrlsysweb.pojo.data.AckData;
import yoru.parcellockerctrlsysweb.pojo.data.UploadStatusData;
import yoru.parcellockerctrlsysweb.utils.PacketUtils;
import yoru.parcellockerctrlsysweb.utils.SerialPacket;
import yoru.parcellockerctrlsysweb.utils.SerialPortUtils;
import yoru.parcellockerctrlsysweb.utils.SpringContextUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PacketHandler {
    private static final int WAITING_TIME = 3000;

    // private static ParcelLockerCtrlSysFrame parent;
    private static final Map<Integer, CompletableFuture<Void>> ackQueue = new ConcurrentHashMap<>();
    private static final Map<String, CompletableFuture<Void>> queryQueue = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(PacketHandler.class);


    public static synchronized void recvSerialPacket(String portName, Frame frame) {
        FrameService frameService = SpringContextUtils.getBean(FrameService.class);
        DeviceStatusService deviceStatusService = SpringContextUtils.getBean(DeviceStatusService.class);

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
            deviceStatus = deviceStatusService.loadLatestDeviceStatus(portName);

            // ACK响应
            triggerAck(ackData.getFrameNumber());
        }

        deviceStatusService.storeDeviceStatus(deviceStatus);
        frameService.storeFrame(frame);

//        // 更新界面（设备状态更新）
//        parent.updateDeviceState(deviceStatus);
//        // 更新界面（帧更新）
//        parent.updateFrame(frame);
    }

    public static synchronized void sendSerialPacket(String portName, Frame frame) {
        FrameService frameService = SpringContextUtils.getBean(FrameService.class);

        SerialPacket serialPacket = PacketUtils.buildSerialPacket(frame);
        SerialPortUtils.writeToSerialPort(portName, serialPacket.getFrameData());

        frame.setLength(serialPacket.getLength());
        frame.setCheckCode(serialPacket.getCheckCode());

        // 命令帧等待ACK
        if (frame.getType() != Frame.QUERY_TYPE)
            waitForAck(frame.getFrameNumber(), portName);
        else
            waitForUploadStatus(portName);

        frameService.storeFrame(frame);

//        // 更新界面（帧更新）
//        parent.updateFrame(frame);
    }


    private static void triggerUploadStatus(String portName) {
        CompletableFuture<Void> future = queryQueue.get(portName);
        if (future != null && !future.isDone()) future.cancel(true);
        queryQueue.remove(portName);
    }

    private static void waitForUploadStatus(String portName) {
        queryQueue.put(portName, CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(WAITING_TIME);
                if (queryQueue.get(portName) != null) {
                    queryQueue.remove(portName);
                    log.error("Query失败, {}设备离线", portName);
//                    parent.notifyQueryError(portName);
                }
            } catch (InterruptedException ignored) {
            }
        }));
    }


    private static synchronized void triggerAck(int frameNumber) {
        CompletableFuture<Void> future = ackQueue.get(frameNumber);
        if (future != null && !future.isDone()) future.cancel(true);
        ackQueue.remove(frameNumber);
//        if (ackQueue.isEmpty()) parent.unsetACKWaiting();
    }

    private static synchronized void waitForAck(int frameNumber, String portName) {
//        parent.setACKWaiting();
        CompletableFuture<Void> ackFuture = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(WAITING_TIME);
                if (ackQueue.get(frameNumber) != null) {
                    ackQueue.remove(frameNumber);
                    log.error("{}未收到ACK, 帧号{}", portName, frameNumber);
//                    if (ackQueue.isEmpty()) parent.unsetACKWaiting();
//                    parent.notifyACKError(frameNumber, portName);
                }
            } catch (InterruptedException ignored) {
            }
        });
        ackQueue.put(frameNumber, ackFuture);
    }
}