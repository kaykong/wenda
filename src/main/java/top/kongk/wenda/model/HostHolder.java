package top.kongk.wenda.model;

import org.springframework.stereotype.Component;

/**
 * @author kkk
 */
@Component
public class HostHolder {

    private static ThreadLocal<User> threadLocalUsers = new ThreadLocal<>();

    public User getCurrentUser() {
        return threadLocalUsers.get();
    }

    public void setCurrentUser(User user) {
        threadLocalUsers.set(user);
    }

    public void removeCurrentUser() {
        threadLocalUsers.remove();
    }

}
