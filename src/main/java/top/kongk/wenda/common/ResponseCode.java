package top.kongk.wenda.common;

/**
 * 描述：ServerResponse的status属性用到的状态码
 *
 * @author kk
 * @date 2018/9/23 21:35
 */
public enum ResponseCode {
    /**
     * 操作成功
     */
    SUCCESS(ResponseCode.SUCCESS_CODE, "SUCCESS"),
    /**
     * 操作失败
     */
    ERROR(1, "ERROR"),
    /**
     * 用户需要登录的状态码
     */
    NEED_LOGIN(10, "NEED_LOGIN"),
    /**
     * url路径中不合理的参数
     */
    ILLEGAL_ARGUMENT(2, "ILLEGAL_ARGUMENT"),
    /**
     * 传来的数据格式错误
     */
    FORMAT_ERROR(3, "FORMAT_ERROR"),
    /**
     * 服务器内部错误
     */
    SERVER_ERROR(500, "SERVER_ERROR");

    private final int code;
    private final String desc;

    public static final int SUCCESS_CODE = 0;
    public static final int ERROR_CODE = 1;
    public static final int NEED_LOGIN_CODE = 10;
    public static final int ILLEGAL_ARGUMENT_CODE = 2;
    public static final int FORMAT_ERROR_CODE = 3;
    public static final int SERVER_ERROR_CODE = 500;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public final int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
