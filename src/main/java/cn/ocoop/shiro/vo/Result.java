package cn.ocoop.shiro.vo;

/**
 * Created by liolay on 15-6-23.
 */
public class Result {

    private String code, msg;
    private Object data;

    Result() {
    }

    Result(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    Result(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Result build(String code, String msg) {
        return new Result(code, msg);
    }

    public static Result build(String code, String msg, Object object) {
        return new Result(code, msg, object);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
