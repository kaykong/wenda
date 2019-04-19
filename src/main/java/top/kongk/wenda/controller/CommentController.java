package top.kongk.wenda.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;
import top.kongk.wenda.async.EventModel;
import top.kongk.wenda.async.EventProducer;
import top.kongk.wenda.async.EventType;
import top.kongk.wenda.model.*;
import top.kongk.wenda.service.CommentService;
import top.kongk.wenda.service.QuestionService;
import top.kongk.wenda.service.SensitiveService;
import top.kongk.wenda.service.UserService;
import top.kongk.wenda.util.WendaUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author kk
 */
@Controller
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @Autowired
    SensitiveService sensitiveService;

    @Autowired
    EventProducer eventProducer;

    /**
     * 给问题增加回答
     *
     * @param questionId 问题id
     * @param content    回答内容
     * @return java.lang.String
     */
    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content) {
        try {

            Question question = questionService.getById(questionId);

            if (question == null) {
                return "redirect:/";
            }

            content = HtmlUtils.htmlEscape(content);
            content = sensitiveService.filter(content);
            content = divideContent(content);
            // 过滤content
            Comment comment = new Comment();
            if (hostHolder.getCurrentUser() != null) {
                comment.setUserId(hostHolder.getCurrentUser().getId());
            } else {
                //如果没登陆, 就跳转到登录页面
                return "redirect:/reglogin?next=/question/" + String.valueOf(questionId);
            }
            comment.setContent(content);
            comment.setEntityId(questionId);
            //给问题做评论 --> 回答问题
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);

            //在数据库中添加问题的回答
            commentService.addComment(comment);

            // 更新题目里的评论数量
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(), count);

            //回答问题事件
            eventProducer.fireEvent(new EventModel(EventType.Answer)
                    .setActorId(hostHolder.getCurrentUser().getId())
                    .setEntityId(questionId)
                    .setEntityType(EntityType.ENTITY_QUESTION)
                    .setEntityOwnerId(question.getUserId()));


        } catch (Exception e) {
            logger.error("增加评论失败" + e.getMessage());
        }

        return "redirect:/question/" + String.valueOf(questionId);
    }

    private String divideContent(String content) {
        return content.replaceAll("\r\n", "<br/>");
    }


    /**
     * 获取评论详情
     *
     * @param id
     * @return java.lang.String
     */
    @RequestMapping(path = {"/comment/{id}"}, method = {RequestMethod.GET})
    public String getAnswer(Model model, @PathVariable("id") int id) {
        try {

            //评论
            Comment comment = commentService.getCommentById(id);

            if (comment == null) {
                return "redirect:/";
            }
            ViewObject commentVo = new ViewObject();
            commentVo.set("comment", comment);
            commentVo.set("user", userService.getUser(comment.getUserId()));

            //评论下的回复
            List<Comment> replies = commentService.getCommentsByEntity(comment.getId(), EntityType.ENTITY_COMMENT);
            List<ViewObject> repliesVo = new ArrayList<>(replies.size());
            for (Comment reply : replies) {
                //回复的回复数
                reply.setReplyCount(commentService.getCommentReplyCount(reply.getId(), EntityType.ENTITY_COMMENT));
                ViewObject replyVo = new ViewObject();
                replyVo.set("reply", reply);
                replyVo.set("user", userService.getUser(reply.getUserId()));
                repliesVo.add(replyVo);
            }

            model.addAttribute("commentVo", commentVo);
            model.addAttribute("repliesVo", repliesVo);

        } catch (Exception e) {
            logger.error("增加评论失败" + e.getMessage());
        }

        return "detailComment";
    }


    /**
     * 给回答添加评论
     *
     * @param answerId
     * @param content
     * @return java.lang.String
     */
    @RequestMapping(path = {"/addAnswerComment"}, method = {RequestMethod.POST})
    public String addAnswerComment(@RequestParam("answerId") int answerId,
                                  @RequestParam("content") String content) {
        Comment answer = commentService.getCommentById(answerId);

        try {
            if (answer == null) {
                return WendaUtil.getJSONString(999, "回答不存在的!");
            }

            content = HtmlUtils.htmlEscape(content);
            content = sensitiveService.filter(content);
            content = divideContent(content);
            // 过滤content
            Comment comment = new Comment();
            if (hostHolder.getCurrentUser() != null) {
                comment.setUserId(hostHolder.getCurrentUser().getId());
            } else {
                //如果没登陆, 就跳转到登录页面
                return "redirect:/reglogin?next=/comment/" + String.valueOf(answerId);
            }
            comment.setContent(content);
            comment.setEntityId(answerId);
            //给评论回复
            comment.setEntityType(EntityType.ENTITY_ANSWER);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);

            //在数据库中添加问题的回答
            commentService.addComment(comment);

            //评论回答事件
            /*eventProducer.fireEvent(new EventModel(EventType.Answer)
                    .setActorId(hostHolder.getCurrentUser().getId())
                    .setEntityId(questionId)
                    .setEntityType(EntityType.ENTITY_QUESTION)
                    .setEntityOwnerId(question.getUserId()));*/


        } catch (Exception e) {
            logger.error("评论回答失败" + e.getMessage());
        }

        return "redirect:/question/" + String.valueOf(answer.getEntityId()) + "/answer?aid=" + answerId;
    }


    /**
     * 给评论添加回复
     *
     * @param commentId
     * @param content
     * @return java.lang.String
     */
    @RequestMapping(path = {"/addCommentReply"}, method = {RequestMethod.POST})
    public String addCommentReply(@RequestParam("commentId") int commentId,
                             @RequestParam("content") String content) {
        try {

            //Question question = questionService.getById(questionId);
            Comment comment = commentService.getCommentById(commentId);

            if (comment == null) {
                return WendaUtil.getJSONString(999, "不存在的!");
            }

            content = HtmlUtils.htmlEscape(content);
            content = sensitiveService.filter(content);
            content = divideContent(content);

            Comment reply = new Comment();
            if (hostHolder.getCurrentUser() != null) {
                reply.setUserId(hostHolder.getCurrentUser().getId());
            } else {
                //如果没登陆, 就跳转到登录页面
                return "redirect:/reglogin?next=/comment/" + String.valueOf(commentId);
            }
            reply.setContent(content);
            reply.setEntityId(commentId);
            //给评论回复
            reply.setEntityType(EntityType.ENTITY_COMMENT);
            reply.setCreatedDate(new Date());
            reply.setStatus(0);

            //在数据库中添加问题的回答
            commentService.addComment(reply);

            //回复评论事件
            /*eventProducer.fireEvent(new EventModel(EventType.Answer)
                    .setActorId(hostHolder.getCurrentUser().getId())
                    .setEntityId(questionId)
                    .setEntityType(EntityType.ENTITY_QUESTION)
                    .setEntityOwnerId(question.getUserId()));*/


        } catch (Exception e) {
            logger.error("回复评论失败" + e.getMessage());
        }

        return "redirect:/comment/" + String.valueOf(commentId);
    }



}
