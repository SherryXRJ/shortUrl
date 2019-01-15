package pers.sherry.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 *  短链接拦截器
 *  拦截指定格式的短链接地址
 *  如果是短链接地址，则重定向指定接口中进行长链接转换
 */
public class ShortUrlInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //  拦截短地址 重定向到指定接口
        response.sendRedirect(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/shortUrl/visit" + request.getServletPath());
        return false;
    }
}
