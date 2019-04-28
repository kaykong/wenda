package top.kongk.wenda.dao;

import org.apache.ibatis.annotations.*;
import top.kongk.wenda.model.Comment;

import java.util.List;


/**
 * @author kk
 */
@Mapper
public interface CommentDao {

    /**
     * 新增评论
     *
     * @param comment
     * @return int
     */
    int addComment(Comment comment);

    /**
     * 根据 entityId 和 entityType 更新评论的状态
     *
     * @param entityId
     * @param entityType
     * @param status
     * @return void
     */
    void updateStatus(@Param("entityId") int entityId, @Param("entityType") int entityType, @Param("status") int status);

    /**
     * 根据 id  更新评论的状态
     *
     * @param id
     * @param status
     * @return void
     */
    int updateStatusById(@Param("id") int id, @Param("status") int status);

    /**
     * 根据 entityId 和 entityType 选出评论列表
     *
     * @param entityId
     * @param entityType
     * @return java.util.List<top.kongk.wenda.model.Comment>
     */
    List<Comment> selectByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);

    /**
     * 根据 entityId 和 entityType 获取评论的数量
     *
     * @param entityId
     * @param entityType 
     * @return int
     */
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);


    /**
     * 根据id 获取评论
     *
     * @param id
     * @return top.kongk.wenda.model.Comment
     */
    Comment getCommentById(int id);

    /**
     * 获取用户回答的数量
     *
     * @param id
     * @return int
     */
    int getUserCommentCount(int id);

    /**
     * 获取回答
     *
     * @param questionId
     * @param answerId
     * @return top.kongk.wenda.model.Comment
     */
    Comment getAnswerByqIdAnswerId(@Param("questionId") int questionId,
                                   @Param("answerId") int answerId);


    /**
     * 获取用户在某一类型下的comment
     *
     * @param userId
     * @param entityType
     * @return java.util.List<top.kongk.wenda.model.Comment>
     */
    List<Comment> getAnswersByUserId(@Param("userId") Integer userId,
                                     @Param("entityType") Integer entityType);
}
