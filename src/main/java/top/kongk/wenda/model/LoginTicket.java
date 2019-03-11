package top.kongk.wenda.model;


import java.time.LocalDateTime;

/**
 * @author kkk
 */
public class LoginTicket extends BaseIdEntity {

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * ticket
     */
    private String ticket;

    /**
     * 过期时间
     */
    private LocalDateTime expired;

    /**
     * 状态 0-登录, 1-退出登录
     */
    private Integer status;


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public LocalDateTime getExpired() {
        return expired;
    }

    public void setExpired(LocalDateTime expired) {
        this.expired = expired;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
