package top.kongk.wenda.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kongk.wenda.common.ServerResponse;
import top.kongk.wenda.common.UserValidatorUtil;
import top.kongk.wenda.dao.CourseCategoryDao;
import top.kongk.wenda.dao.LoginTicketDao;
import top.kongk.wenda.dao.QuestionDao;
import top.kongk.wenda.dao.UserDao;
import top.kongk.wenda.model.*;
import top.kongk.wenda.util.MD5Util;
import top.kongk.wenda.util.WendaUtil;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author kkk
 */
@Service

public class CourseCategoryService {

    private static final Logger log = LoggerFactory.getLogger(CourseCategoryService.class);

    @Autowired
    private CourseCategoryDao courseCategoryDao;

    @Autowired
    private QuestionDao questionDao;



    public Category getCategory(int id) {
        return courseCategoryDao.selectById(id);
    }

    public User getUserByName(String name) {

        if (!UserValidatorUtil.isUsername(name)) {
            return null;
        }

        return courseCategoryDao.getUserByName(name);
    }

    public User getUserByEmail(String email) {
        if (!UserValidatorUtil.isEmail(email)) {
            return null;
        }

        return courseCategoryDao.getUserByEmail(email);
    }


    /*public Page<User> getUsersBySelectiveWithPage(User user, int currPage, int pageSize) {
        int totalCount = courseCategoryDao.countBySelective(user);

        if (currPage < 0) {
            currPage = 1;
        }

        //初始化Page 生成Page对象,传入当前页数,每页的记录数,以及记录总数
        Page<User> page = new Page<>(currPage,pageSize,totalCount);

        if (totalCount == 0) {
            return page;
        }

        //设置map, 因为 courseCategoryDao.selectBySelectiveWithPage(map) 需要
        Map map = new HashMap(3);
        map.put("user", user);
        map.put("start", page.getStart());
        map.put("pageSize", page.getPageSize());

        List<User> userList = courseCategoryDao.selectBySelectiveWithPage(map);

        page.setLists(userList);

        return page;
    }*/

    public Page<Category> getCourseCategoriesBySelectiveWithPage(Category category, int currPage, int pageSize) {

        int totalCount = courseCategoryDao.countBySelective(category);

        if (currPage < 0) {
            currPage = 1;
        }

        //初始化Page 生成Page对象,传入当前页数,每页的记录数,以及记录总数
        Page<Category> page = new Page<>(currPage,pageSize,totalCount);

        if (totalCount == 0) {
            return page;
        }

        //设置map, 因为 courseCategoryDao.selectBySelectiveWithPage(map) 需要
        Map map = new HashMap(3);
        map.put("category", category);
        map.put("start", page.getStart());
        map.put("pageSize", page.getPageSize());

        List<Category> categoryList = courseCategoryDao.selectBySelectiveWithPage(map);

        page.setLists(categoryList);

        return page;
    }

    public boolean updateRoleById(Integer id, Integer role) {
        return courseCategoryDao.updateRoleById(id, role) > 0;
    }

    public List<Category> getCategoryListByLevel(Integer level) {
        return courseCategoryDao.getCategoryListByLevel(level);
    }

    public boolean updateNameParentIdById(Integer id, String name, Integer parentId) {
        return courseCategoryDao.updateNameParentIdById(id, name, parentId) > 0;
    }

    public boolean addCourseCategory(Category category) {
        try {
            courseCategoryDao.addCourseCategory(category);
        } catch (Exception e) {
            log.error("新增课程失败");
        }
        return  category.getId() != null && category.getId() > 0;
    }

    public List<Question> checkCategoryInUse(Integer id) {
        List<Question> questionList = questionDao.selectQuestionByCategoryId(id);
        return questionList;
    }

    public boolean deleteCategoryById(Integer id) {
        return courseCategoryDao.deleteCategoryById(id) > 0;
    }
}
