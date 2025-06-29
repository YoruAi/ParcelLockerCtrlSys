package yoru.parcellockerctrlsysweb.utils;

import yoru.parcellockerctrlsysweb.pojo.Frame;

import java.time.LocalDateTime;

public class PacketUtils {
    // 接收时将数据包转化为Frame对象
    public static Frame parseSerialPacket(SerialPacket serialPacket) {
        Frame frame = new Frame();
        frame.setDirection(0);
        frame.setLength(serialPacket.getLength());
        frame.setFrameNumber(serialPacket.getFrameNumber());
        frame.setDeviceAddress(serialPacket.getDeviceAddress());
        frame.setType(serialPacket.getType());
        frame.setData(serialPacket.getData());
        frame.setCheckCode(serialPacket.getCheckCode());
        frame.setTime(LocalDateTime.now());
        return frame;
    }

    // 发送时将Frame对象转化为数据包
    public static SerialPacket buildSerialPacket(Frame frame) {
        return new SerialPacket(
                frame.getFrameNumber(),
                frame.getDeviceAddress(),
                frame.getType(),
                frame.getData()
        );
    }
}
