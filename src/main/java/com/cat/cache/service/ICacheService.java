package com.cat.cache.service;

public interface ICacheService {

    Object get(String key);

    void put(String key, Object value);

}
