package top.kongk.wenda.dao;

import org.apache.ibatis.annotations.Mapper;
import top.kongk.wenda.model.User;

import java.util.List;

/**
 * @author kkk
 */
@Mapper
public interface UserDao {

    List<User> getUserList();

    List<User> getUserList2();

    /**
     * 根据用户名返回 User
     *
     * @author kongkk
     * @param name 用户名
     * @return top.kongk.wenda.model.User
     */
    User getUserByName(String name);

    /**
     * 根据邮箱返回 User
     *
     * @author kongkk
     * @param email 邮箱
     * @return top.kongk.wenda.model.User
     */
    User getUserByEmail(String email);

    /**
     * 新增用户, 成功后user.getId就是数据库的主键
     *
     * @author kongkk
     * @param user
     * @return int
     */
    int addUser(User user);

    /**
     * 根据id获取用户
     *
     * @param id
     * @return top.kongk.wenda.model.User
     */
    User selectById(Integer id);
}
