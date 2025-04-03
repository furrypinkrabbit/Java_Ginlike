package Router;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RadixTreeNode {
    private String pathSegment;
    private Map<RequestMethod, RouteHandler> handlers;
    private Map<String, RadixTreeNode> children;
    private Map<String, RadixTreeNode> lruCache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, RadixTreeNode> eldest) {
            return size() > 10;
        }
    };

    public RadixTreeNode(String pathSegment) {
        this.pathSegment = pathSegment;
        this.handlers = new java.util.EnumMap<>(RequestMethod.class);
        this.children = new HashMap<>();
    }

    public void setHandler(RequestMethod method, RouteHandler handler) {
        handlers.put(method, handler);
    }

    public RouteHandler getHandler(RequestMethod method) {
        return handlers.get(method);
    }

    public void addChild(String key, RadixTreeNode node) {
        children.put(key, node);
        lruCache.put(key, node);
    }

    public RadixTreeNode getChild(String key) {
        RadixTreeNode child = lruCache.get(key);
        if (child == null) {
            child = children.get(key);
            if (child != null) {
                lruCache.put(key, child);
            }
        }
        return child;
    }

    // 修改 executeRoute 方法，接收 Request 对象
    public RouteResult executeRoute(RequestMethod method, Request request) {
        RouteHandler handler = getHandler(method);
        if (handler != null) {
            return handler.handle(method, request);
        } else {
            System.out.println("未找到匹配的请求方法的路由");
            return new RouteResult(HttpStatus.NOT_FOUND, java.util.Collections.emptyMap());
        }
    }
}
