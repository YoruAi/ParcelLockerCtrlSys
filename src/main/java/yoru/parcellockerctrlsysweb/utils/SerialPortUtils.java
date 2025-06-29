package yoru.parcellockerctrlsysweb.utils;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yoru.parcellockerctrlsysweb.pojo.Frame;
import yoru.parcellockerctrlsysweb.service.PacketHandler;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SerialPortUtils {
    private static final Map<String, SerialPort> device_serialPort = new TreeMap<>();
    private static final Logger log = LoggerFactory.getLogger(SerialPortUtils.class);

    public static List<String> getAvailablePortNames() {
        return device_serialPort.keySet().stream().toList();
    }

    public static void searchForAvailablePortNames() {
        String serialPortConfig = System.getProperty("config.serialport");
        if (serialPortConfig != null) {
            String[] portNames = serialPortConfig.split(",");
            for (String portName : portNames) {
                portName = portName.trim().toUpperCase();
                SerialPort serialPort = SerialPort.getCommPort(portName);
                if (serialPort == null) {
                    SerialPortUtils.log.error("串口{}未找到! 请检查串口名是否正确", portName);
                } else {
                    setupSerialPort(serialPort);
                    SerialPortUtils.log.info("发现串口{}", portName);
                }
            }
        } else {
            SerialPort[] serialPorts = SerialPort.getCommPorts();
            for (SerialPort serialPort : serialPorts) {
                final String portName = serialPort.getSystemPortName();
                if (Integer.parseInt(portName.substring(3)) > 120)
                    continue;
                setupSerialPort(serialPort);
            }
        }
    }

    public static void openSerialPorts() {
        for (Map.Entry<String, SerialPort> entry : device_serialPort.entrySet()) {
            String portName = entry.getKey();
            SerialPort serialPort = entry.getValue();
            if (serialPort.openPort()) {
                SerialPortUtils.log.info("串口{}已打开", portName);
            } else {
                SerialPortUtils.log.error("无法打开串口{}", portName);
            }
        }
    }

    private static void setupSerialPort(SerialPort serialPort) {
        final String portName = serialPort.getSystemPortName();
        serialPort.setBaudRate(38400);
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(1);
        serialPort.setParity(SerialPort.NO_PARITY);

        serialPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                System.out.println("set");
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;

                byte[] frameData = new byte[serialPort.bytesAvailable()];
                serialPort.readBytes(frameData, frameData.length);
                SerialPortUtils.readFromSerialPort(portName, frameData);
            }
        });

        device_serialPort.put(portName, serialPort);
    }

    public static void closeSerialPorts() {
        for (SerialPort serialPort : device_serialPort.values()) {
            serialPort.closePort();
        }
        device_serialPort.clear();
    }

    private static void readFromSerialPort(String portName, byte[] frameData) {
        // log
        SerialPortUtils.log.info("RecvFrom {}: {}", portName, HexUtils.bytesToHex(frameData));

        SerialPacket recvSerialPacket = new SerialPacket(frameData);
        if (recvSerialPacket.isValid()) {
            Frame frame = PacketUtils.parseSerialPacket(recvSerialPacket);
            frame.setPortName(portName);
            PacketHandler.recvSerialPacket(portName, frame);
        } else {
            SerialPortUtils.log.error("帧校验未通过!");
        }
    }

    public static void writeToSerialPort(String portName, byte[] frameData) {
        // log
        SerialPortUtils.log.info("SendTo {}: {}", portName, HexUtils.bytesToHex(frameData));

        SerialPort serialPort = device_serialPort.get(portName);
        if (serialPort == null) return;
        serialPort.writeBytes(frameData, frameData.length);
    }
}
