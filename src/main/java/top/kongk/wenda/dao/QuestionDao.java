package top.kongk.wenda.dao;

import org.apache.ibatis.annotations.Mapper;
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
}
