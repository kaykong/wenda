package top.kongk.wenda.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.kongk.wenda.model.LoginTicket;
import top.kongk.wenda.model.User;

import java.util.List;

/**
 * @author kkk
 */
@Mapper
public interface LoginTicketDao {

    /**
     * 新增ticket
     *
     * @author kongkk
     * @param loginTicket
     */
    void addLoginTicket(LoginTicket loginTicket);


    /**
     * 根据ticket, 获取可用的,有效的 user
     *
     * @author kongkk
     * @param ticket ticket
     * @return top.kongk.wenda.model.User
     */
    User getUsableUserByTicket(String ticket);

    /**
     * 更新 ticket 状态
     *
     * @param ticket
     * @param status
     * @return void
     */
    void updateStatus(@Param("ticket") String ticket, @Param("status") Integer status);
}
