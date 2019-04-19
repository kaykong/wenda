package top.kongk.wenda.model;

import java.util.Date;


/**
 * 评论实体类
 *
 * @author kk
 */
public class Comment extends BaseIdEntity {

    /**
     * 评论作者
     */
    private Integer userId;
    /**
     * 评论对象的id
     */
    private Integer entityId;
    /**
     * 评论对象的类型
     */
    private Integer entityType;
    /**
     * 评论的具体内容
     */
    private String content;
    /**
     * 评论时间
     */
    private Date createdDate;
    /**
     * 评论的状态
     */
    private Integer status;
    /**
     * 评论的回复数
     */
    private Integer replyCount;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getEntityType() {
        return entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }
}
