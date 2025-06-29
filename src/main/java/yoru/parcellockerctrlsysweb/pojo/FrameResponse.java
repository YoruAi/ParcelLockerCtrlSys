package yoru.parcellockerctrlsysweb.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrameResponse {
    private int direction;
    private int length;
    private int frameNumber;
    private int deviceAddress;
    private byte type;
    private String dataBase64;
    private short checkCode;
    private LocalDateTime time;
    private String portName;
}
