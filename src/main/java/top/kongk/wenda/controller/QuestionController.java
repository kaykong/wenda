package top.kongk.wenda.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.kongk.wenda.async.EventModel;
import top.kongk.wenda.async.EventProducer;
import top.kongk.wenda.async.EventType;
import top.kongk.wenda.common.ServerResponse;
import top.kongk.wenda.model.*;
import top.kongk.wenda.service.*;
import top.kongk.wenda.util.WendaUtil;

import java.util.*;

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

    @Autowired
    SearchService searchService;

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
    public String questionDetail(Model model, @PathVariable("qid") int qid,
                                 @RequestParam(value = "orderBy", defaultValue = "hot") String orderBy) {
        if ("hot".equals(orderBy)) {
            model.addAttribute("orderBy", 1);
        } else {
            model.addAttribute("orderBy", 2);
        }

        Question question = questionService.getById(qid);
        if (question.getStatus() == 1) {
            return "redirect:/";
        }
        model.addAttribute("question", question);
        //获取问题下的所有回答
        List<Comment> commentList = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);

        for (Comment comment : commentList) {
            comment.setReplyCount(commentService.getAnswerReplyCount(comment.getId(), EntityType.ENTITY_ANSWER));

            //获取回答的喜欢人数
            long likeCount = likeService.getLikeCount(EntityType.ENTITY_ANSWER, comment.getId());
            comment.setLikeCount((int) likeCount);
            //获取回答的不喜欢人数
            long dislikeCount = likeService.getDisikeCount(EntityType.ENTITY_ANSWER, comment.getId());
            comment.setDislikeCount((int) dislikeCount);
        }

        //按热度排序
        if ("hot".equals(orderBy)) {

            //获取权重, 可以通过数据库获取
            int likeCountWeight = 6;
            int dislikeCountWeight = 1;
            //回复数量的权重
            int replyCountWeight = 1;

            commentList.sort(new Comparator<Comment>() {
                private int getScore(Comment comment) {
                    //喜欢的数量*权重 - 不喜欢的数量*权重 + 回复的数量*权重
                    return comment.getLikeCount() * likeCountWeight
                            - comment.getDislikeCount() * dislikeCountWeight + comment.getReplyCount() * replyCountWeight;
                }

                @Override
                public int compare(Comment o1, Comment o2) {
                    return getScore(o2) - getScore(o1);
                }
            });
        }

        //封装回答
        List<ViewObject> vos = new ArrayList<>(commentList.size());
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
            vo.set("likeCount", comment.getLikeCount());
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


    @RequestMapping(value = "{qid}/answer", method = {RequestMethod.GET})
    public String questionAnswerDetail(Model model,
                                       @PathVariable("qid") int qid,
                                       @RequestParam("aid") int aid) {
        Question question = questionService.getById(qid);
        if (question.getStatus() == 1) {
            question.setTitle("该问题已经被删除");
            question.setContent("");
        }
        model.addAttribute("question", question);

        Comment comment = commentService.getAnswerByqIdAnswerId(qid, aid);


        if (comment != null) {
            //获取回答的信息
            ViewObject vo = new ViewObject();
            vo.set("answer", comment);
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

            //获取回答下的评论
            List<Comment> commentList = commentService.getCommentsByEntity(aid, EntityType.ENTITY_ANSWER);
            List<ViewObject> commentVos = new ArrayList<>(commentList.size());
            //每个评论的回复
            for (Comment comment1 : commentList) {
                comment1.setReplyCount(commentService.getCommentReplyCount(comment1.getId(), EntityType.ENTITY_COMMENT));
                ViewObject v1 = new ViewObject();
                v1.set("comment", comment1);
                v1.set("user", userService.getUser(comment1.getUserId()));
                commentVos.add(v1);
            }
            //添加进评论
            vo.set("commentVos", commentVos);

            model.addAttribute("vo", vo);
        }
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

        return "detailAnswer";
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

    @RequestMapping(value = "/add2", method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion2(@RequestParam("title") String title,
                              @RequestParam("content") String content,
                               @RequestParam("categoryId") Integer categoryId) {
        try {
            Question question = new Question();
            question.setContent(content);
            question.setCreatedDate(new Date());
            question.setTitle(title);
            //默认不匿名
            question.setAnonymous(false);
            question.setCategoryId(categoryId);
            //question.setStatus(1);
            if (hostHolder.getCurrentUser() == null) {
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
            log.error("增加问题失败" + e.getMessage());
        }
        return WendaUtil.getJSONString(1, "失败");
    }


    @RequestMapping(value = "/getCategoryList", method = {RequestMethod.GET})
    @ResponseBody
    public List<Category> getCategoryList(@RequestParam(value = "parentId", defaultValue = "1") String parentId) {
        try {
            List<Category> categories = questionService.getCategoryListByParentId(parentId);
            return categories;
        } catch (Exception e) {
            log.error("获取分类数据失败" + e.getMessage());
        }
        return Collections.EMPTY_LIST;
    }

    @RequestMapping(value = "/getCategoryByParentId", method = {RequestMethod.GET})
    @ResponseBody
    public List<Category> getCategoryByParentId(@RequestParam(value = "parentId", defaultValue = "1") String parentId) {
        try {
            List<Category> categories = questionService.getCategoryListByParentId(parentId);
            return categories;
        } catch (Exception e) {
            log.error("获取分类数据失败" + e.getMessage());
        }
        return Collections.EMPTY_LIST;
    }


    @RequestMapping(value = "/delete/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public String delete(@PathVariable("id") int id) {

        if (hostHolder.getCurrentUser() == null) {
            return WendaUtil.getJSONString(999, "请登录");
        }

        Question question = questionService.getById(id);
        if (question == null) {
            return WendaUtil.getJSONString(1, "该问题不存在");
        }

        /*
         * 检查用户是否有权限
         */
        User currentUser = hostHolder.getCurrentUser();
        if (!(question.getUserId().equals(currentUser.getId()) || currentUser.getManager())) {
            return WendaUtil.getJSONString(1, "您没有删除问题的权限");
        }


        boolean check = questionService.deleteById(id);

        if (check) {
            searchService.deleteById(String.valueOf(id));
            return WendaUtil.getJSONString(0, id + "成功");
        }

        return WendaUtil.getJSONString(1, "删除失败");
    }

}
