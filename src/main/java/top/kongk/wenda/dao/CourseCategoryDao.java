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

    Category selectById(Integer id);

    int countBySelective(Category category);

    List<Category> selectBySelectiveWithPage(Map map);

    List<Category> getCategoryListByLevel(@Param("level") Integer level);

    int updateNameParentIdById(@Param("id") Integer id,
                               @Param("name") String name,
                               @Param("parentId") Integer parentId);

    int addCourseCategory(Category category);


    int deleteCategoryById(Integer id);
}
