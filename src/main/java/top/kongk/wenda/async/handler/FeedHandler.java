package top.kongk.wenda.async.handler;

import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.kongk.wenda.async.EventHandler;
import top.kongk.wenda.async.EventModel;
import top.kongk.wenda.async.EventType;
import top.kongk.wenda.model.EntityType;
import top.kongk.wenda.model.Feed;
import top.kongk.wenda.model.Question;
import top.kongk.wenda.model.User;
import top.kongk.wenda.service.FeedService;
import top.kongk.wenda.service.FollowService;
import top.kongk.wenda.service.QuestionService;
import top.kongk.wenda.service.UserService;
import top.kongk.wenda.util.JedisAdapter;

import java.util.*;


/**
 * @author kk
 */
@Component
public class FeedHandler implements EventHandler {

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    FeedService feedService;

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    QuestionService questionService;


    private String buildFeedData2(EventModel model) {

        Map<String, String> map = new HashMap<>(6);
        // 谁发起的事件
        User actor = userService.getUser(model.getActorId());
        if (actor == null) {
            return null;
        }
        map.put("userId", String.valueOf(actor.getId()));
        map.put("userHead", actor.getHeadUrl());
        map.put("userName", actor.getName());

        Question question = null;
        if (model.getEntityType() == EntityType.ENTITY_QUESTION) {
            question = questionService.getById(model.getEntityId());
            if (question == null) {
                return null;
            }
        }

        User user = null;
        if (model.getEntityType() == EntityType.ENTITY_USER) {
            //获取事件关联到的用户
            user = userService.getUser(model.getEntityId());
            if (user == null) {
                return null;
            }
        }

        //回答问题事件
        if (model.getType() == EventType.Answer) {
            //回答问题事件: 您关注的xx回答了qq问题
            //回答问题事件: 您关注的xx回答了qq问题, 回答链接
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            map.put("answerId", model.getExt("answerId"));
            return JSONObject.toJSONString(map);
        }

        //关注事件
        else if (model.getType() == EventType.FOLLOW) {
            //关注问题
            if (model.getEntityType() == EntityType.ENTITY_QUESTION) {
                //关注问题
                map.put("questionId", String.valueOf(question.getId()));
                map.put("questionTitle", question.getTitle());
                return JSONObject.toJSONString(map);
            }

            //关注用户
            /*if (model.getEntityType() == EntityType.ENTITY_USER) {

                map.put("followerId", String.valueOf(user.getId()));
                map.put("followerName", user.getName());
                map.put("followerHead", user.getHeadUrl());
                return JSONObject.toJSONString(map);

            } else */
        } else if (model.getType() == EventType.LIKE && model.getEntityType() == EntityType.ENTITY_ANSWER) {
            //点赞了哪个回答
            map.put("answerId", String.valueOf(model.getEntityId()));
            map.put("questionId", model.getExt("questionId"));
            return JSONObject.toJSONString(map);
        }

        return null;
    }


    private String buildFeedData(EventModel model) {
        Map<String, String> map = new HashMap<>();
        // 触发用户是通用的
        User actor = userService.getUser(model.getActorId());
        if (actor == null) {
            return null;
        }
        map.put("userId", String.valueOf(actor.getId()));
        map.put("userHead", actor.getHeadUrl());
        map.put("userName", actor.getName());

        if (model.getType() == EventType.Answer ||
                (model.getType() == EventType.FOLLOW  && model.getEntityType() == EntityType.ENTITY_QUESTION)) {
            Question question = questionService.getById(model.getEntityId());
            if (question == null) {
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }

    @Override
    public void doHandle(EventModel model) {

        // 构造一个新鲜事
        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        //哪类事件 EventType
        feed.setType(model.getType().getValue());
        //谁做的事
        feed.setUserId(model.getActorId());
        //把事件具体内容格式化成json
        feed.setData(buildFeedData2(model));

        if (feed.getData() == null) {
            // 不支持的feed事件
            return;
        }

        //从数据库中删除一样的feed
        feedService.deleteFeedByUserIdDataType(feed.getUserId(), feed.getData(), feed.getType());

        feedService.addFeed(feed);

        /*// 获得所有粉丝
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, model.getActorId(), Integer.MAX_VALUE);
        // 系统队列
        followers.add(0);
        // 给所有粉丝推事件
        for (int follower : followers) {
            String timelineKey = RedisKeyUtil.getTimelineKey(follower);
            jedisAdapter.lpush(timelineKey, String.valueOf(feed.getId()));
            // 限制最长长度，如果timelineKey的长度过大，就删除后面的新鲜事
        }*/
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.Answer, EventType.FOLLOW, EventType.LIKE);
    }
}
