package Router;

import java.util.Map;

public class RouteResult {
    private int statusCode;
    private Map<String, Object> data;

    public RouteResult(int statusCode, Map<String, Object> data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
