package com.cat.cache.service.impl;

import com.cat.cache.domain.CacheObject;
import com.cat.cache.service.ICacheService;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CacheServiceImpl implements ICacheService {

    public L2CacheServiceImpl l2CacheServiceImpl = new L2CacheServiceImpl();
    public static final long cleanup = 1;
    public final long timeToLive = 120000; //Time out set for 2 minutes.
    public static ConcurrentHashMap<String, CacheObject> cacheMap = new ConcurrentHashMap<String, CacheObject>();


    /**
     * This function looks for the value of the key in L1 cache first and if not present in L1 cache
     * then goes to in memory database(L2 Cache) to retrieve value.
     *
     * If the value is present, returns the value and also sets it in L1 Cache for further use or returns an error
     * saying the value is not present for the key.
     * @param key
     * @return the value for the key
     */
    @Override
    public Object get(String key) {
        if(key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Invalid key = " + key);
        }

        Object value = Optional.ofNullable(cacheMap.get(key))
                        .filter(cacheObject -> cacheObject.isExpired())
                        .map(CacheObject::getValue).orElse(null);

        if(value == null) {
            value = l2CacheServiceImpl.get(key);

            //Now that value is fetched from L2, its put in L1 for further use.
            //The time is also reset for the key.
            cacheMap.put(key, new CacheObject(value, System.currentTimeMillis() + timeToLive));
        }
        return value;
    }

    /**
     * This function persists the key value pair in a Concurrent HashMap serving as L1 cache,and also in memory database
     * serving as a L2 Cache.
     *
     * @param key
     * @param value
     */
    @Override
    public void put(String key, Object value) {
        if(key == null){
            return;
        }
        if(value == null && cacheMap.containsKey(key)){
            cacheMap.remove(key);
        } else {
            long ttl = System.currentTimeMillis() + timeToLive;
            //Put in L1 Cache
            cacheMap.put(key, new CacheObject(value, ttl));

            //Also putting it in L2 Cache
            l2CacheServiceImpl.put(key, new CacheObject(value, 0));
        }
    }

    /**
     * This function is used for cleanup and whenever called, clears up all expired items from the cache.
     */
    public static void run() {
        Thread cleanerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(cleanup);
                    cacheMap.entrySet()
                            .removeIf(entry -> Optional.ofNullable(entry.getValue())
                                    .map(CacheObject::isExpired).orElse(false));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        cleanerThread.setDaemon(true);
        cleanerThread.start();
    }

}
