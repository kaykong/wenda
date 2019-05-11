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
     * 获取粉丝
     */
    private static String BIZ_FOLLOWER = "FOLLOWER";

    /**
     * 关注对象
     */
    private static String BIZ_FOLLOWEE = "FOLLOWEE";

    /**
     * 时间线
     */
    private static String BIZ_TIMELINE = "TIMELINE";

    /**
     * 邮箱注册
     */
    private static String BIZ_EMAIL_REGISTER = "EMAIL_REGISTER";


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

    /**
     * 获取事件
     *
     * @return java.lang.String
     */
    public static String getEventQueueKey() {
        return BIZ_EVENTQUEUE;
    }

    /**
     * 根据 entityType 和 entityId 获取某个实体的粉丝key
     *
     * @param entityType
     * @param entityId
     * @return java.lang.String
     */
    public static String getFollowerKey(int entityType, int entityId) {
        /*
         * entityType
         * 问题--1
         * 回答--2
         * 用户--3
         */
        return BIZ_FOLLOWER + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    //

    /**
     * 根据用户id 获取用户对某类实体的关注key
     * 例如获取用户关注的问题的key, 以此从redis中拿到该用户关注问题的列表
     *
     * @param userId
     * @param entityType
     * @return java.lang.String
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return BIZ_FOLLOWEE + SPLIT + String.valueOf(userId) + SPLIT + String.valueOf(entityType);
    }

    /**
     * 获取用户的时间线key
     *
     * @param userId
     * @return java.lang.String
     */
    public static String getTimelineKey(int userId) {
        return BIZ_TIMELINE + SPLIT + String.valueOf(userId);
    }

    /**
     * 获取email的注册 key
     *
     * @param email
     * @return java.lang.String
     */
    public static String getEmailRegisterKey(String email) {
        return BIZ_EMAIL_REGISTER + SPLIT + email;
    }

}
