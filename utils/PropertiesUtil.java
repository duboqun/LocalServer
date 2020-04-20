package earthview.ne.localserver.utils;

import android.util.Log;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
    private static Properties properties;
    private static String TAG = "PropertiesUtil";

    static{
        properties = new Properties();
        try {
            properties.load(PropertiesUtil.class.getResourceAsStream("/assets/imageserver.properties"));
        } catch (IOException e) {
            Log.e(TAG, "加载本地配置文件异常！", e);
        }
    }

    public static String getProperty(String  propertyName){
        String propertiesValue = properties.getProperty(propertyName);

        return propertiesValue;
    }
}
