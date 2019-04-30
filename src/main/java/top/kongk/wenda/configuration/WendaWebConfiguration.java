package top.kongk.wenda.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import top.kongk.wenda.interceptor.LoginRequiredInterceptor;
import top.kongk.wenda.interceptor.ManagerRequiredInterceptor;
import top.kongk.wenda.interceptor.PassportInterceptor;

/**
 * @author kkk
 */
@Component
public class WendaWebConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private PassportInterceptor passportInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    ManagerRequiredInterceptor managerRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册绑定用户拦截器
        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor(loginRequiredInterceptor)
                .addPathPatterns("/user/*")
                .addPathPatterns("/msg/*");
        registry.addInterceptor(managerRequiredInterceptor)
                .addPathPatterns("/backend/*");
        super.addInterceptors(registry);
    }
}
