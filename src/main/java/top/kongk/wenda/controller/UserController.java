package top.kongk.wenda.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.kongk.wenda.common.ResponseCode;
import top.kongk.wenda.common.ServerResponse;
import top.kongk.wenda.model.User;
import top.kongk.wenda.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户模块
 *
 * @author kongkk
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ServerResponse login(@RequestParam("name") String name,
                         @RequestParam("password") String password,
                         @RequestParam(value = "rememberMe", defaultValue = "false") Boolean rememberMe,
                         HttpServletResponse response) {
        ServerResponse serverResponse;
        try {
            serverResponse = userService.login(name, password, rememberMe);
        } catch (Exception e) {
            log.error("登录异常 {}", e.getMessage());
            return ServerResponse.createErrorWithMsg("登录失败");
        }

        if (serverResponse.getStatus() == ResponseCode.SUCCESS_CODE && serverResponse.getData() != null) {
            //登录成功, 设置cookie
            String ticket = (String) serverResponse.getData();
            Cookie cookie = new Cookie("ticket", ticket);
            cookie.setPath("/");
            if (rememberMe) {
                //100天
                cookie.setMaxAge(100*24*60*60);
            } else {
                cookie.setMaxAge(7*24*60*60);
            }
            response.addCookie(cookie);
        }

        return serverResponse;
    }

    @PostMapping("/register")
    public ServerResponse register(@RequestBody User user, HttpServletResponse response) {

        ServerResponse serverResponse;
        try {
            serverResponse = userService.register(user);
        } catch (Exception e) {
            log.error("注册异常 {}", e.getMessage());
            return ServerResponse.createErrorWithMsg("注册失败");
        }

        if (serverResponse.getStatus() == ResponseCode.SUCCESS_CODE && serverResponse.getData() != null) {
            //注册成功, 设置cookie
            String ticket = (String) serverResponse.getData();
            Cookie cookie = new Cookie("ticket", ticket);
            cookie.setPath("/");
            cookie.setMaxAge(7*24*60*60);
            response.addCookie(cookie);
        }

        return serverResponse;
    }


    @GetMapping("/getList")
    public List<User> getList() {
        return userService.getUserList();
    }
}
