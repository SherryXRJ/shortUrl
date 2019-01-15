package pers.sherry.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlDao {

    /**
     * 查询匹配的链接数量
     * @param shortUrl  短链接
     * @param longUrl   长链接
     * @return  数量
     */
    Integer queryUrlCount(@Param("shortUrl") String shortUrl, @Param("longUrl") String longUrl);

    /**
     * 保存长链接与短链接
     * @param shortUrl  短链接
     * @param longUrl   长链接
     */
    void saveUrl(@Param("shortUrl") String shortUrl, @Param("longUrl") String longUrl);

    /**
     * 根据短链接查询长链接
     * @param shortUrl  短链接
     * @return  短链接对应的长链接
     */
    String queryLongUrlByShort(String shortUrl);
}
