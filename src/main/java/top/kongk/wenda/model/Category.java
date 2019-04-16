package top.kongk.wenda.model;

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
