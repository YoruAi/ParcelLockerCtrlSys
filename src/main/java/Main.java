import service.PacketHandler;
import utils.SerialPortUtils;
import view.ParcelLockerCtrlSysFrame;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        SerialPortUtils.searchForAvailablePortNames();
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                Font font = new Font("微软雅黑", Font.PLAIN, 12);
                UIManager.put("Button.font", font);
                UIManager.put("Label.font", font);
                UIManager.put("TextField.font", font);
                UIManager.put("Table.font", font);
                UIManager.put("TableHeader.font", font);
                UIManager.put("TitledBorder.font", font.deriveFont(Font.BOLD));
            } catch (Exception e) {
                System.err.println(LocalDateTime.now() + " - 初始化界面失败！");
            }

            ParcelLockerCtrlSysFrame frame = new ParcelLockerCtrlSysFrame();
            frame.setVisible(true);
            PacketHandler.setParent(frame);

            SerialPortUtils.openSerialPorts();
        });
    }
}
