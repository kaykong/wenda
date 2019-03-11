package top.kongk.wenda.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kongk.wenda.common.ServerResponse;
import top.kongk.wenda.common.UserValidatorUtil;
import top.kongk.wenda.dao.LoginTicketDao;
import top.kongk.wenda.dao.UserDao;
import top.kongk.wenda.model.LoginTicket;
import top.kongk.wenda.model.User;
import top.kongk.wenda.util.MD5Util;
import top.kongk.wenda.util.WendaUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author kkk
 */
@Service

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDao userDao;
    @Autowired
    private LoginTicketDao loginTicketDao;

    public List<User> getUserList() {
        return userDao.getUserList();
    }


    public List<User> getUserList2() {
        return userDao.getUserList2();
    }

    public User getUserByName(String name) {

        if (!UserValidatorUtil.isUsername(name)) {
            return null;
        }

        return userDao.getUserByName(name);
    }

    public User getUserByEmail(String email) {
        if (!UserValidatorUtil.isEmail(email)) {
            return null;
        }

        return userDao.getUserByEmail(email);
    }

    public ServerResponse register(User user) {
        /*
         * 检查参数
         */
        if (!UserValidatorUtil.isUsername(user.getName())) {
            return ServerResponse.createErrorWithMsg("用户名格式错误");
        }

        if (!UserValidatorUtil.isPassword(user.getPassword())) {
            return ServerResponse.createErrorWithMsg("密码格式错误");
        }

        if (StringUtils.isNotBlank(user.getEmail())) {
            if (!UserValidatorUtil.isEmail(user.getEmail())) {
                return ServerResponse.createErrorWithMsg("邮箱格式错误");
            }
            if (getUserByEmail(user.getEmail()) != null) {
                return ServerResponse.createErrorWithMsg("邮箱[" + user.getEmail() +"]已经被占用");
            }
        }

        if (getUserByName(user.getName()) != null) {
            return ServerResponse.createErrorWithMsg("用户名已经被注册");
        }

        /*
         * 设置必要的属性
         */
        user.setRole(1);
        if (user.getGender() == null) {
            user.setGender(1);
        }
        user.setSalt(UUID.randomUUID().toString().substring(0, 6));
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword() + user.getSalt()));



        /*
         * 在数据库中插入记录
         */
        try {
            userDao.addUser(user);
        } catch (Exception e) {
            log.error("新增用户异常");
            return ServerResponse.createServerErrorWithMessage("注册失败, 请重试");
        }

        /*
         * 设置ticket
         */
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setStatus(0);
        loginTicket.setTicket(WendaUtil.get32UUID());
        loginTicket.setUserId(user.getId());
        LocalDateTime localDateTime = LocalDateTime.now();
        loginTicket.setExpired(localDateTime.plusDays(7));

        try {
            loginTicketDao.addLoginTicket(loginTicket);
            return ServerResponse.createSuccess(loginTicket.getTicket());
        } catch (Exception e) {
            log.error("新增ticket {}", e.getMessage());
            return ServerResponse.createSuccess();
        }

    }

    public ServerResponse login(String name, String password, Boolean rememberMe) {

        if (!UserValidatorUtil.isUsername(name)) {
            return ServerResponse.createErrorWithMsg("用户名格式错误");
        }
        if (!UserValidatorUtil.isPassword(password)) {
            return ServerResponse.createErrorWithMsg("密码格式错误");
        }

        User user = getUserByName(name);

        if (user == null) {
            return ServerResponse.createErrorWithMsg("用户名不存在");
        }

        if (!MD5Util.MD5EncodeUtf8(password + user.getSalt()).equals(user.getPassword())) {
            return ServerResponse.createErrorWithMsg("密码错误");
        }

        /*
         * 设置ticket
         */
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setStatus(0);
        loginTicket.setTicket(WendaUtil.get32UUID());
        loginTicket.setUserId(user.getId());
        LocalDateTime localDateTime = LocalDateTime.now();
        if (rememberMe) {
            //记住我
            loginTicket.setExpired(localDateTime.plusDays(100));
        } else {
            loginTicket.setExpired(localDateTime.plusDays(7));
        }

        try {
            loginTicketDao.addLoginTicket(loginTicket);
        } catch (Exception e) {
            log.error("新增ticket异常 {}", e.getMessage());
            return ServerResponse.createServerErrorWithMessage("登录失败, 请重试");
        }

        return ServerResponse.createSuccess(loginTicket.getTicket());
    }
}
