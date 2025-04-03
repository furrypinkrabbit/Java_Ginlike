import Cache.Cache;
import Cache.Database;
import Controllers.AuthController;
import Controllers.ArticleController;
import Controllers.FileUploadController;
import Controllers.SessionController;
import Router.*;
import Utils.AuthMiddleware;
import Utils.JwtUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // 初始化数据库和缓存
        Database database = new Database();
        Cache cache = new Cache();

        // 初始化控制器
        AuthController authController = new AuthController(database, cache);
        ArticleController articleController = new ArticleController(database, cache);
        FileUploadController fileUploadController = new FileUploadController();
        SessionController sessionController = new SessionController(new SessionManager());

        // 初始化路由树
        RadixTreeNode root = new RadixTreeNode("");

        // 设置路由处理器
        root.setHandler(RequestMethod.POST, (method, request) -> {
            String path = request.getParam("path");
            if (path.equals("/login")) {
                String username = request.getParam("username");
                String password = request.getParam("password");
                return authController.login(username, password);
            } else if (path.equals("/register")) {
                String username = request.getParam("username");
                String password = request.getParam("password");
                return authController.register(username, password);
            } else if (path.equals("/uploadArticle")) {
                return articleController.uploadArticle(method, request);
            } else if (path.equals("/uploadFile")) {
                return fileUploadController.handleFileUpload(method, request);
            } else if (path.equals("/session")) {
                return sessionController.handleSessionRequest(method, request);
            }
            return new RouteResult(HttpStatus.NOT_FOUND, new HashMap<>());
        });

        root.setHandler(RequestMethod.GET, (method, request) -> {
            String path = request.getParam("path");
            if (path.equals("/browseArticles")) {
                return articleController.browseArticles(method, request);
            }
            return new RouteResult(HttpStatus.NOT_FOUND, new HashMap<>());
        });

        // 初始化认证中间件
        AuthMiddleware authMiddleware = new AuthMiddleware(cache);

        // 模拟注册请求
        Request registerRequest = new Request();
        registerRequest.setParam("path", "/register");
        registerRequest.setParam("username", "newuser");
        registerRequest.setParam("password", "newpassword");
        // 修改调用方式，传递 Request 对象
        RouteResult registerResult = authMiddleware.apply((method, req) -> root.executeRoute(method, req), RequestMethod.POST, registerRequest);
        System.out.println("Register Response Status Code: " + registerResult.getStatusCode());
        System.out.println("Register Response Data: " + registerResult.getData());

        // 模拟登录请求
        Request loginRequest = new Request();
        loginRequest.setParam("path", "/login");
        loginRequest.setParam("username", "newuser");
        loginRequest.setParam("password", "newpassword");
        // 修改调用方式，传递 Request 对象
        RouteResult loginResult = authMiddleware.apply((method, req) -> root.executeRoute(method, req), RequestMethod.POST, loginRequest);
        System.out.println("Login Response Status Code: " + loginResult.getStatusCode());
        System.out.println("Login Response Data: " + loginResult.getData());

        // 模拟上传文章请求
        Request uploadArticleRequest = new Request();
        uploadArticleRequest.setParam("path", "/uploadArticle");
        uploadArticleRequest.setParam("token", (String) loginResult.getData().get("token"));
        uploadArticleRequest.setParam("article", "This is a test article.");
        // 修改调用方式，传递 Request 对象
        RouteResult uploadArticleResult = authMiddleware.apply((method, req) -> root.executeRoute(method, req), RequestMethod.POST, uploadArticleRequest);
        System.out.println("Upload Article Response Status Code: " + uploadArticleResult.getStatusCode());
        System.out.println("Upload Article Response Data: " + uploadArticleResult.getData());

        // 模拟浏览文章请求
        Request browseArticlesRequest = new Request();
        browseArticlesRequest.setParam("path", "/browseArticles");
        browseArticlesRequest.setParam("token", (String) loginResult.getData().get("token"));
        // 修改调用方式，传递 Request 对象
        RouteResult browseArticlesResult = authMiddleware.apply((method, req) -> root.executeRoute(method, req), RequestMethod.GET, browseArticlesRequest);
        System.out.println("Browse Articles Response Status Code: " + browseArticlesResult.getStatusCode());
        System.out.println("Browse Articles Response Data: " + browseArticlesResult.getData());

        // 模拟上传文件请求
        Request uploadFileRequest = new Request();
        uploadFileRequest.setParam("path", "/uploadFile");
        InputStream fileInputStream = new ByteArrayInputStream("Test file content".getBytes());
        uploadFileRequest.setFileInputStream("file", fileInputStream);
        uploadFileRequest.setParam("fileName", "test.txt");
        // 修改调用方式，传递 Request 对象
        RouteResult uploadFileResult = authMiddleware.apply((method, req) -> root.executeRoute(method, req), RequestMethod.POST, uploadFileRequest);
        System.out.println("Upload File Response Status Code: " + uploadFileResult.getStatusCode());
        System.out.println("Upload File Response Data: " + uploadFileResult.getData());

        // 模拟会话操作请求
        Request sessionRequest = new Request();
        sessionRequest.setParam("path", "/session");
        // 修改调用方式，传递 Request 对象
        RouteResult sessionResult = authMiddleware.apply((method, req) -> root.executeRoute(method, req), RequestMethod.POST, sessionRequest);
        System.out.println("Session Response Status Code: " + sessionResult.getStatusCode());
        System.out.println("Session Response Data: " + sessionResult.getData());

        // 关闭数据库连接池
        database.close();
    }
}
