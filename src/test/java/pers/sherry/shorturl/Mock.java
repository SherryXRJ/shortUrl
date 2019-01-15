package pers.sherry.shorturl;

public interface Mock {

    /**
     * JSON
     * longUrl = https://www.xiguacity.cn
     */
    String LONG_URL = "{\n" +
            "\t\"longUrl\" : \"https://www.xiguacity.cn\"\n" +
            "}";

    /**
     * Json
     * longUrl = http://www.baidu.com
     * shortUrl = 1234
     */
    String LONG_AND_SHORT = "{\n" +
            "\t\"longUrl\" : \"http://www.baidu.com\",\n" +
            "\t\"shortUrl\": \"1234\"\n" +
            "}";

}
