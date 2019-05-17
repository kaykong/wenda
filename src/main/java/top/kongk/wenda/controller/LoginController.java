package top.kongk.wenda.controller;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.kongk.wenda.model.User;
import top.kongk.wenda.service.DataDictionaryService;
import top.kongk.wenda.service.SensitiveService;
import top.kongk.wenda.service.UserService;
import top.kongk.wenda.util.JedisAdapter;
import top.kongk.wenda.util.MyMailSender;
import top.kongk.wenda.util.RedisKeyUtil;
import top.kongk.wenda.util.WendaUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * @author kk
 */
@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    DataDictionaryService dataDictionaryService;

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    MyMailSender mailSender;

    @Autowired
    SensitiveService sensitiveService;

    @RequestMapping(path = {"/registerIndex"}, method = {RequestMethod.GET})
    public String registerIndex(Model model, @RequestParam(value = "next", required = false) String next) {
        model.addAttribute("next", next);

        Map<String, String> map = dataDictionaryService.getByType("email");

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getValue() + "  ");
        }

        model.addAttribute("email-limit", sb.toString());
        return "register";
    }

    /*@RequestMapping(path = {"/reg/"}, method = {RequestMethod.POST})
    public String reg(Model model, @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam("next") String next,
                      @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                      HttpServletResponse response) {
        try {
            User user = new User();
            user.setName(username);
            user.setPassword(password);
            Map<String, Object> map = userService.register(username, password, "邮箱test");
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme) {
                    cookie.setMaxAge(3600 * 24 * 5);
                }
                response.addCookie(cookie);
                if (StringUtils.isNotBlank(next) && !next.contains("http")) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }

        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            model.addAttribute("msg", "服务器错误");
            return "login";
        }
    }*/

    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String regloginPage(Model model, @RequestParam(value = "next", required = false) String next) {
        model.addAttribute("next", next);

        Map<String, String> map = dataDictionaryService.getByType("email");

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getValue() + " ");
        }

        model.addAttribute("email-limit", sb.toString());
        return "login";
    }

    @RequestMapping(path = {"/login"}, method = {RequestMethod.POST})
    public String login(Model model, @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "next", required = false) String next,
                        @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                        HttpServletResponse response) {
        try {
            Map<String, Object> map = new HashMap<>(0);
            if (StringUtils.isNotBlank(username)) {
                if (username.contains("@")) {
                    map = userService.loginByEmail(username, password);
                } else {
                    map = userService.login(username, password);
                }
            }

            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme) {
                    //5天
                    cookie.setMaxAge(3600 * 24 * 5);
                }
                response.addCookie(cookie);
                if (StringUtils.isNotBlank(next) && !next.contains("http")) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        } catch (Exception e) {
            logger.error("登陆异常" + e.getMessage());
            return "login";
        }
    }

    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }

    @RequestMapping(path = {"/getCheckCode"}, method = {RequestMethod.GET})
    @ResponseBody
    public String getCheckCode(@RequestParam("email") String email) {

        /*
         * 1.验证邮箱格式
         */
        if (StringUtils.isBlank(email)) {
            return WendaUtil.getJSONString(1, "邮箱不能为空");
        }

        boolean emailFormat = false;
        Map<String, String> map = dataDictionaryService.getByType("email");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (email.endsWith(entry.getValue())) {
                emailFormat = true;
            }
        }

        if (!emailFormat && map.size() > 0) {
            return WendaUtil.getJSONString(1, "邮箱格式不对, 请查看可注册的邮箱");
        }

        /*
         * 2.检测邮箱是否被使用
         */

        User userByEmail = userService.getUserByEmail(email);
        if (userByEmail != null) {
            return WendaUtil.getJSONString(1, "邮箱[" + email + "]已经被注册");
        }


        /*
         * 3.生成验证码, 保存在redis, 邮件发送验证码, 返回提示信息
         */
        String checkCode = WendaUtil.getRandom(6);
        String emailRegisterKey = RedisKeyUtil.getEmailRegisterKey(email);

        boolean exists = jedisAdapter.exists(emailRegisterKey);

        if (exists) {
            return WendaUtil.getJSONString(0, "验证码已经发送, 请注意查收");
        }


        Map<String, Object> map2 = new HashMap<>(2);
        map2.put("checkCode", checkCode);
        map2.put("time", "5分钟");


        boolean send = mailSender.sendWithHTMLTemplate(email, "验证码", "mails/register_checkCode.html", map2);
        if (!send) {
            return WendaUtil.getJSONString(1, "验证码发送失败, 请稍后再试");
        } else {
            //5分钟
            jedisAdapter.setex(emailRegisterKey, 300, checkCode);
        }

        return WendaUtil.getJSONString(0, "验证码已发送");
    }

    private String checkUsername(String name) {
        /*
         * 1.验证用户名格式
         */
        if (StringUtils.isBlank(name)) {
            return WendaUtil.getJSONString(1, "用户名不能为空");
        }

        if (name.contains("*")) {
            return WendaUtil.getJSONString(1, "用户名不能包含*");
        }
        if (name.contains("@")) {
            return WendaUtil.getJSONString(1, "用户名不能包含@");
        }
        String filter = sensitiveService.filter(name);
        if (filter.contains(WendaUtil.REPLACE_COMMENT)) {
            return WendaUtil.getJSONString(1, "用户名包含敏感词");
        }

        /*
         * 2.检测用户名是否被使用
         */
        User userByName = userService.getUserByName(name);
        if (userByName != null) {
            return WendaUtil.getJSONString(1, "用户名[" + name + "]已经被注册");
        }

        return null;
    }

    @RequestMapping(path = {"/checkUsername"}, method = {RequestMethod.GET})
    @ResponseBody
    public String checkUsername(Model model, @RequestParam("name") String name) {

        String check = checkUsername(name);
        if (check == null) {
            return WendaUtil.getJSONString(0, "用户名可用");
        } else {
            return check;
        }
    }


    @RequestMapping(path = {"/register"}, method = {RequestMethod.POST})
    @ResponseBody
    public String register(Model model, @RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam("email") String email,
                           @RequestParam("checkCode") String checkCode,
                           HttpServletResponse response) {

        /*
         * 1.检测用户名
         */
        String checkUsername = checkUsername(username);
        if (checkUsername != null) {
            return checkUsername;
        }

        /*
         * 2.检测邮箱和验证码
         */
        String emailRegisterKey = RedisKeyUtil.getEmailRegisterKey(email);
        boolean exists = jedisAdapter.exists(emailRegisterKey);
        if (!exists) {
            return WendaUtil.getJSONString(1, "验证码错误, 请重新获取");
        }

        String checkCodeRedis = jedisAdapter.get(emailRegisterKey);
        if (checkCodeRedis == null) {
            return WendaUtil.getJSONString(1, "验证码错误, 请重新获取");
        }

        if (!checkCodeRedis.equals(checkCode)) {
            return WendaUtil.getJSONString(1, "验证码错误");
        }

        try {
            User user = new User();
            user.setName(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setRole(2);
            Map<String, Object> map = userService.register(username, password, email);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                cookie.setMaxAge(3600 * 24 * 5);
                response.addCookie(cookie);
            } else {
                return WendaUtil.getJSONString(1, (String) map.get("msg"));
            }
        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            return WendaUtil.getJSONString(1, "服务器错误");
        }

        return WendaUtil.getJSONString(0);
    }
}
