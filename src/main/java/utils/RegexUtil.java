package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ClassName:otherUtil.RegexUtil
 * Package:PACKAGE_NAME
 * Description:正则相关的util工具
 *
 * @Author: thechen
 * @Create: 2023/7/21 - 9:28
 */
public class RegexUtil {


    /**
     *
     * @param input  要匹配的字符串
     * @param regex  要匹配的正则规则
     * @return
     */
    public static Matcher match(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input);
    }
}