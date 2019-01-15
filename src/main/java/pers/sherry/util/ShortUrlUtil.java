package pers.sherry.util;

import org.springframework.util.DigestUtils;

/**
 * 生成短链接工具类
 */
public class ShortUrlUtil {

    private static final String SHORT_URL_PREFIX = "s.my/";

    private static final String KEY = "key";
    /**
     * 短链接码表64个字符 对应八进制 077 -> 6位 111111
     */
    private static String[] table = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z", "A", "B", "C", "D",
            "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
            "Y", "Z", "_", "!"};

    /**
     * 短链接算法
     * 算法:
     * 1.通过撒盐进行MD5
     * 2.根据指定的短链接长度 将MD5算法的字符串分组
     * 3.将分组好的字符串依次转成16进制的byte[]
     * 4.取byte[]数组中的6位 取length个长度
     * 5.将数字 对 {@link ShortUrlUtil#table}的长度进行取余 => 各个余数组成短链接
     *
     * 最小长度1: 最大长度:21
     * @param longUrl   需要转换的长链接
     * @param length    短链接长度
     * @return 短链接
     */
    public static String generateShortUrl(String longUrl, int length) {
        checkShortLength(length);
        String md5 = DigestUtils.md5DigestAsHex((KEY + longUrl).getBytes());    //  MD5
        byte[] bytes = new byte[128];
        for (int i = 0; i < md5.length(); i++) {
            Integer v = Integer.valueOf(String.valueOf(md5.charAt(i)), 16);
            byte[] b = new byte[]{
                    (byte) ((v >> 3) & 0x1),    //  取16进制第4位
                    (byte) ((v >> 2) & 0x1),    //  取16进制第3位
                    (byte) ((v >> 1) & 0x1),    //  取16进制第2位
                    (byte) (v & 0x1)            //  取16进制第1位
            };
            System.arraycopy(b, 0, bytes, i * 4, b.length);
        }

        //  以length为长度 依次取出byte数组6位 转成整型 再对table取余
        String result = "";
        byte[] bs;
        for (int i = 0; i < length; i++) {
            bs = new byte[6];
            System.arraycopy(bytes, i * bs.length, bs, 0, bs.length);
            int n = 0;
            for (int j = bs.length - 1; j >= 0; j--) {
                n |= (bs[j]) << j;
            }
            result += table[n % table.length];
        }
        return result;
    }

    /**
     * 短链接长度检查
     * 0 < len <= 21
     * @param len 短链接长度
     */
    public static void checkShortLength(int len) {
        if (len <= 0 || len > 21) {
            throw new RuntimeException("短链接长度: 0 < length <= 21");
        }
    }

}
