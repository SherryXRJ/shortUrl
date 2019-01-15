package pers.sherry.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 切面:统计次数拦截器
 * 使用Redis存储短链接被访问的次数
 */
@Aspect
@Component
public class CountAspect {

    @Resource(name = "shardedJedis")
    private ShardedJedis shardedJedis;

    /**
     * Redis统计visit中短url被访问的字数
     * @param joinPoint 切点
     * @return 切点返回值
     */
    @Around("execution(* pers.sherry.controller.ShortUrlController.visit(..)))")
    public Object aroundVisit(ProceedingJoinPoint joinPoint) {
        Object result;
        try {
            String shortUrl = (String) Optional.ofNullable(joinPoint.getArgs()) //  获取短链接参数
                    .orElseThrow(() -> new RuntimeException("请输入短链接"))[0];
            result = joinPoint.proceed();
            shardedJedis.incr(shortUrl);    //  使用Redis统计短链接被访问次数
            return result;
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable.getMessage());
        }
    }
}
