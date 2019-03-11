package top.kongk.wenda.dao;

import org.apache.ibatis.annotations.Mapper;
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
}
