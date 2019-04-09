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
}
