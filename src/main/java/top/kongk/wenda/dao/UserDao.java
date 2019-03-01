package top.kongk.wenda.dao;

import org.apache.ibatis.annotations.Mapper;
import top.kongk.wenda.model.User;

import java.util.List;

@Mapper
public interface UserDao {

    List<User> getUserList();
}
