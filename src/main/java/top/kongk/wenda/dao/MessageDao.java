package top.kongk.wenda.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.kongk.wenda.model.Message;

import java.util.List;


/**
 * @author kk
 */
@Mapper
public interface MessageDao {

    /**
     * 插入消息
     *
     * @param message
     * @return int
     */
    int addMessage(Message message);

    /**
     * 根据conversationId分页获取两个用户之间的消息列表
     *
     * @param conversationId
     * @param offset
     * @param limit
     * @return java.util.List<top.kongk.wenda.model.Message>
     */
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset, @Param("limit") int limit);


    /**
     * 根据conversationId, userId, 获取消息列表中发送给userId的未读消息数量
     *
     * @param userId
     * @param conversationId
     * @return int
     */
    int getConvesationUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    /**
     * 分页获取与userId有关的用户之间最新消息列表
     *
     * @param userId
     * @param offset
     * @param limit
     * @return java.util.List<top.kongk.wenda.model.Message>
     */
    List<Message> getConversationList(@Param("userId") int userId,
                                      @Param("offset") int offset, @Param("limit") int limit);


    /**
     * 把 conversationId 中 toId 为当前用户id的 message 的修改为已读
     *
     * @param conversationId
     * @param userId
     * @return int
     */
    int updateMessageStatusByConversationId(@Param("conversationId") String conversationId,
                                            @Param("userId") int userId);
}
