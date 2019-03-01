package top.kongk.wenda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.kongk.wenda.model.User;
import top.kongk.wenda.service.UserService;

import java.util.List;

@RestController
public class IndexController {

    @Autowired
    private UserService userService;

    @GetMapping("/getList")
    public List<User> getList() {
        return userService.getUserList();
    }
}
