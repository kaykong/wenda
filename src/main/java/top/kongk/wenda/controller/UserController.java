package top.kongk.wenda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.kongk.wenda.model.User;
import top.kongk.wenda.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Boolean login(@RequestParam String name, @RequestParam String password) {
        return false;
    }


    @GetMapping("/getList")
    public List<User> getList() {
        return userService.getUserList();
    }
}
