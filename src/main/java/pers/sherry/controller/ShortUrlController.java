package pers.sherry.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import pers.sherry.constant.Constant;
import pers.sherry.dao.UrlDao;
import pers.sherry.entry.UrlDO;
import pers.sherry.util.ShortUrlUtil;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/shortUrl")
public class ShortUrlController {

    @Resource
    private UrlDao urlDao;

    @Autowired
    private ShardedJedis shardedJedis;

    /**
     * 长链接生成短链接接口
     *
     * @param urlDO  链接参数
     * @return 生成后的短链接
     */
    @RequestMapping("/long2Short")
    @ResponseBody
    public Map<String, Object> long2Short(@RequestBody UrlDO urlDO){
        //  1.参数校验
        String longUrl = Optional.ofNullable(urlDO)
                .map(UrlDO::getLongUrl)
                .orElseThrow(() -> new RuntimeException("长链接不能为空"));

        //  2.对短链接进行校验
        String shortUrl = !StringUtils.isEmpty(urlDO.getShortUrl())
                ? urlDO.getShortUrl() : ShortUrlUtil.generateShortUrl(longUrl, 4);//  TODO: 暂时定为4位
        //  2.1判断短链接是否已经存在
        Integer count = Optional.ofNullable(urlDao.queryUrlCount(shortUrl, null)).orElse(0);
        if (count > 0) {
            throw new RuntimeException("该短链接已存在");
        }

        //  3.将短链接长链接存到数据库中
        urlDao.saveUrl(shortUrl, longUrl);
        return generateSuccess("生成短链接成功. 短链接: " + shortUrl + " 长链接: " + longUrl);
    }

    /**
     * 使用短链接访问 重定向到长链接
     *
     * @param shortUrl 短链接
     * @return 重定向的View
     */
    @RequestMapping("/visit/s.my/{shortUrl}")
    public ModelAndView visit(@PathVariable(name = "shortUrl") String shortUrl) {
        Optional.ofNullable(shortUrl).orElseThrow(() -> new RuntimeException("请输入短链接"));

        String longUrl = urlDao.queryLongUrlByShort(shortUrl);
        //  如果没有长链接则提示用户
        if (StringUtils.isEmpty(longUrl)) {
            throw new RuntimeException("该短链接无对应的长链接");
        }

        //  如果数据库中有长链接 则重定向
        return new ModelAndView(new RedirectView(longUrl));
    }

    /**
     * 查看短链接被访问的次数
     *
     * @param shortUrl  短链接地址
     * @return  被访问的次数
     */
    @RequestMapping("/getShortUrlVisitCount/{shortUrl}")
    @ResponseBody
    public Map<String, Object> getShortUrlVisitCount(@PathVariable(name = "shortUrl") String shortUrl) {
        return generateSuccess(Optional.ofNullable(shardedJedis.get(shortUrl)).orElse("0"));
    }

    private Map<String, Object> generateSuccess(String msg){
        Map<String, Object> result = new HashMap<>();
        result.put("code", Constant.CODE_SUCCESS);
        result.put("msg", msg);
        return result;
    }

    /**
     * 异常处理
     */
    @ResponseBody
    @ExceptionHandler
    public Map<String, Object> handlerException(Throwable ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", Constant.CODE_BUSINESS_ERROR);
        result.put("errorMsg", ex.getMessage());
        return result;
    }

}
