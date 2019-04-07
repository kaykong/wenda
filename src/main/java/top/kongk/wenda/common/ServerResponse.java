package top.kongk.wenda.common;



import java.io.Serializable;

/*
* 该注解的作用就是, 让ServerResponse<T> 在被序列化的时候,如果某个属性值为null,
* 那么就让这个属性在序列化的时候就被忽略了*/

/**
 * 描述：统一地把信息返回给前端
 *
 * @author kk
 * @date 2018/9/24 7:34
 */
public class ServerResponse<T> implements Serializable {

    /**
     * 枚举类ResponseCode中的状态码
     */
    private int status;
    /**
     * 返回的提示信息
     */
    private String msg;
    /**
     * 返回的数据
     */
    private T data;

    private ServerResponse(int status) {
        this.status = status;
    }

    /**
     * 如果传入的是 ServerResponse(int, String) 则调用此构造函数
     *
     * @param status 状态码
     * @param msg 提示信息
     */
    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    /**
     * 如果传入的是 ServerResponse(int, String除外的类型) 则调用此构造函数
     *
     * @param status 状态码
     * @param data 数据
     */
    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }


    public void setData(T data) {
        this.data = data;
    }

    /**
     * 描述: 判断状态码是否为ResponseCode.SUCCESS.getCode(),
     * 添加了JsonIgnore注解，在序列化的时候会被忽略掉
     *
     * @return boolean
     */
    public boolean isSuccess() {
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    /**
     * 描述: 判断状态码是否为ResponseCode.ERROR.getCode(),
     * 添加了JsonIgnore注解，在序列化的时候会被忽略掉
     * @return boolean
     */
    public boolean isError() {
        return this.status == ResponseCode.ERROR.getCode();
    }

    public static <T> ServerResponse<T> createSuccess() {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createSuccessWithMsg(String msg) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), msg);
    }

    public static <T> ServerResponse<T> createSuccess(T data) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), data);
    }

    public static <T> ServerResponse<T> createSuccess(String msg, T data) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> ServerResponse<T> createError() {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getDesc());
    }

    public static <T> ServerResponse<T> createError(ResponseCode responseCode) {
        return new ServerResponse<>(responseCode.getCode(), responseCode.getDesc());
    }

    public static <T> ServerResponse<T> createErrorWithMsg(String msg) {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), msg);
    }

    public static <T> ServerResponse<T> createError(T data) {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), data);
    }

    public static <T> ServerResponse<T> createError(String msg, T data) {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), msg, data);
    }

    public static <T> ServerResponse<T> createFormatError(String msg) {
        return new ServerResponse<>(ResponseCode.FORMAT_ERROR.getCode(), msg);
    }

    public static <T> ServerResponse<T> createNeedloginError(String msg) {
        return new ServerResponse<>(ResponseCode.NEED_LOGIN.getCode(), msg);
    }

    public static <T> ServerResponse<T> createIllegalArgumentError() {
        return new ServerResponse<>(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数错误");
    }

    public static <T> ServerResponse<T> createServerErrorWithMessage(String msg) {
        return new ServerResponse<>(ResponseCode.SERVER_ERROR_CODE, msg);
    }


}
