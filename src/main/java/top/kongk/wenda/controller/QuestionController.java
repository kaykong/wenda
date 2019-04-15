package top.kongk.wenda.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.kongk.wenda.async.EventHandler;
import top.kongk.wenda.async.EventModel;
import top.kongk.wenda.async.EventProducer;
import top.kongk.wenda.async.EventType;
import top.kongk.wenda.common.QuestionCode;
import top.kongk.wenda.common.ResponseCode;
import top.kongk.wenda.common.ServerResponse;
import top.kongk.wenda.model.*;
import top.kongk.wenda.service.*;
import top.kongk.wenda.util.WendaUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 问题模块
 *
 * @author kongkk
 */
@Controller
@RequestMapping("/question")
public class QuestionController {

    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private QuestionService questionService;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowService followService;

    @Autowired
    EventProducer eventProducer;

    /**
     * 用户提交问题, 把问题插入数据库, 设置状态为待审核
     *
     * @author kongkk
     * @param question
     * @return top.kongk.wenda.common.ServerResponse
     */
    @RequestMapping(value = "/addQuestion", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addQuestion(@RequestBody Question question) {

        User user = hostHolder.getCurrentUser();
        if (user == null) {
            return ServerResponse.createNeedloginError("请先登录");
        }
        ServerResponse serverResponse;
        try {
            serverResponse = questionService.addQuestion(question, user);
        } catch (Exception e) {
            log.error("新增问题异常 {}", e.getMessage());
            return ServerResponse.createErrorWithMsg("新增问题失败");
        }

        return serverResponse;
    }

    @RequestMapping(value = "{qid}", method = {RequestMethod.GET})
    public String questionDetail(Model model, @PathVariable("qid") int qid) {
        Question question = questionService.getById(qid);
        model.addAttribute("question", question);
        List<Comment> commentList = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);
        List<ViewObject> vos = new ArrayList<>();
        for (Comment comment : commentList) {
            ViewObject vo = new ViewObject();
            vo.set("comment", comment);

            if (hostHolder.getCurrentUser() == null) {
                vo.set("liked", 0);
            } else {
                //获取用户是否喜欢该回答
                vo.set("liked",
                        likeService.getLikeStatus(hostHolder.getCurrentUser().getId(),
                                EntityType.ENTITY_ANSWER, comment.getId()));
            }

            //获取回答的喜欢人数
            vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_ANSWER, comment.getId()));
            vo.set("user", userService.getUser(comment.getUserId()));
            vos.add(vo);
        }
        model.addAttribute("comments", vos);

        // 获取关注此问题的用户信息
        List<ViewObject> followUsers = new ArrayList<>(20);
        List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION, qid, 20);
        for (Integer userId : users) {
            ViewObject vo = new ViewObject();
            User u = userService.getUser(userId);
            if (u == null) {
                continue;
            }
            vo.set("name", u.getName());
            vo.set("headUrl", u.getHeadUrl());
            vo.set("id", u.getId());
            followUsers.add(vo);
        }
        model.addAttribute("followUsers", followUsers);
        if (hostHolder.getCurrentUser() != null) {
            model.addAttribute("followed", followService.isFollower(hostHolder.getCurrentUser().getId(), EntityType.ENTITY_QUESTION, qid));
        } else {
            model.addAttribute("followed", false);
        }

        return "detail";
    }

    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content") String content) {
        try {
            Question question = new Question();
            question.setContent(content);
            question.setCreatedDate(new Date());
            question.setTitle(title);
            //默认不匿名
            question.setAnonymous(false);
            if (hostHolder.getCurrentUser() == null) {
                //question.setUserId(WendaUtil.ANONYMOUS_USERID);
                return WendaUtil.getJSONString(999);
            } else {
                question.setUserId(hostHolder.getCurrentUser().getId());
            }

            if (questionService.addQuestion(question, hostHolder.getCurrentUser()).isSuccess()) {
                eventProducer.fireEvent(new EventModel(EventType.ADD_QUESTION)
                        .setActorId(question.getUserId()).setEntityId(question.getId())
                        .setExt("title", question.getTitle()).setExt("content", question.getContent()));
                return WendaUtil.getJSONString(0);
            }
        } catch (Exception e) {
            log.error("增加题目失败" + e.getMessage());
        }
        return WendaUtil.getJSONString(1, "失败");
    }


}
