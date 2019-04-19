package top.kongk.wenda.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kongk.wenda.model.EntityType;
import top.kongk.wenda.util.JedisAdapter;
import top.kongk.wenda.util.RedisKeyUtil;

/**
 * @author kk
 */
@Service
public class LikeService {

    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 根据 entityType 和 entityId 获取喜欢的数量
     *
     * @param entityType
     * @param entityId
     * @return long
     */
    public long getLikeCount(int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        return jedisAdapter.scard(likeKey);
    }

    /**
     * 根据 entityType 和 entityId 获取不喜欢的数量
     *
     * @param entityType
     * @param entityId
     * @return long
     */
    public long getDisikeCount(int entityType, int entityId) {
        String dislikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        return jedisAdapter.scard(dislikeKey);
    }

    /**
     * 判断 user 对某个 comment 是否喜欢
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return int 喜欢返回1, 不喜欢返回-1, 其他返回0
     */
    public int getLikeStatus(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        if (jedisAdapter.sismember(likeKey, String.valueOf(userId))) {
            return 1;
        }
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        return jedisAdapter.sismember(disLikeKey, String.valueOf(userId)) ? -1 : 0;
    }

    /**
     * 用户喜欢某个 comment
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return long 返回喜欢该 comment 的人数
     */
    public long like(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));

        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.srem(disLikeKey, String.valueOf(userId));

        return jedisAdapter.scard(likeKey);
    }

    /**
     * 用户不喜欢某个 comment
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return long 返回喜欢该 comment 的人数
     */
    public long disLike(int userId, int entityType, int entityId) {
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.sadd(disLikeKey, String.valueOf(userId));

        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.srem(likeKey, String.valueOf(userId));

        return jedisAdapter.scard(likeKey);
    }
}
