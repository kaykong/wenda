package top.kongk.wenda.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import top.kongk.wenda.common.QuestionCode;
import top.kongk.wenda.common.ServerResponse;
import top.kongk.wenda.dao.QuestionDao;
import top.kongk.wenda.model.Question;
import top.kongk.wenda.model.User;

import java.time.LocalDateTime;
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
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));

        //过滤敏感词
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));

        question.setUserId(user.getId());
        question.setStatus(QuestionCode.TO_BE_AUDITED.getCode());
        //question.setCreateDate(LocalDateTime.now());
        question.setCreateDate(new Date());
        questionDao.addQuestion(question);
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
     * 分页获取数据
     *
     * @param userId 用户id
     * @param offset
     * @param limit
     * @return java.util.List<top.kongk.wenda.model.Question>
     */
    public List<Question> getLatestQuestions(Integer userId, Integer offset, Integer limit) {
        return questionDao.selectLatestQuestions(userId, offset, limit);
    }
}
