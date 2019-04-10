package top.kongk.wenda.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.kongk.wenda.model.Comment;
import top.kongk.wenda.model.EntityType;
import top.kongk.wenda.model.HostHolder;
import top.kongk.wenda.service.CommentService;
import top.kongk.wenda.service.LikeService;
import top.kongk.wenda.util.WendaUtil;


/**
 * @author kk
 */
@Controller
public class LikeController {
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    /*@Autowired
    EventProducer eventProducer;*/

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

        /*eventProducer.fireEvent(new EventModel(EventType.LIKE)
                .setActorId(hostHolder.getUser().getId()).setEntityId(commentId)
                .setEntityType(EntityType.ENTITY_COMMENT).setEntityOwnerId(comment.getUserId())
                .setExt("questionId", String.valueOf(comment.getEntityId())));*/

        long likeCount = likeService.like(hostHolder.getCurrentUser().getId(), EntityType.ENTITY_ANSWER, commentId);
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
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

        long likeCount = likeService.disLike(hostHolder.getCurrentUser().getId(), EntityType.ENTITY_ANSWER, commentId);
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }
}
