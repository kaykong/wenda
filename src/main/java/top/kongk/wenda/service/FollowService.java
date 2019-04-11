package top.kongk.wenda.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import top.kongk.wenda.util.JedisAdapter;
import top.kongk.wenda.util.RedisKeyUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * @author kk
 */
@Service
public class FollowService {

    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 用户关注某个实体, 可以关注问题, 关注用户等任何实体
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return boolean
     */
    public boolean follow(int userId, int entityType, int entityId) {
        //获取实体的粉丝key
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        //获取用户关注某个实体的独一无二的key, 在此key中加入实体id
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();

        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        // 问题/用户的粉丝中增加 当前用户id
        tx.zadd(followerKey, date.getTime(), String.valueOf(userId));
        // 当前用户 对问题/用户 关注+1, (加上实体id)
        //例如, 用户1 关注 a问题, 那么 "1:问题" 的优先队列中添加问题id a
        tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }

    /**
     * 取消关注
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean unfollow(int userId, int entityType, int entityId) {
        //取出实体的粉丝key
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        //取出用户对某个实体类型的关注key
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        // 实体的粉丝中移除 当前用户
        tx.zrem(followerKey, String.valueOf(userId));
        // 当前用户对此类实体 关注-1, (减掉实体id)
        tx.zrem(followeeKey, String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx, jedis);

        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }

    /**
     * 获取某个实体的粉丝id列表
     *
     * zrevrange代表逆序获取, 因为保存时优先队列的分数是按照毫秒数来算的,
     * 那么如果按redis原来的从小到大的排序方式,
     * 先关注的在最上面, 这是不合适的。应该最后关注的在最上面
     *
     * @param entityType
     * @param entityId
     * @param count
     * @return java.util.List<java.lang.Integer>
     */
    public List<Integer> getFollowers(int entityType, int entityId, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey, 0, count));
    }

    /**
     * 分页获取实体的粉丝id列表
     *
     * @param entityType
     * @param entityId
     * @param offset
     * @param count
     * @return java.util.List<java.lang.Integer>
     */
    public List<Integer> getFollowers(int entityType, int entityId, int offset, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey, offset, offset + count));
    }

    /**
     * 获取用户关注的某类实体的 实体id
     *
     * @param userId
     * @param entityType
     * @param count
     * @return java.util.List<java.lang.Integer>
     */
    public List<Integer> getFollowees(int userId, int entityType, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, 0, count));
    }

    /**
     * 分页获取用户关注的某类实体的 实体id
     *
     * @param userId
     * @param entityType
     * @param count
     * @return java.util.List<java.lang.Integer>
     */
    public List<Integer> getFollowees(int userId, int entityType, int offset, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, offset, offset + count));
    }

    /**
     * 获取某个实体 (问题/用户) 的粉丝数量
     *
     * @param entityType
     * @param entityId
     * @return long
     */
    public long getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }

    /**
     * 获取用户对某类实体关注的数量,
     * 如用户a关注了多少问题? 关注了多少人?
     *
     * @param userId
     * @param entityType
     * @return long
     */
    public long getFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return jedisAdapter.zcard(followeeKey);
    }

    private List<Integer> getIdsFromSet(Set<String> idSet) {
        List<Integer> ids = new ArrayList<>();
        for (String str : idSet) {
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }

    /**
     * 判断用户是否关注了某个实体
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return boolean
     */
    public boolean isFollower(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        //在某个用户, 某个问题的粉丝队列中查找该用户的权重, 如果用户没关注此实体类, 那么返回null
        return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
    }
}
