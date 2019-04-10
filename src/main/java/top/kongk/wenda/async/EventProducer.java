package top.kongk.wenda.async;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kongk.wenda.util.JedisAdapter;
import top.kongk.wenda.util.RedisKeyUtil;

/**
 * 生产者, 向生产者中添加要处理的事件
 * @author kk
 */
@Service
public class EventProducer {

    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 发送事件-->处理
     *
     * @param eventModel
     * @return boolean
     */
    public boolean fireEvent(EventModel eventModel) {
        try {
            //把事件序列化成 json串, 存入redis, 取出后再反序列化成 eventModel
            String json = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key, json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
