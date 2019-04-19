package top.kongk.wenda.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kongk.wenda.dao.MessageDao;
import top.kongk.wenda.model.Message;

import java.util.List;


/**
 * @author kk
 */
@Service
public class MessageService {
    @Autowired
    MessageDao messageDao;

    @Autowired
    SensitiveService sensitiveService;

    /**
     * 插入消息
     *
     * @param message
     * @return int
     */
    public int addMessage(Message message) {
        message.setContent(sensitiveService.filter(message.getContent()));
        return messageDao.addMessage(message);
    }

    /**
     * 消息详情页要用到
     * 根据conversationId分页获取两个用户之间的消息列表
     *
     * @param conversationId
     * @param offset
     * @param limit
     * @return java.util.List<top.kongk.wenda.model.Message>
     */
    public List<Message> getConversationDetail(String conversationId, int offset, int limit) {
        return messageDao.getConversationDetail(conversationId, offset, limit);
    }

    /**
     * 消息页要用到
     * 分页获取与userId有关的用户之间最新消息列表
     *
     * @param userId
     * @param offset
     * @param limit
     * @return java.util.List<top.kongk.wenda.model.Message>
     */
    public List<Message> getConversationList(int userId, int offset, int limit) {
        return messageDao.getConversationList(userId, offset, limit);
    }

    /**
     * 消息页要用到, 显示未读消息
     * 根据conversationId, userId, 获取消息列表中发送给userId的未读消息数量
     *
     * @param userId
     * @param conversationId
     * @return int
     */
    public int getConvesationUnreadCount(int userId, String conversationId) {
        return messageDao.getConvesationUnreadCount(userId, conversationId);
    }

    /**
     * 把 conversationId 中 toId 为当前用户id的 message 的修改为已读
     *
     * @param conversationId
     * @param userId
     * @return int
     */
    public int updateMessageStatusByConversationId(String conversationId, int userId) {
        return messageDao.updateMessageStatusByConversationId(conversationId, userId);
    }

    public int getUserUnreadCount(Integer id) {
        return messageDao.getUserUnreadCount(id);
    }

    public void addMessageList(List<Message> messageList) {
        messageDao.addMessageList(messageList);
    }


    public void deleteMessageByFromToIdContent(Integer fromId, Integer toId, String content) {
        messageDao.deleteMessageByFromToIdContent(fromId, toId, content);
    }
}
