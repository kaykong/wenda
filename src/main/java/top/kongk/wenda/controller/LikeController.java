package top.kongk.wenda.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.kongk.wenda.async.EventModel;
import top.kongk.wenda.async.EventProducer;
import top.kongk.wenda.async.EventType;
import top.kongk.wenda.model.Comment;
import top.kongk.wenda.model.EntityType;
import top.kongk.wenda.model.HostHolder;
import top.kongk.wenda.service.CommentService;
import top.kongk.wenda.service.LikeService;
import top.kongk.wenda.util.JedisAdapter;
import top.kongk.wenda.util.RedisKeyUtil;
import top.kongk.wenda.util.WendaUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * @author kk
 */
@Controller
public class LikeController {

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;

    /**
     * 用户对回答点赞
     *
     * @param commentId
     * @return java.lang.String
     */
    @RequestMapping(path = {"/like"}, method = {RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId) {
        if (hostHolder.getCurrentUser() == null) {
            //提示用户登录
            return WendaUtil.getJSONString(999);
        }

        Comment comment = commentService.getCommentById(commentId);

        int likeStatus = likeService.getLikeStatus(hostHolder.getCurrentUser().getId(), EntityType.ENTITY_ANSWER, commentId);

        if (likeStatus > 0) {
            //如果已经喜欢, 再点击就是取消喜欢
            String likeKey = RedisKeyUtil.getLikeKey(EntityType.ENTITY_ANSWER, commentId);
            jedisAdapter.srem(likeKey, String.valueOf(hostHolder.getCurrentUser().getId()));
        } else {
            //喜欢回答

            //向事件生产者中发射事件
            eventProducer.fireEvent( new EventModel()
                    //事件的类型: 点赞
                    .setType(EventType.LIKE)
                    //事件的发起者: 当前用户
                    .setActorId(hostHolder.getCurrentUser().getId())
                    //事件作用的实体类
                    .setEntityId(commentId)
                    .setEntityType(EntityType.ENTITY_ANSWER)
                    //事件的影响者: 回答的作者
                    .setEntityOwnerId(comment.getUserId())
                    //额外补充的参数, 回答问题的id
                    .setExt("questionId", String.valueOf(comment.getEntityId())));

            likeService.like(hostHolder.getCurrentUser().getId(), EntityType.ENTITY_ANSWER, commentId);
        }

        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("likeStatus", likeService.getLikeStatus(hostHolder.getCurrentUser().getId(), EntityType.ENTITY_ANSWER, commentId));
        resultMap.put("likeCount", likeService.getLikeCount(EntityType.ENTITY_ANSWER, commentId));
        return WendaUtil.getJSONString(0, resultMap);
    }

    /**
     * 用户对回答点踩
     *
     * @param commentId
     * @return java.lang.String
     */
    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId) {
        if (hostHolder.getCurrentUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        int likeStatus = likeService.getLikeStatus(hostHolder.getCurrentUser().getId(), EntityType.ENTITY_ANSWER, commentId);

        if (likeStatus < 0) {
            //取消反对
            String disLikeKey = RedisKeyUtil.getDisLikeKey(EntityType.ENTITY_ANSWER, commentId);
            jedisAdapter.srem(disLikeKey, String.valueOf(hostHolder.getCurrentUser().getId()));
        } else {
            likeService.disLike(hostHolder.getCurrentUser().getId(), EntityType.ENTITY_ANSWER, commentId);
        }

        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("likeStatus", likeService.getLikeStatus(hostHolder.getCurrentUser().getId(), EntityType.ENTITY_ANSWER, commentId));
        resultMap.put("likeCount", likeService.getLikeCount(EntityType.ENTITY_ANSWER, commentId));
        return WendaUtil.getJSONString(0, resultMap);
    }
}
