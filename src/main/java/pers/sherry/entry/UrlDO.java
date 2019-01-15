package pers.sherry.entry;

/**
 * url表实体类
 */
public class UrlDO {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 短链接
     */
    private String shortUrl;

    /**
     * 长链接
     */
    private String longUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }
}
