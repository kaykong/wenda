package top.kongk.wenda.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import top.kongk.wenda.dao.LoginTicketDao;
import top.kongk.wenda.model.HostHolder;
import top.kongk.wenda.model.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author kkk
 */
@Component
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LoginTicketDao loginTicketDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        /*
         * 遍历cookie, 获取ticket, 获取当前user信息, 存入hostHolder
         */
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("ticket".equals(cookie.getName())) {
                    String ticket = cookie.getValue();
                    User user = loginTicketDao.getUsableUserByTicket(ticket);
                    if (user != null) {
                        //为每一个请求线程绑定 user 信息
                        hostHolder.setCurrentUser(user);
                    }
                    break;
                }
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && hostHolder.getCurrentUser() != null) {
            modelAndView.addObject("user", hostHolder.getCurrentUser());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //一次请求线程结束, 清除当前线程的user信息
        hostHolder.removeCurrentUser();
    }
}
