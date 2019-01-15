package pers.sherry.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
    @RequestMapping(value = "/long2Short", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> long2Short(@RequestBody UrlDO urlDO){
        //  1.参数校验
        String longUrl = Optional.ofNullable(urlDO)
                .map(UrlDO::getLongUrl)
                .orElseThrow(() -> new RuntimeException("长链接不能为空"));

        //  2.对短链接进行校验
        String shortUrl = !StringUtils.isEmpty(urlDO.getShortUrl())
                ? urlDO.getShortUrl() : ShortUrlUtil.generateShortUrl(longUrl, urlDao.getShortUrlConfigLength());
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
    @RequestMapping(value = "/visit/s.my/{shortUrl}", method = RequestMethod.GET)
    public ModelAndView visit(@PathVariable(name = "shortUrl") String shortUrl) throws Exception{
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
    @RequestMapping(value = "/getShortUrlVisitCount/{shortUrl}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getShortUrlVisitCount(@PathVariable(name = "shortUrl") String shortUrl) {
        return generateSuccess(Optional.ofNullable(shardedJedis.get(shortUrl)).orElse("0"));
    }


    /**
     * 配置生成短链接长度
     * @param length    需要配置的长度
     * @return 是否成功
     */
    @RequestMapping(value = "/configShortUrlLength/{length}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> configShortUrlLength(@PathVariable(name = "length") Integer length){
        ShortUrlUtil.checkShortLength(length);
        urlDao.updateConfig(Constant.CONFIG_KEY_SHORT_LEN, String.valueOf(length));
        return generateSuccess("更新成功. 新生成的短链接长度为:" + length);
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
    @ExceptionHandler(value = Throwable.class)
    public Map<String, Object> handlerException(Throwable ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", Constant.CODE_BUSINESS_ERROR);
        result.put("errorMsg", ex.getMessage());
        return result;
    }

}
