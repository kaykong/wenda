package top.kongk.wenda.async;


/**
 * 异步事件枚举类型
 *
 * @author kk
 */
public enum EventType {
    /**
     * 点赞某个回答事件
     */
    LIKE(0),
    /**
     * 回答了问题事件
     */
    Answer(1),
    /**
     * 登录事件
     */
    LOGIN(2),
    /**
     * 邮箱
     */
    MAIL(3),
    /**
     * 关注
     */
    FOLLOW(4),
    /**
     * 取消关注
     */
    UNFOLLOW(5);

    private int value;

    EventType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
