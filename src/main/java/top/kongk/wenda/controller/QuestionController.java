package top.kongk.wenda.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.kongk.wenda.common.QuestionCode;
import top.kongk.wenda.common.ResponseCode;
import top.kongk.wenda.common.ServerResponse;
import top.kongk.wenda.model.HostHolder;
import top.kongk.wenda.model.Question;
import top.kongk.wenda.model.User;
import top.kongk.wenda.service.QuestionService;
import top.kongk.wenda.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 问题模块
 *
 * @author kongkk
 */
@Controller
@RequestMapping("/question")
public class QuestionController {

    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private QuestionService questionService;

    /**
     * 用户提交问题, 把问题插入数据库, 设置状态为待审核
     *
     * @author kongkk
     * @param question
     * @return top.kongk.wenda.common.ServerResponse
     */
    @RequestMapping(value = "/addQuestion", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addQuestion(@RequestBody Question question) {

        User user = hostHolder.getCurrentUser();
        if (user == null) {
            return ServerResponse.createNeedloginError("请先登录");
        }
        ServerResponse serverResponse;
        try {
            serverResponse = questionService.addQuestion(question, user);
        } catch (Exception e) {
            log.error("新增问题异常 {}", e.getMessage());
            return ServerResponse.createErrorWithMsg("新增问题失败");
        }

        return serverResponse;
    }

    @RequestMapping("{id}")
    @ResponseBody
    public ServerResponse getQuestion(@PathVariable("id") int id) {

        /*User user = hostHolder.getCurrentUser();
        if (user == null) {
            return ServerResponse.createNeedloginError("请先登录");
        }*/
        ServerResponse serverResponse;
        try {
            serverResponse = questionService.getQuestionById(id);
        } catch (Exception e) {
            log.error("获取问题异常 {}", e.getMessage());
            return ServerResponse.createErrorWithMsg("获取问题失败");
        }

        return serverResponse;
    }


}
