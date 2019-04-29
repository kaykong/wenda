package top.kongk.wenda.controller.backend;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import top.kongk.wenda.controller.HomeController;
import top.kongk.wenda.model.*;
import top.kongk.wenda.service.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * @author kk
 */
@Controller
@RequestMapping("/backend")
public class BackendHomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    FollowService followService;

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model) {

        return "back-user";
    }

    @RequestMapping(path = {"/courseCategoryIndex"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String courseCategory(Model model) {

        return "back-courseCategory";
    }

    @RequestMapping(path = {"/sensitiveWordIndex"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String sensitiveWord(Model model) {

        return "back-sensitiveWord";
    }

}
