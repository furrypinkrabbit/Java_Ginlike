package Router;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private Map<String, String> params;
    private Map<String, String> cookies;
    private Map<String, InputStream> fileInputs;

    public Request() {
        this.params = new HashMap<>();
        this.cookies = new HashMap<>();
        this.fileInputs = new HashMap<>();
    }

    public String getParam(String key) {
        return params.get(key);
    }

    public InputStream getFileInputStream(String key) {
        return fileInputs.get(key);
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }

    public void setParam(String key, String value) {
        params.put(key, value);
    }

    public void setCookie(String key, String value) {
        cookies.put(key, value);
    }

    public void setFileInputStream(String key, InputStream inputStream) {
        fileInputs.put(key, inputStream);
    }
}
