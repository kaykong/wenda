package top.kongk.wenda.model;

import java.util.Date;

/**
 * 描述：
 *
 * @author kk
 */
public class Category extends BaseIdEntity {
    /**
     * 分类名称
     */
    private String name;
    /**
     * 父类id
     */
    private Integer parentId;

    /**
     * 父类名称
     */
    private String parentName;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 最后一次更新的人
     */
    private Integer updateUserId;

    /**
     * 更新时间
     */
    private Date updateTime;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Integer updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}
