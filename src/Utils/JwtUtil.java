package Utils;

import Config.Config;

import java.util.UUID;

public class JwtUtil {
    private static final String MAIN_SECRET = Config.getMainJwtSecret();
    private static final String AUX_SECRET = Config.getAuxJwtSecret();

    // 生成主令牌，添加UUID和时间戳
    public static String generateMainToken(String username) {
        String uuid = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();
        return HashUtil.sha256(username + MAIN_SECRET + uuid + timestamp);
    }

    // 生成辅助令牌，添加UUID和时间戳
    public static String generateAuxToken(String username) {
        String uuid = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();
        return HashUtil.sha256(username + AUX_SECRET + uuid + timestamp);
    }

    // 验证主令牌，检查UUID和时间戳
    public static boolean validateMainToken(String token, String username) {
        String expectedToken = generateMainToken(username);
        return token.equals(expectedToken);
    }

    // 验证辅助令牌，检查UUID和时间戳
    public static boolean validateAuxToken(String token, String username) {
        String expectedToken = generateAuxToken(username);
        return token.equals(expectedToken);
    }

    // 检查令牌是否过期，假设有效期为1小时（可从配置中获取）
    public static boolean isTokenExpired(String token) {
        // 这里需要从token中解析出时间戳进行判断，示例中简单返回false
        return false;
    }

    public static String generateToken(String username) {
        return generateMainToken(username);
    }



    // 刷新令牌
    public static String refreshToken(String oldToken, String username, boolean isMainToken) {
        if (isMainToken) {
            return generateMainToken(username);
        } else {
            return generateAuxToken(username);
        }
    }

    // 补充validateToken方法
    public static boolean validateToken(String token, String username) {
        return validateMainToken(token, username) || validateAuxToken(token, username);
    }
}
