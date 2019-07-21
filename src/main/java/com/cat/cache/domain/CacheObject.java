package com.cat.cache.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CacheObject {

    public long createdTime;
    public Object value;

    public CacheObject (Object value) {
        this.value = value;
    }

    public CacheObject (Object value, long createdTime) {
        this.value = value;
        this.createdTime = createdTime;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > createdTime;
    }
}
