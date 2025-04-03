package Utils;

import Cache.Cache;
import Router.Middleware;
import Router.Request;
import Router.RequestMethod;
import Router.RouteHandler;
import Router.RouteResult;
import Utils.JwtUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AuthMiddleware implements Middleware {
    private Cache cache;

    public AuthMiddleware(Cache cache) {
        this.cache = cache;
    }

    @Override
    public RouteResult apply(RouteHandler handler, RequestMethod method, Request request) {
        String token = request.getParam("token");
        String username = (String) cache.get(token);

        if (token == null || username == null ||!JwtUtil.validateToken(token, username) || JwtUtil.isTokenExpired(token)) {
            // 返回详细错误码和信息
            Map<String, Object> errorData = new HashMap<>();
            if (token == null) {
                errorData.put("errorCode", 1001);
                errorData.put("message", "缺少令牌");
            } else if (username == null) {
                errorData.put("errorCode", 1002);
                errorData.put("message", "缓存中未找到用户信息");
            } else if (!JwtUtil.validateToken(token, username)) {
                errorData.put("errorCode", 1003);
                errorData.put("message", "令牌无效");
            } else if (JwtUtil.isTokenExpired(token)) {
                errorData.put("errorCode", 1005);
                errorData.put("message", "令牌已过期");
            }
            return new RouteResult(Router.HttpStatus.UNAUTHORIZED, errorData);
        }

        return handler.handle(method, request);
    }
}
