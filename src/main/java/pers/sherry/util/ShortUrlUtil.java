package pers.sherry.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;

/**
 * 生成短链接工具类
 */
public class ShortUrlUtil {

    private static final String SHORT_URL_PREFIX = "s.my/";

    private static final String KEY = "key";
    /**
     * 短链接码表62个字符
     */
    private static String[] table = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z", "A", "B", "C", "D",
            "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
            "Y", "Z", "_", "0"};

    /**
     * 短链接算法
     * MD5   => 长32的字符串、32*8 字节
     * 长度   => 根据长度均分
     * *字符集 =>  有容量大小
     *
     * 算法:
     * 1.通过撒盐进行MD5
     * 2.根据指定的短链接长度 将MD5算法的字符串分组
     * 3.将分组好的字符串依次转成16进制数字
     * 4.将16进制数 对 {@link ShortUrlUtil#table}的长度进行取余 => 各个余数组成短链接
     *
     * // TODO:算法改进
     * @param longUrl 需要转换的长链接
     * @param shortUrlLength 短链接长度
     * @return
     */
    public static String generateShortUrl(String longUrl, int shortUrlLength) {
        String md5 = DigestUtils.md5DigestAsHex((KEY + longUrl).getBytes());    //  MD5
        String result = "";
        //  根据指定的短链接长度进行分组
        for (int i = 0; i < shortUrlLength && i * shortUrlLength < md5.length(); i++) {
            System.out.println(i);
            String s = md5.substring(i * shortUrlLength,
                    (i + 1) * shortUrlLength < md5.length() ? (i + 1) * shortUrlLength : md5.length()); //  分组后的字符串
            long l = Long.valueOf(s, 16) % table.length;//  转成16进制 % table.length
            result += table[(int) l];
        }
        return result;
    }

    public static void test(String longUrl, int length){
        String md5 = DigestUtils.md5DigestAsHex((KEY + longUrl).getBytes());    //  MD5
        byte[] bytes;
        for (int i = 0; i < md5.length(); i++) {
            Long v = Long.valueOf(String.valueOf(md5.charAt(i)), 16);
            System.out.println(v.byteValue() << i * 4);
            bytes = new byte[]{
                    (byte) (v & 0x8),
                    (byte) (v & 0x4),
                    (byte) (v & 0x2),
                    (byte) (v & 0x1)
            };
            System.out.println(bytes);
        }
    }

    public static void main(String[] args) {
        ShortUrlUtil.test("http://www.baidu.com", 7);
    }
}
