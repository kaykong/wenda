package top.kongk.wenda.common;

/**
 * @author kkk
 */
public enum QuestionCode {

    /**
     * 问题已关闭
     */
    CLOSE(0, "问题已关闭"),

    /**
     * 问题待审核
     */
    TO_BE_AUDITED(1, "问题待审核"),

    /**
     * 问题仅可浏览
     */
    BROWSE_ONLY(2, "问题仅可浏览"),

    /**
     * 问题可回答
     */
    ANSWERABLE(3, "问题可回答");

    /**
     * 数字code
     */
    private final int code;
    /**
     * 描述
     */
    private final String desc;

    QuestionCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
