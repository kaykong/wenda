package top.kongk.wenda.model;


/**
 * 描述：
 *
 * @author kk
 */
public class SensitiveWord extends BaseIdEntity {

    /**
     * 名称
     */
    private String name;

    /**
     * 分类名称
     */
    private String categoryName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
