package top.kongk.wenda.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import top.kongk.wenda.common.QuestionCode;
import top.kongk.wenda.common.ServerResponse;
import top.kongk.wenda.dao.DataDictionaryDao;
import top.kongk.wenda.dao.QuestionDao;
import top.kongk.wenda.model.Category;
import top.kongk.wenda.model.DataDictionary;
import top.kongk.wenda.model.Question;
import top.kongk.wenda.model.User;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kkk
 */
@Service
public class DataDictionaryService {

    private static final Logger logger = LoggerFactory.getLogger(DataDictionaryService.class);

    @Autowired
    private DataDictionaryDao dictionaryDao;
    @Autowired
    private SensitiveService sensitiveService;


    /**
     * dataDictionaryList 的 code, value 组成的map
     *
     * @param type
     * @return java.util.Map<java.lang.String,java.lang.String>
     */
    public Map<String, String> getByType(String type) {

        List<DataDictionary> dataDictionaryList = dictionaryDao.getDataDictionaryListByType(type);

        Map<String, String> map = new HashMap<>(dataDictionaryList.size());
        for (DataDictionary dataDictionary : dataDictionaryList) {
            map.put(dataDictionary.getCode(), dataDictionary.getValue());
        }

        return map;
    }


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
            dictionaryDao.addQuestion(question);
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
            Question question = dictionaryDao.getQuestionById(id);
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
            question = dictionaryDao.getQuestionById(id);
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
        return dictionaryDao.selectLatestQuestions(userId, offset, limit);
    }

    public List<Question> getLatestQuestions(Integer userId, Integer offset, Integer limit, Integer categoryId) {
        return dictionaryDao.selectLatestQuestionsByCategoryId(userId, offset, limit, categoryId);
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
        return dictionaryDao.selectQuestions(userId, offset, limit, categoryId);
    }


    /**
     * 根据问题id更新问题的评论数
     *
     * @param id
     * @param count
     * @return int
     */
    public int updateCommentCount(int id, int count) {
        return dictionaryDao.updateCommentCount(id, count);
    }


    /**
     * 根据 parentId 获取分类数据
     *
     * @param parentId
     * @return java.util.List<top.kongk.wenda.model.Category>
     */
    public List<Category> getCategoryListByParentId(String parentId) {
        return dictionaryDao.getCategoryListByParentId(parentId);
    }


    public boolean deleteById(int id) {
        return dictionaryDao.updateStatusById(id, 1) > 0;
    }
}
