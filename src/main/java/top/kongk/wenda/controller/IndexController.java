package top.kongk.wenda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.kongk.wenda.model.User;
import top.kongk.wenda.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/test")
public class IndexController {

    @Autowired
    private UserService userService;

    @GetMapping("/getList")
    public List<User> getList() {
        return userService.getUserList();
    }

    @GetMapping("/response")
    public String testResponse(HttpServletResponse response) {

        //生成随机字符串
        String uuid = UUID.randomUUID().toString();
        Cookie cookie = new Cookie("sessionId", uuid);
        cookie.setMaxAge(300);
        //在response中添加cookie
        response.addCookie(cookie);

        return uuid;
    }

    @GetMapping("/request")
    public String testResponse(HttpServletRequest request, @CookieValue(value = "sessionId", required = false) String sessionId) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName() + "|" + cookie.getValue());
                System.out.println(cookie.getMaxAge());
            }
        }
        return sessionId;
    }

    @GetMapping("/list")
    public List<String> testList() {

        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
            stringList.add(UUID.randomUUID().toString().substring(0, 10));
        }
        return stringList;
    }

    @GetMapping("/list2")
    public List<String> testList2() {

        List<String> stringList = new ArrayList<>();

        return stringList;
    }

}
