package pers.sherry.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import pers.sherry.dao.UrlDao;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 短链接访问Controller
 */
@Controller
public class VisitController {

    @Resource
    private UrlDao urlDao;

    /**
     * 使用短链接访问 重定向到长链接
     *
     * @param shortUrl 短链接
     * @return 重定向的View
     */
    @RequestMapping("/s.my/{shortUrl}")
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
}
