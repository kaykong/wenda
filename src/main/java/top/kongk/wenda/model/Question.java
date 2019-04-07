package top.kongk.wenda.model;

import java.util.Date;

/**
 * @author kkk
 */
public class Question extends BaseIdEntity {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 提问者id (不能为null)
     */
    private Integer userId;

    /**
     * 问题下的回答数量
     */
    private Integer commentCount;

    /**
     * 是否匿名
     */
    private Boolean anonymous;

    /**
     * 问题的状态, 0-关闭,1-待审核,2-仅浏览,3-正常可回答
     */
    private Integer status;

    /**
     * 问题分类id
     */
    private Integer categoryId;

    /**
     * 问题分类名称
     */
    private String categoryName;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
