package top.kongk.wenda.model;

import java.util.Date;

/**
 * 描述：
 *
 * @author kk
 */
public class DataDictionary extends BaseIdEntity {
    /**
     * 分类名称
     */
    private String type;

    /**
     * 父类名称
     */
    private String code;

    /**
     * 父类名称
     */
    private String value;

    /**
     * 状态
     */
    private Integer status;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
