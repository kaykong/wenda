package top.kongk.wenda.controller;


import org.apache.commons.lang3.math.NumberUtils;
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
import top.kongk.wenda.service.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


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

    @Autowired
    LikeService likeService;

    @Autowired
    DataDictionaryService dataDictionaryService;

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

    private List<ViewObject> getQuestions(Integer userId, Integer offset, Integer limit, Integer categoryId, String orderBy) {

        List<Question> questionList = Collections.EMPTY_LIST;

        //如果按热度排序, 需要重新给集合排序
        if ("hot".equals(orderBy)) {

            //不按默认时间排序不能分页
            questionList = questionService.getQuestions(userId, null, null, categoryId);

            //按照热度排序
            //按关注人数 + 回答数
            for (Question question : questionList) {
                //关注人数
                question.setFollowCount((int) followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
                //回答数
                question.getCommentCount();
                //回答的总赞数
            }

            Map<String, String> weightQuestion = dataDictionaryService.getByType("weight-question");

            Integer followCountWeight = null;
            Integer answerCountWeight = null;

            String followCountWeightString = weightQuestion.get("followCountWeight");
            if (NumberUtils.isNumber(followCountWeightString)) {
                followCountWeight = Integer.valueOf(followCountWeightString);
            }
            String answerCountWeightString = weightQuestion.get("answerCountWeight");
            if (NumberUtils.isNumber(answerCountWeightString)) {
                answerCountWeight = Integer.valueOf(answerCountWeightString);
            }

            if (followCountWeight == null) {
                followCountWeight = 1;
            }
            if (answerCountWeight == null) {
                answerCountWeight = 1;
            }
            Integer followCountWeightFinal = followCountWeight;
            Integer answerCountWeightFinal = answerCountWeight;

            questionList.sort(new Comparator<Question>() {
                private int getScore(Question question) {
                    return question.getFollowCount() * followCountWeightFinal + question.getCommentCount() * answerCountWeightFinal;
                }

                @Override
                public int compare(Question o1, Question o2) {
                    return getScore(o2) - getScore(o1);
                }
            });

        } else if ("time".equals(orderBy)) {
            questionList = questionService.getQuestions(userId, offset, limit, categoryId);
            for (Question question : questionList) {
                //关注人数
                question.setFollowCount((int) followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            }
        }

        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questionList) {
            ViewObject vo = new ViewObject();
            vo.set("followCount", question.getFollowCount());
            vo.set("question", question);
            vo.set("user", userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model,
                        @RequestParam(value = "pop", defaultValue = "0") int pop,
                        @RequestParam(value = "categoryId", required = false) Integer categoryId,
                        @RequestParam(value = "orderBy", defaultValue = "time") String orderBy) {
        //List<ViewObject> questions = getQuestions(0, 0, 100, categoryId);

        String requestURI = "";
        if (categoryId != null) {
            requestURI += "&categoryId=" + categoryId;
        }
        model.addAttribute("requestURI", requestURI);
        if ("time".equals(orderBy)) {
            model.addAttribute("orderBy", 1);
        } else if ("hot".equals(orderBy)) {
            model.addAttribute("orderBy", 2);
        }
        List<ViewObject> questions = getQuestions(null, 0, 100, categoryId, orderBy);
        model.addAttribute("vos", questions);
        return "index";
    }



    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model,
                            @PathVariable("userId") Integer userId,
                            @RequestParam(value = "categoryId", required = false) Integer categoryId) {

        model.addAttribute("vos", getQuestions(userId, 0, 1000, categoryId));

        //获取人物简介
        User user = userService.getUser(userId);
        if (user == null) {
            return "redirect:/";
        }
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        //getUserCommentCount 获取的就是回答数量
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        //获取当前用户是否是此用户的粉丝
        if (hostHolder.getCurrentUser() != null) {
            vo.set("followed", followService.isFollower(hostHolder.getCurrentUser().getId(), EntityType.ENTITY_USER, userId));
        } else {
            vo.set("followed", false);
        }
        //获取用户的赞同数
        //1.获取用户的所有回答,
        //2.获取用户的所有回答的赞同数
        List<Comment> commentList = commentService.getAnswersByUserId(user.getId());
        long likeCount = 0;
        for (Comment comment : commentList) {
            likeCount += likeService.getLikeCount(EntityType.ENTITY_ANSWER, comment.getId());
        }
        vo.set("likeCount", likeCount);

        model.addAttribute("profileUser", vo);

        return "profile";
    }
}
