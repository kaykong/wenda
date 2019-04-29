package top.kongk.wenda.controller.backend;

import org.apache.commons.validator.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import top.kongk.wenda.model.Page;
import top.kongk.wenda.model.User;
import top.kongk.wenda.service.UserService;
import top.kongk.wenda.util.WendaUtil;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/backend")
public class BackendUserController {

    @Autowired
    UserService userService;

    /*
    *  点击修改小按钮的时候 要根据id获取一个员工
    * */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    @ResponseBody
    public User getUser(@PathVariable("id") Integer id) {

        User user = userService.getUser(id);
        user.setPassword("");
        user.setSalt("");
        return user;
    }

    /*
    * 根据主键 更新 user
    * */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    @ResponseBody
    public String getEmployee(User user) {

        boolean check = userService.updateRoleById(user.getId(), user.getRole());

        if (check) {
            return WendaUtil.getJSONString(0);
        } else {
            return WendaUtil.getJSONString(1, "更新失败");
        }
    }


    /*
    * 根据条件 分页查询员工
    * */
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public Page<User> getUsers(User user,
                               @RequestParam(name = "currPage", defaultValue = "1") int currPage,
                               @RequestParam(name = "pageSize", defaultValue = "5") int pageSize) {
        if (currPage < 1 || pageSize < 1) {
            return null;
        }

        return userService.getUsersBySelectiveWithPage(user, currPage, pageSize);
    }


}
