package Controllers;

import Router.Request;
import Router.RequestMethod;
import Router.RouteResult;
import Router.SessionManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SessionController {
    private SessionManager sessionManager;

    public SessionController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public RouteResult handleSessionRequest(RequestMethod method, Request request) {
        String sessionId = request.getCookie("sessionId");
        if (sessionId == null) {
            sessionId = sessionManager.createSession();
            // 这里可以添加设置Cookie的逻辑，例如使用HttpServletResponse
        }

        Map<String, Object> session = sessionManager.getSession(sessionId);
        if (session == null) {
            session = new HashMap<>();
        }

        sessionManager.setSessionAttribute(sessionId, "exampleAttribute", "exampleValue");

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("message", "会话操作成功");
        return new RouteResult(Router.HttpStatus.OK, responseData);
    }
}