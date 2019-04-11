package top.kongk.wenda.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.kongk.wenda.async.EventModel;
import top.kongk.wenda.async.EventProducer;
import top.kongk.wenda.async.EventType;
import top.kongk.wenda.model.*;
import top.kongk.wenda.service.CommentService;
import top.kongk.wenda.service.FollowService;
import top.kongk.wenda.service.QuestionService;
import top.kongk.wenda.service.UserService;
import top.kongk.wenda.util.WendaUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author kk
 */
@Controller
public class FollowController {

    @Autowired
    FollowService followService;

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/followUser"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId) {

        User currentUser = hostHolder.getCurrentUser();
        if (currentUser == null) {
            return WendaUtil.getJSONString(999);
        }

        boolean ret = followService.follow(currentUser.getId(), EntityType.ENTITY_USER, userId);

        //处理关注事件
        eventProducer.fireEvent(
                //类型: 关注事件
                new EventModel(EventType.FOLLOW)
                //关注者的id
                .setActorId(currentUser.getId())
                //被关注人的id
                .setEntityId(userId)
                //实体类型: 用户
                .setEntityType(EntityType.ENTITY_USER)
                //实体的拥有者
                .setEntityOwnerId(userId));

        // 返回当前用户关注的人数
        long followeeCount = followService.getFolloweeCount(currentUser.getId(), EntityType.ENTITY_USER);
        return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followeeCount));
    }

    @RequestMapping(path = {"/unfollowUser"}, method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId) {
        User currentUser = hostHolder.getCurrentUser();

        if (currentUser == null) {
            //提示用户登录
            return WendaUtil.getJSONString(999);
        }

        boolean ret = followService.unfollow(currentUser.getId(), EntityType.ENTITY_USER, userId);

        eventProducer.fireEvent(
                //取消关注用户事件
                new EventModel(EventType.UNFOLLOW)
                .setActorId(currentUser.getId())
                .setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER)
                .setEntityOwnerId(userId));

        // 返回关注的人数
        long followeeCount = followService.getFolloweeCount(currentUser.getId(), EntityType.ENTITY_USER);
        return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followeeCount));
    }

    @RequestMapping(path = {"/followQuestion"}, method = {RequestMethod.POST})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId) {

        User currentUser = hostHolder.getCurrentUser();

        if (currentUser == null) {
            return WendaUtil.getJSONString(999);
        }

        Question q = questionService.getById(questionId);
        if (q == null) {
            return WendaUtil.getJSONString(1, "问题不存在");
        }

        boolean ret = followService.follow(currentUser.getId(), EntityType.ENTITY_QUESTION, questionId);

        //关注问题事件
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(currentUser.getId())
                .setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityOwnerId(q.getUserId()));

        Map<String, Object> info = new HashMap<>(4);
        //返回刚刚关注问题的用户信息
        info.put("headUrl", currentUser.getHeadUrl());
        info.put("name", currentUser.getName());
        info.put("id", currentUser.getId());
        //返回关注此问题的人数
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));

        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }

    @RequestMapping(path = {"/followQuestion2"}, method = {RequestMethod.GET})
    @ResponseBody
    public String followQuestion2(@RequestParam("questionId") int questionId) {

        User currentUser = hostHolder.getCurrentUser();

        if (currentUser == null) {
            return WendaUtil.getJSONString(999);
        }

        Question q = questionService.getById(questionId);
        if (q == null) {
            return WendaUtil.getJSONString(1, "问题不存在");
        }

        boolean ret = followService.follow(currentUser.getId(), EntityType.ENTITY_QUESTION, questionId);

        //关注问题事件
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(currentUser.getId())
                .setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityOwnerId(q.getUserId()));

        Map<String, Object> info = new HashMap<>(4);
        //返回刚刚关注问题的用户信息
        info.put("headUrl", currentUser.getHeadUrl());
        info.put("name", currentUser.getName());
        info.put("id", currentUser.getId());
        //返回关注此问题的人数
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));

        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }

    @RequestMapping(path = {"/unfollowQuestion"}, method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId) {
        User currentUser = hostHolder.getCurrentUser();
        if (currentUser == null) {
            return WendaUtil.getJSONString(999);
        }

        Question q = questionService.getById(questionId);
        if (q == null) {
            return WendaUtil.getJSONString(1, "问题不存在");
        }

        boolean ret = followService.unfollow(currentUser.getId(), EntityType.ENTITY_QUESTION, questionId);

        //取消关注问题事件
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(currentUser.getId())
                .setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityOwnerId(q.getUserId()));

        Map<String, Object> info = new HashMap<>(2);
        //返回取消关注的用户id
        info.put("id", currentUser.getId());
        //返回关注此问题的人数
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }

    /**
     * 用户 userId 的粉丝页
     *
     * @param model
     * @param userId
     * @return java.lang.String
     */
    @RequestMapping(path = {"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String followers(Model model, @PathVariable("uid") int userId) {

        //获取userId的粉丝
        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, userId, 0, 1000);
        if (hostHolder.getCurrentUser() != null) {
            model.addAttribute("followers", getUsersInfo(hostHolder.getCurrentUser().getId(), followerIds));
        } else {
            model.addAttribute("followers", getUsersInfo(0, followerIds));
        }

        //总粉丝数
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        //该用户信息
        model.addAttribute("curUser", userService.getUser(userId));

        return "followers";
    }

    /**
     * 用户userId 关注了人
     *
     * @param model
     * @param userId
     * @return java.lang.String
     */
    @RequestMapping(path = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followees(Model model, @PathVariable("uid") int userId) {

        //获取关注的人的信息
        List<Integer> followeeIds = followService.getFollowees(userId, EntityType.ENTITY_USER, 0, 1000);

        if (hostHolder.getCurrentUser() != null) {
            model.addAttribute("followees", getUsersInfo(hostHolder.getCurrentUser().getId(), followeeIds));
        } else {
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }

        //关注了多少人
        model.addAttribute("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        //该用户的信息
        model.addAttribute("curUser", userService.getUser(userId));

        return "followees";
    }

    /**
     * 遍历用户id集合, 找出每个用户的回答数, 粉丝数, 关注数 + 当前用户关注了每个用户
     *
     * @param localUserId 当前用户id
     * @param userIds 用户id集合
     * @return java.util.List<top.kongk.wenda.model.ViewObject>
     */
    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds) {

        List<ViewObject> userInfos = new ArrayList<>();

        for (Integer uid : userIds) {
            User user = userService.getUser(uid);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("commentCount", commentService.getUserCommentCount(uid));
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));
            vo.set("followeeCount", followService.getFolloweeCount(uid, EntityType.ENTITY_USER));
            if (localUserId != 0) {
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            } else {
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }
}
