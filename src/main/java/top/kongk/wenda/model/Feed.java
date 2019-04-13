package top.kongk.wenda.model;

import com.alibaba.fastjson.JSONObject;
import java.util.Date;


/**
 * @author kk
 */
public class Feed extends BaseIdEntity {

    /**
     * feed流中事件的类型, 点赞, 关注问题, 回答问题
     */
    private int type;

    /**
     * 这个事件是谁发起的, 用来匹配当前用户关注的人的id
     */
    private int userId;

    /**
     * 创建时间
     */
    private Date createdDate;

    /**
     * 事件具体的内容, 采用json存储
     */
    private String data;

    /**
     * 用来让前端方便地获取data中的数据
     */
    private JSONObject dataJSON = null;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        dataJSON = JSONObject.parseObject(data);
    }

    public String get(String key) {
        //在velocity中, 调用vo.title, 就是调用 get("title")
        return dataJSON == null ? null : dataJSON.getString(key);
    }
}
