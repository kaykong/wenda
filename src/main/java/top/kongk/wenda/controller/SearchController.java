package top.kongk.wenda.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import top.kongk.wenda.model.EntityType;
import top.kongk.wenda.model.Question;
import top.kongk.wenda.model.ViewObject;
import top.kongk.wenda.service.FollowService;
import top.kongk.wenda.service.QuestionService;
import top.kongk.wenda.service.SearchService;
import top.kongk.wenda.service.UserService;

import java.util.ArrayList;
import java.util.List;


/**
 * @author kk
 */
@Controller
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    SearchService searchService;

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    /**
     * 搜索
     *
     * @param model
     * @param keyword
     * @param offset
     * @param count
     * @return java.lang.String
     */
    @RequestMapping(path = {"/search"}, method = {RequestMethod.GET})
    public String search(Model model, @RequestParam("q") String keyword,
                         @RequestParam(value = "offset", defaultValue = "0") int offset,
                         @RequestParam(value = "count", defaultValue = "100") int count) {
        try {

            List<Question> questionList = searchService.searchQuestion(keyword, offset, count,
                    "<em>", "</em>");

            List<ViewObject> vos = new ArrayList<>();
            for (Question question : questionList) {
                Question q = questionService.getById(question.getId());
                ViewObject vo = new ViewObject();
                if (question.getContent() != null) {
                    q.setContent(question.getContent());
                }
                if (question.getTitle() != null) {
                    q.setTitle(question.getTitle());
                }
                //得到搜索的问题
                vo.set("question", q);
                //问题下的关注人数
                vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
                //谁创建的该问题
                vo.set("user", userService.getUser(q.getUserId()));
                vos.add(vo);
            }
            model.addAttribute("vos", vos);
            model.addAttribute("keyword", keyword);
        } catch (Exception e) {
            logger.error("搜索评论失败" + e.getMessage());
        }
        return "result";
    }
}
