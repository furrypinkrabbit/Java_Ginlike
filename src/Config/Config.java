package Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static Properties properties = new Properties();

    static {
        try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getMainJwtSecret() {
        return getProperty("main_jwt_secret");
    }

    public static String getAuxJwtSecret() {
        return getProperty("aux_jwt_secret");
    }

    // 新增获取令牌有效期的方法（单位：秒）
    public static int getJwtExpirationTime() {
        String expirationTimeStr = getProperty("jwt_expiration_time");
        return expirationTimeStr != null? Integer.parseInt(expirationTimeStr) : 3600;
    }
}