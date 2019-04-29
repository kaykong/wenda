package top.kongk.wenda.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.kongk.wenda.model.Category;
import top.kongk.wenda.model.Question;
import top.kongk.wenda.model.User;

import java.util.List;
import java.util.Map;

/**
 * @author kkk
 */
@Mapper
public interface CourseCategoryDao {

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
    //User selectById(Integer id);

    Category selectById(Integer id);

    int countBySelective(Category category);

    List<Category> selectBySelectiveWithPage(Map map);

    int updateRoleById(@Param("id") Integer id, @Param("role") Integer role);

    List<Category> getCategoryListByLevel(@Param("level") Integer level);

    int updateNameParentIdById(@Param("id")Integer id,
                                   @Param("name")String name,
                                   @Param("parentId")Integer parentId);

    int addCourseCategory(Category category);


    int deleteCategoryById(Integer id);
}
