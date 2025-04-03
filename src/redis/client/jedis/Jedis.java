package redis.client.jedis;

import java.util.HashMap;
import java.util.Map;

public class Jedis {
    private Map<String, String> data = new HashMap<>();

    public void set(String key, String value) {
        data.put(key, value);
    }

    public String get(String key) {
        return data.get(key);
    }

    public void close() {
        // 实际中需要关闭连接等操作
    }
}
