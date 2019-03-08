package top.kongk.wenda.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kongk.wenda.dao.UserDao;
import top.kongk.wenda.model.User;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public List<User> getUserList() {
        return userDao.getUserList();
    }


    public List<User> getUserList2() {
        return userDao.getUserList2();
    }
}
