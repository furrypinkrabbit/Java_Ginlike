package Controllers;

import Cache.Cache;
import Cache.Database;
import Router.HttpStatus;
import Router.Request;
import Router.RequestMethod;
import Router.RouteResult;
import Utils.JwtUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class ArticleController {
    private Database database;
    private Cache cache;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    public ArticleController(Database database, Cache cache) {
        this.database = database;
        this.cache = cache;
    }

    public RouteResult uploadArticle(RequestMethod method, Request request) {
        String token = request.getParam("token");
        String article = request.getParam("article");
        if (!JwtUtil.validateToken(token, cache.get(token))) {
            return new RouteResult(HttpStatus.UNAUTHORIZED, Collections.singletonMap("message", "无效的令牌"));
        }

        FutureTask<Void> futureTask = new FutureTask<>(() -> {
            try (Connection connection = database.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO articles (id, content) VALUES (?,?)")) {
                String articleId = UUID.randomUUID().toString();
                statement.setString(1, articleId);
                statement.setString(2, article);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });

        executorService.submit(futureTask);

        try {
            futureTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new RouteResult(HttpStatus.INTERNAL_SERVER_ERROR, Collections.singletonMap("message", "文章上传失败"));
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("message", "文章上传成功，正在处理中");
        return new RouteResult(HttpStatus.OK, responseData);
    }

    public RouteResult browseArticles(RequestMethod method, Request request) {
        String token = request.getParam("token");
        if (!JwtUtil.validateToken(token, cache.get(token))) {
            return new RouteResult(HttpStatus.UNAUTHORIZED, Collections.singletonMap("message", "无效的令牌"));
        }

        FutureTask<Void> futureTask = new FutureTask<>(() -> {
            try (Connection connection = database.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT content FROM articles")) {
                java.sql.ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String article = resultSet.getString("content");
                    System.out.println("浏览文章: " + article);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });

        executorService.submit(futureTask);

        try {
            futureTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("message", "正在异步加载文章列表");
        return new RouteResult(HttpStatus.OK, responseData);
    }
}