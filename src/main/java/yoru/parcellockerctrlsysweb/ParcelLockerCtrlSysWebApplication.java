package yoru.parcellockerctrlsysweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import yoru.parcellockerctrlsysweb.utils.SerialPortUtils;

@EnableScheduling
@ServletComponentScan
@SpringBootApplication
public class ParcelLockerCtrlSysWebApplication {

    public static void main(String[] args) {
        SerialPortUtils.searchForAvailablePortNames();
        SpringApplication.run(ParcelLockerCtrlSysWebApplication.class, args);
        SerialPortUtils.openSerialPorts();
    }

}
