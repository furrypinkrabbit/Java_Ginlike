package Controllers;

import Cache.Cache;
import Cache.Database;
import Router.RouteResult;
import Utils.HashUtil;
import Utils.JwtUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AuthController {
    private Database database;
    private Cache cache;

    public AuthController(Database database, Cache cache) {
        this.database = database;
        this.cache = cache;
    }

    public RouteResult login(String username, String password) {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT password FROM users WHERE username =?")) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                if (storedPassword.equals(HashUtil.sha256(password))) {
                    String token = JwtUtil.generateToken(username);
                    // 使用 Cache 类的 set 方法替代 Jedis
                    cache.set(token, username, 3600); // 假设缓存有效期为 1 小时
                    Map<String, Object> responseData = new HashMap<>();
                    responseData.put("message", "登录成功");
                    responseData.put("token", token);
                    return new RouteResult(Router.HttpStatus.OK, responseData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new RouteResult(Router.HttpStatus.UNAUTHORIZED, Collections.singletonMap("message", "用户名或密码错误"));
    }

    public RouteResult register(String username, String password) {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?,?)")) {
            statement.setString(1, username);
            statement.setString(2, HashUtil.sha256(password));
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("message", "注册成功");
                return new RouteResult(Router.HttpStatus.CREATED, responseData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new RouteResult(Router.HttpStatus.BAD_REQUEST, Collections.singletonMap("message", "用户名已存在或其他错误"));
    }
}
