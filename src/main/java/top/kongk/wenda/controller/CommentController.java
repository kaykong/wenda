package top.kongk.wenda.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;
import top.kongk.wenda.model.Comment;
import top.kongk.wenda.model.EntityType;
import top.kongk.wenda.model.HostHolder;
import top.kongk.wenda.service.CommentService;
import top.kongk.wenda.service.QuestionService;
import top.kongk.wenda.service.SensitiveService;
import top.kongk.wenda.service.UserService;

import java.util.Date;

/**
 * Created by nowcoder on 2016/7/2.
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
            content = HtmlUtils.htmlEscape(content);
            content = sensitiveService.filter(content);
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
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);

            //在数据库中添加问题的回答
            commentService.addComment(comment);

            // 更新题目里的评论数量
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(), count);
            // 怎么异步化
        } catch (Exception e) {
            logger.error("增加评论失败" + e.getMessage());
        }

        return "redirect:/question/" + String.valueOf(questionId);
    }
}
