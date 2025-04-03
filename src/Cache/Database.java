package Cache;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/your_database_name";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";

    private static final int POOL_SIZE = 10;
    private ExecutorService connectionPool = Executors.newFixedThreadPool(POOL_SIZE);
    private Lock lock = new ReentrantLock();

    public Connection getConnection() {
        try {
            lock.lock();
            return connectionPool.submit(() -> {
                try {
                    return DriverManager.getConnection(URL, USER, PASSWORD);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock();
        }
    }

    public void close() {
        connectionPool.shutdown();
        try {
            if (!connectionPool.awaitTermination(60, TimeUnit.SECONDS)) {
                connectionPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            connectionPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}