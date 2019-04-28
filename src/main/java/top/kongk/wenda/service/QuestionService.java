package top.kongk.wenda.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import top.kongk.wenda.common.QuestionCode;
import top.kongk.wenda.common.ServerResponse;
import top.kongk.wenda.dao.QuestionDao;
import top.kongk.wenda.model.Category;
import top.kongk.wenda.model.Question;
import top.kongk.wenda.model.User;

import java.util.Date;
import java.util.List;

/**
 * @author kkk
 */
@Service
public class QuestionService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    private QuestionDao questionDao;
    @Autowired
    private SensitiveService sensitiveService;


    /**
     * 用户提交问题, 把问题插入数据库, 设置状态为待审核
     *
     * @author kongkk
     * @param question 问题
     * @param user 用户
     * @return top.kongk.wenda.common.ServerResponse
     */
    public ServerResponse addQuestion(Question question, User user) {
        /*
         * 对title和content进行敏感词过滤
         */
        //过滤html标签
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        //question.setContent(HtmlUtils.htmlEscape(question.getContent()));

        //过滤敏感词
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));

        question.setUserId(user.getId());
        question.setStatus(QuestionCode.TO_BE_AUDITED.getCode());
        question.setCreatedDate(new Date());
        try {
            questionDao.addQuestion(question);
        } catch (Exception e) {
            logger.error("QuestionService.addQuestion Exception", e);
            return ServerResponse.createServerErrorWithMessage("问题提交失败");
        }

        /*
         * 问题的分类, 需要问题id, 还有一张表
         */
        return ServerResponse.createSuccessWithMsg("问题提交成功, 等待审核");
    }

    /**
     * 根据 id 获取问题
     *
     * @param id
     * @return top.kongk.wenda.common.ServerResponse
     */
    public ServerResponse getQuestionById(int id) {

        ServerResponse serverResponse;
        try {
            Question question = questionDao.getQuestionById(id);
            serverResponse = ServerResponse.createSuccess(question);
        } catch (Exception e) {
            logger.error("QuestionService.getQuestionById Exception", e);
            serverResponse = ServerResponse.createServerErrorWithMessage("数据库获取question失败");
        }

        return serverResponse;
    }

    /**
     * 根据 id 获取问题
     *
     * @param id
     * @return top.kongk.wenda.model.Question
     */
    public Question getById(int id) {

        Question question = new Question();
        try {
            question = questionDao.getQuestionById(id);
        } catch (Exception e) {
            logger.error("QuestionService.getQuestionById Exception", e);
        }

        return question;
    }


    /**
     * 分页获取问题数据-->展示在首页
     *
     * @param userId 用户id
     * @param offset
     * @param limit
     * @return java.util.List<top.kongk.wenda.model.Question>
     */
    public List<Question> getLatestQuestions(Integer userId, Integer offset, Integer limit) {
        return questionDao.selectLatestQuestions(userId, offset, limit);
    }

    public List<Question> getLatestQuestions(Integer userId, Integer offset, Integer limit, Integer categoryId) {
        return questionDao.selectLatestQuestionsByCategoryId(userId, offset, limit, categoryId);
    }

    /**
     * 分页或者不分页地获取数据 (limit 为 null), 可以按问题的分类 categoryId, 问题的创建人 userId
     *
     * @param userId
     * @param offset
     * @param limit
     * @param categoryId
     * @return java.util.List<top.kongk.wenda.model.Question>
     */
    public List<Question> getQuestions(Integer userId, Integer offset, Integer limit, Integer categoryId) {
        return questionDao.selectQuestions(userId, offset, limit, categoryId);
    }


    /**
     * 根据问题id更新问题的评论数
     *
     * @param id
     * @param count
     * @return int
     */
    public int updateCommentCount(int id, int count) {
        return questionDao.updateCommentCount(id, count);
    }


    /**
     * 根据 parentId 获取分类数据
     *
     * @param parentId
     * @return java.util.List<top.kongk.wenda.model.Category>
     */
    public List<Category> getCategoryListByParentId(String parentId) {
        return questionDao.getCategoryListByParentId(parentId);
    }


    public boolean deleteById(int id) {
        return questionDao.updateStatusById(id, 0) > 0;
    }
}
