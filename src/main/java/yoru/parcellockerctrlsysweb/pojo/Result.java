package yoru.parcellockerctrlsysweb.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private int code;
    private String msg;
    private Object data;

    public static Result success(Object data) {
        return new Result(1, "Success", data);
    }

    public static Result success() {
        return new Result(1, "Success", null);
    }

    public static Result error() {
        return new Result(0, "Fail", null);
    }

    public static Result error(String message) {
        return new Result(0, message, null);
    }
}
