package yoru.parcellockerctrlsysweb.pojo;

import lombok.Data;

import java.util.List;

@Data
public class CommandRequestDTO {
    private String currentPortName;
    private Integer currentDeviceAddress;
    private String portName;
    private Integer deviceAddress;
    private String deviceCode;
    private Double setTemperature;
    private Integer compressorStartupDelay;
    private Integer statusUploadInterval;
    private Integer temperatureDeviation;
    private List<Integer> indexes;
}
