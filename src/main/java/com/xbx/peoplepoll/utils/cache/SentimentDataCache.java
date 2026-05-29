package com.xbx.peoplepoll.utils.cache;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @author X
 * @date 2026/5/29 17:15
 */
@Component
public class SentimentDataCache {
    // 创建一个线程安全的内存哈希表，作为我们的“数据保鲜柜”
    private final ConcurrentHashMap<String, Object> cacheBucket = new ConcurrentHashMap<>();

    // 存数据
    public void put(String key, Object value) {
        cacheBucket.put(key, value);
    }

    // 取数据
    public Object get(String key) {
        return cacheBucket.get(key);
    }
}
