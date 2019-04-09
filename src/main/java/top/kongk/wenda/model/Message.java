package top.kongk.wenda.model;

import java.util.Date;


/**
 * @author kk
 */
public class Message extends BaseIdEntity {

    /**
     * 哪个用户发的
     */
    private Integer fromId;
    /**
     * 发给哪个用户
     */
    private Integer toId;
    /**
     * 发的具体的内容
     */
    private String content;
    /**
     * 消息发送时间
     */
    private Date createdDate;
    /**
     * 是否已读
     */
    private Integer hasRead;
    /**
     * 用户与用户之间的消息id, 如
     *
     * 用户1与用户2 --> 1_2
     * 用户7与用户3 --> 3_7
     *
     * 小的用户id排在前面, 保证两个用户的消息id唯一性
     */
    private String conversationId;

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public Integer getToId() {
        return toId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getHasRead() {
        return hasRead;
    }

    public void setHasRead(Integer hasRead) {
        this.hasRead = hasRead;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationId() {
        if (fromId < toId) {
            return String.format("%d_%d", fromId, toId);
        }
        return String.format("%d_%d", toId, fromId);
    }
}
