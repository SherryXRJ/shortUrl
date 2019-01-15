package pers.sherry.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pers.sherry.interceptor.ShortUrlInterceptor;

@Configuration
@ConditionalOnClass
@ConditionalOnMissingBean(WebMvcConfigurationSupport.class)
public class WebConfig implements WebMvcConfigurer {

    /**
     * 短链接前缀
     */
    private final static String SHORT_URL_PREFIX = "/s.my/**";

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //  添加拦截器
        registry.addInterceptor(new ShortUrlInterceptor())
                .addPathPatterns(SHORT_URL_PREFIX)
                .excludePathPatterns("/shortUrl/visit/**");
    }
}
