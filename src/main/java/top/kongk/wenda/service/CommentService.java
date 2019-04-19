package top.kongk.wenda.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kongk.wenda.dao.CommentDao;
import top.kongk.wenda.model.Comment;
import top.kongk.wenda.model.EntityType;

import java.util.List;


/**
 * @author kk
 */
@Service
public class CommentService {

    @Autowired
    private CommentDao commentDao;

    public List<Comment> getCommentsByEntity(int entityId, int entityType) {
        return commentDao.selectByEntity(entityId, entityType);
    }

    public int addComment(Comment comment) {
        return commentDao.addComment(comment);
    }

    public int getCommentCount(int entityId, int entityType) {
        return commentDao.getCommentCount(entityId, entityType);
    }

    public int getUserCommentCount(int userId) {
        return commentDao.getUserCommentCount(userId);
    }

    public void deleteComment(int entityId, int entityType) {
        commentDao.updateStatus(entityId, entityType, 1);
    }

    public Comment getCommentById(int commentId) {
        return commentDao.getCommentById(commentId);
    }

    public Comment getAnswerByqIdAnswerId(int questionId, int answerId) {
        return commentDao.getAnswerByqIdAnswerId(questionId, answerId);
    }

    /**
     * 获得回答下的评论
     *
     * @param answerId
     * @param entityType
     * @param count
     * @return java.lang.Integer
     */
    public Integer getAnswerReplyCount(int answerId, int entityType, int count) {
        //entityType = EntityType.ENTITY_ANSWER;
        //Integer replyCount = 0;
        //回答下的第一层评论
        List<Comment> comments = commentDao.selectByEntity(answerId, entityType);

        for (Comment comment : comments) {
            count++;
            Integer commentReplyCount = getCommentReplyCount(comment.getId(), EntityType.ENTITY_COMMENT, count);
            count = commentReplyCount;
        }

        return count;
    }

    public Integer getAnswerReplyCount(int answerId, int entityType) {

        return getAnswerReplyCount(answerId, entityType, 0);
    }

    /**
     * 评论的回复数
     *
     * @param entityId
     * @param entityType
     * @return java.lang.Integer
     */
    public Integer getCommentReplyCount(int entityId, int entityType) {

        return getCommentReplyCount(entityId, entityType, 0);
    }

    public Integer getCommentReplyCount(int entityId, int entityType, int count) {
        //entityType = EntityType.ENTITY_ANSWER;
        //Integer replyCount = 0;
        //回答下的第一层评论
        List<Comment> comments = commentDao.selectByEntity(entityId, entityType);

        for (Comment comment : comments) {
            count++;
            count = getCommentReplyCount(comment.getId(), EntityType.ENTITY_COMMENT, count);
        }

        return count;
    }

    public List<Comment> getAnswersByUserId(Integer id) {
        return getAnswersByUserIdEntityType(id, EntityType.ENTITY_QUESTION);
    }

    private List<Comment> getAnswersByUserIdEntityType(Integer id, int entityType) {
        return commentDao.getAnswersByUserId(id, entityType);
    }
}
