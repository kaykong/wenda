package top.kongk.wenda.controller;


import org.apache.ibatis.jdbc.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import top.kongk.wenda.model.*;
import top.kongk.wenda.service.CommentService;
import top.kongk.wenda.service.FollowService;
import top.kongk.wenda.service.QuestionService;
import top.kongk.wenda.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * @author kk
 */
@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    FollowService followService;

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    private List<ViewObject> getQuestions(Integer userId, Integer offset, Integer limit) {
        List<Question> questionList = questionService.getLatestQuestions(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questionList) {
            ViewObject vo = new ViewObject();
            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            vo.set("question", question);
            vo.set("user", userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }

    private List<ViewObject> getQuestions(Integer userId, Integer offset, Integer limit, Integer categoryId) {
        if (categoryId == null || categoryId == 1) {
            return getQuestions(userId, offset, limit);
        }

        List<Question> questionList = questionService.getLatestQuestions(userId, offset, limit, categoryId);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questionList) {
            ViewObject vo = new ViewObject();
            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            vo.set("question", question);
            vo.set("user", userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model,
                        @RequestParam(value = "pop", defaultValue = "0") int pop,
                        @RequestParam(value = "categoryId", required = false) Integer categoryId) {

        model.addAttribute("vos", getQuestions(0, 0, 100, categoryId));
        return "index";
    }

    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model,
                            @PathVariable("userId") Integer userId,
                            @RequestParam(value = "categoryId", required = false) Integer categoryId) {

        model.addAttribute("vos", getQuestions(userId, 0, 100, categoryId));

        //获取人物简介
        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        //获取当前用户是否是此用户的粉丝
        if (hostHolder.getCurrentUser() != null) {
            vo.set("followed", followService.isFollower(hostHolder.getCurrentUser().getId(), EntityType.ENTITY_USER, userId));
        } else {
            vo.set("followed", false);
        }

        model.addAttribute("profileUser", vo);

        return "profile";
    }
}
