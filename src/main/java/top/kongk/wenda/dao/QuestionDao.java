package top.kongk.wenda.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
     * @author kongkk
     * @param question
     * @return void
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
}
