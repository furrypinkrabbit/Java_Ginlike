package Cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Cache {
    private ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private AtomicInteger hitCount = new AtomicInteger(0);
    private AtomicInteger missCount = new AtomicInteger(0);
    private ReentrantLock lock = new ReentrantLock();

    private static class CacheEntry {
        String value;
        long expirationTime;

        CacheEntry(String value, long expirationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
        }
    }

    public String get(String key) {
        lock.lock();
        try {
            CacheEntry entry = cache.get(key);
            if (entry != null && entry.expirationTime > System.currentTimeMillis()) {
                hitCount.incrementAndGet();
                return entry.value;
            } else {
                cache.remove(key);
                missCount.incrementAndGet();
                return null;
            }
        } finally {
            lock.unlock();
        }
    }

    public void set(String key, String value, int timeoutSeconds) {
        lock.lock();
        try {
            long expirationTime = System.currentTimeMillis() + timeoutSeconds * 1000;
            cache.put(key, new CacheEntry(value, expirationTime));
        } finally {
            lock.unlock();
        }
    }

    public double getHitRate() {
        int total = hitCount.get() + missCount.get();
        if (total == 0) {
            return 0;
        }
        return (double) hitCount.get() / total;
    }
}
