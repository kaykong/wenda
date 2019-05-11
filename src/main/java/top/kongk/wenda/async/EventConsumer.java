package top.kongk.wenda.async;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import top.kongk.wenda.util.JedisAdapter;
import top.kongk.wenda.util.RedisKeyUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 事件消费者, 统一的注册处理事件的方法
 *
 * @author kk
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    /**
     * 事件类型 --> 事件消费者们
     */
    private Map<EventType, List<EventHandler>> config = new HashMap<>();

    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        //拿到实现了 EventHandler 接口的类
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            //如果有事件消费者, 那么遍历他们
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                //获取每个消费者支持的事件类型
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();

                for (EventType type : eventTypes) {
                    if (!config.containsKey(type)) {
                        //在config中添加事件
                        config.put(type, new ArrayList<EventHandler>());
                    }
                    //在config中添加这些事件消费者
                    /*List<EventHandler> eventHandlers = config.get(type);
                    eventHandlers.add(entry.getValue());*/
                    config.get(type).add(entry.getValue());
                }
            }
        }

        //一直不停的处理 已存在的或即将到来的 事件
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String key = RedisKeyUtil.getEventQueueKey();
                    //取出redis中保存的事件, 注意传进去的0, 代表等待时间
                    List<String> events = jedisAdapter.brpop(0, key);

                    for (String message : events) {
                        //jedisAdapter.brpop(0, key) 返回的第一个元素是key, 第二个元素才是事件本身
                        if (message.equals(key)) {
                            continue;
                        }

                        //将事件反序列化
                        EventModel eventModel = JSON.parseObject(message, EventModel.class);

                        if (!config.containsKey(eventModel.getType())) {
                            logger.error("不能识别的事件");
                            continue;
                        }

                        //遍历能处理该事件的handler, 并执行处理事件
                        for (EventHandler handler : config.get(eventModel.getType())) {
                            handler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
