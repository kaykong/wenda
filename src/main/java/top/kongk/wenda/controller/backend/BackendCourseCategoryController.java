package top.kongk.wenda.controller.backend;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.kongk.wenda.model.*;
import top.kongk.wenda.service.CourseCategoryService;
import top.kongk.wenda.util.WendaUtil;

import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/backend")
public class BackendCourseCategoryController {

    @Autowired
    CourseCategoryService courseCategoryService;

    @Autowired
    HostHolder hostHolder;

    /*
    *  点击修改小按钮的时候 要根据id获取一个员工
    * */
    @RequestMapping(value = "/courseCategory/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Category getUser(@PathVariable("id") Integer id) {

        Category category = courseCategoryService.getCategory(id);
        return category;
    }

    @RequestMapping(value = "/getCategoryListByLevel", method = RequestMethod.GET)
    @ResponseBody
    public List<Category> getCategoryListByLevel(@RequestParam("level") Integer level) {

        List<Category> categoryList = courseCategoryService.getCategoryListByLevel(level);
        return categoryList;
    }

    /*
    * 根据主键 更新 user
    * */
    @RequestMapping(value = "/courseCategory", method = RequestMethod.POST)
    @ResponseBody
    public String getEmployee(Category category) {

        boolean check = courseCategoryService.updateNameParentIdById(category.getId(), category.getName(), category.getParentId());

        if (check) {
            return WendaUtil.getJSONString(0);
        } else {
            return WendaUtil.getJSONString(1, "更新失败");
        }
    }

    private boolean checkCourseCategory(Category category) {
        boolean useful = true;
        if (StringUtils.isBlank(category.getName())) {
            useful = false;
        }
        if (category.getParentId() == null) {
            useful = false;
        }
        if (category.getLevel() == null) {
            useful = false;
        }

        return useful;
    }


    @RequestMapping(value = "/addCourseCategory", method = RequestMethod.POST)
    @ResponseBody
    public String addCourseCategory(Category category) {


        User currentUser = hostHolder.getCurrentUser();
        if (currentUser != null) {
            category.setUpdateUserId(currentUser.getId());
            category.setUpdateTime(new Date());
        }

        if (!checkCourseCategory(category)) {
            return WendaUtil.getJSONString(1, "数据不完整");
        }

        boolean check = courseCategoryService.addCourseCategory(category);

        if (check) {
            return WendaUtil.getJSONString(0);
        } else {
            return WendaUtil.getJSONString(1, "新增失败");
        }
    }

    @RequestMapping(value = "/deleteCourseCategory/{id}", method = RequestMethod.POST)
    @ResponseBody
    public String deleteCourseCategory(@PathVariable("id") Integer id) {

        /*
         * 1.先从question中检查是否被使用,
         */
        List<Question> questions = courseCategoryService.checkCategoryInUse(id);

        if (questions.size() > 0) {
            return WendaUtil.getJSONString(1, "该课程分类信息已经被问题使用");
        }

        boolean check = courseCategoryService.deleteCategoryById(id);

        if (check) {
            return WendaUtil.getJSONString(0);
        } else {
            return WendaUtil.getJSONString(1, "新增失败");
        }
    }


    /*
    * 根据条件 分页查询员工
    * */
    @RequestMapping(value = "/courseCategories", method = RequestMethod.GET)
    @ResponseBody
    public Page<Category> getUsers(Category category,
                               @RequestParam(name = "currPage", defaultValue = "1") int currPage,
                               @RequestParam(name = "pageSize", defaultValue = "8") int pageSize) {
        if (currPage < 1 || pageSize < 1) {
            return null;
        }

        //return courseCategoryService.getUsersBySelectiveWithPage(user, currPage, pageSize);
        return courseCategoryService.getCourseCategoriesBySelectiveWithPage(category, currPage, pageSize);
    }


}
