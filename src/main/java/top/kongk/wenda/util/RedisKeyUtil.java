package top.kongk.wenda.util;


/**
 * @author kk
 */
public class RedisKeyUtil {

    /**
     * 分隔符
     */
    private static String SPLIT = ":";
    /**
     * 喜欢 前缀
     */
    private static String BIZ_LIKE = "LIKE";
    /**
     * 不喜欢 前缀
     */
    private static String BIZ_DISLIKE = "DISLIKE";
    /**
     * 事件
     */
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";

    /**
     * 根据 entityType 和 entityId 获取喜欢的key, 用于统一redis的集合key值
     *
     * @param entityType
     * @param entityId
     * @return java.lang.String
     */
    public static String getLikeKey(int entityType, int entityId) {
        return BIZ_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 根据 entityType 和 entityId 获取不喜欢的key, 用于统一redis的集合key值
     *
     * @param entityType
     * @param entityId
     * @return java.lang.String
     */
    public static String getDisLikeKey(int entityType, int entityId) {
        return BIZ_DISLIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getEventQueueKey() {
        return BIZ_EVENTQUEUE;
    }

}
