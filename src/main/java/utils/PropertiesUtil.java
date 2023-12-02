package utils;

import beans.SJMXManager;

import java.util.Properties;
import java.util.ResourceBundle;

/**
 * ClassName:PropertiesUtil
 * Package:com.thechen.realtime.utils
 * Description:读取配置文件，将读取到的信息封装为一个Properties
 *
 * @Author: thechen
 * @Create: 2023/4/25 - 21:11
 */
public class PropertiesUtil {
    private static Properties props;
    private static PropertiesUtil propertiesUtil;

    private PropertiesUtil() {
    }

    public static PropertiesUtil getInstance(){
        if (propertiesUtil == null) {
            propertiesUtil = new PropertiesUtil();
        }
        return propertiesUtil;
    }

    public void setProps(Properties props) {
        PropertiesUtil.props = props;
    }

    public static String getProperty(String key){
        return props.getProperty(key);
    }
}
