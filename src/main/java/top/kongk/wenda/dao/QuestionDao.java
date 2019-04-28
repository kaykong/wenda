package top.kongk.wenda.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.kongk.wenda.model.Category;
import top.kongk.wenda.model.Question;
import top.kongk.wenda.model.User;

import java.util.List;

/**
 * @author kkk
 */
@Mapper
public interface QuestionDao {

    /**
     * 用户提交问题, 把问题插入数据库, 设置状态为待审核
     *
     * @param question
     * @return void
     * @author kongkk
     */
    void addQuestion(Question question);

    /**
     * 根据id获取问题
     *
     * @param id
     * @return top.kongk.wenda.model.Question
     */
    Question getQuestionById(int id);

    /**
     * 分页获取问题
     *
     * @param userId
     * @param offset
     * @param limit
     * @return java.util.List<top.kongk.wenda.model.Question>
     */
    List<Question> selectLatestQuestions(@Param("userId") Integer userId, @Param("offset") Integer offset,
                                         @Param("limit") Integer limit);

    /**
     * 根据问题id更新问题的评论数
     *
     * @param id
     * @param commentCount
     * @return int
     */
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    /**
     * 根据 parentId 获取分类数据
     *
     * @param parentId
     * @return java.util.List<top.kongk.wenda.model.Category>
     */
    List<Category> getCategoryListByParentId(String parentId);

    List<Question> selectLatestQuestionsByCategoryId(@Param("userId") Integer userId,
                                                     @Param("offset") Integer offset,
                                                     @Param("limit") Integer limit,
                                                     @Param("categoryId") Integer categoryId);

    /**
     * 分页或者不分页地获取数据 (limit 为 null), 可以按问题的分类 categoryId, 问题的创建人 userId
     *
     * @param userId
     * @param offset
     * @param limit
     * @param categoryId
     * @return java.util.List<top.kongk.wenda.model.Question>
     */
    List<Question> selectQuestions(@Param("userId") Integer userId,
                                   @Param("offset") Integer offset,
                                   @Param("limit") Integer limit,
                                   @Param("categoryId") Integer categoryId);

    /**
     * 根据id 更新问题的status
     *
     * @param id
     * @param status
     * @return int
     */
    int updateStatusById(@Param("id") int id,
                         @Param("status") int status);
}
