package utils;

import pojo.Frame;
import pojo.data.Data;

import java.time.LocalDateTime;

public class FrameUtils {
    // 构建发送Frame对象
    public static Frame buildSendFrame(int frameNo, int deviceAddr, byte functionNo, Data data, String portName) {
        Frame frame = new Frame();
        frame.setDirection(Frame.DIRECTION_SEND);
//        frame.setLength(serialPacket.getLength());
        frame.setFrameNumber(frameNo);
        frame.setDeviceAddress(deviceAddr);
        frame.setType(functionNo);
        frame.setData(data.toBytes());
//        frame.setCheckCode(serialPacket.getCheckCode());
        frame.setTime(LocalDateTime.now());
        frame.setPortName(portName);
        return frame;
    }
}
